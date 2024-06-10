package hu.progmasters.backend.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import hu.progmasters.backend.domain.AppUser;
import hu.progmasters.backend.domain.Role;
import hu.progmasters.backend.dto.accountdto.*;
import hu.progmasters.backend.dto.securitydto.*;
import hu.progmasters.backend.exceptionhandling.*;
import hu.progmasters.backend.repository.AppUserDetailsRepository;
import hu.progmasters.backend.utils.EmailPrefixSuffixMatcher;
import hu.progmasters.backend.utils.NameFormatter;
import hu.progmasters.backend.utils.PasswordMacther;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
@Slf4j
public class AppUserService implements UserDetailsService {

    private final AppUserDetailsRepository appUserDetailsRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final CloudinaryImageService cloudinaryImageService;

    private final EmailServiceImpl emailService;


    static final String ADMIN = "ROLE_ADMIN";

    static final String INACTIVE = "INACTIVE";

    @Autowired
    public AppUserService(AppUserDetailsRepository appUserDetailsRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, CloudinaryImageService cloudinaryImageService, EmailServiceImpl emailService) {
        this.appUserDetailsRepository = appUserDetailsRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.cloudinaryImageService = cloudinaryImageService;
        this.emailService = emailService;
    }

    public AppUserInfo saveAccount(AppUserCreateCommand command) throws MessagingException, UnsupportedEncodingException {
        AppUser appUser = new AppUser();
        AppUser savedAccount = new AppUser();

        accountNameValidator(command.getUserName());
        command.setFirstName(NameFormatter.upperCaseFormatter(command.getFirstName()));
        command.setLastName(NameFormatter.upperCaseFormatter(command.getLastName()));

        boolean canBeReactivated = emailValidator(command.getEmail());
        if (!canBeReactivated) {
            appUser = modelMapper.map(command, AppUser.class);

        } else {
            appUser = reactivateUser(command);
        }

        cloudinaryImageService.imageControlAppUser(appUser, command);

        savedAccount = appUserDetailsRepository.save(appUser);
        savedAccount.setPassword(passwordEncoder.encode(command.getPassword()));
        savedAccount.setActive(false);


        emailService.sendEmail(savedAccount);

        return modelMapper.map(savedAccount, AppUserInfo.class);

    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser account = appUserDetailsRepository.findByUserName(username);
        if (account == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found int the database");
        } else {
            log.info("User found in the database: {}", username);
        }
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        account.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getRoles())));
        return new User(account.getUserName(), account.getPassword(), authorities);
    }

    boolean emailValidator(String email) {
        boolean isEmailValidated = false;
        if (!EmailPrefixSuffixMatcher.matcher(email)) {
            throw new NotValidEmailException(email);
        }
        boolean emailValidation = searchEmail(email);
        if (emailValidation) {
            isEmailValidated = true;
        }
        return isEmailValidated;
    }

    boolean searchEmail(String email) {
        boolean canBeReactivated = false;
        boolean emailExists = appUserDetailsRepository.findByEmail(email)
                .stream()
                .anyMatch(account -> account.getEmail().equals(email));
        Optional<AppUser> user = appUserDetailsRepository.findByEmail(email);
        if (emailExists && !(user.get().getPassword().equals(INACTIVE))) {
            throw new EmailAlreadyExistsException(email);
        } else if (user.isPresent()) {
            canBeReactivated = true;
        }
        return canBeReactivated;
    }

    private AppUser reactivateUser(AppUserCreateCommand command) {
        Optional<AppUser> appUser = appUserDetailsRepository.findByEmail(command.getEmail());
        appUser.get().setUserName(command.getUserName());
        appUser.get().setPassword(command.getPassword());
        appUser.get().setFirstName(command.getFirstName());
        appUser.get().setLastName(command.getLastName());
        return appUser.get();
    }


    public AppUser findAccountByUserName(String userName) {
        AppUser appUser = appUserDetailsRepository.findByUserName(userName);
        if (appUser.getUserName().isEmpty()) {
            throw new UsernameNotFoundException(userName);
        }
        return appUser;
    }

    public List<AppUserInfo> findAllAppUsers() {
        List<AppUser> allAppUsers = appUserDetailsRepository.findAll();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()).contains(ADMIN)) {
            List<AppUser> activeUsers = new ArrayList<>();
            for (AppUser user : allAppUsers) {
                if (Boolean.TRUE.equals(user.getActive())) {
                    activeUsers.add(user);
                }
            }
            allAppUsers = activeUsers;
        }
        return allAppUsers.stream()
                .map(appUser -> modelMapper.map(appUser, AppUserInfo.class))
                .collect(Collectors.toList());
    }

    public AppUserInfo updateAccount(AppUserUpdateCommand command, Long id) {
        AppUser appUser = findById(id);

        securityCheck(appUser, "You are not allowed to update this account");

        accountNameValidator(command.getUserName());
        emailValidator(command.getEmail());
        accountPasswordValidation(command.getPassword());

        modelMapper.map(command, appUser);

        appUser.setPassword(passwordEncoder.encode(command.getPassword()));

        appUserDetailsRepository.save(appUser);
        return modelMapper.map(appUser, AppUserInfo.class);
    }

    private static void securityCheck(AppUser appUser, String errorMessage) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        if (!authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()).contains(ADMIN)) {
            if (!appUser.getUserName().equals(currentUserName)) {
                throw new UnauthorizedException(errorMessage);
            }
        }
    }

    public AppUserImageInfo updateAccountImage(AppUserImageUpdate command, Long id) {

        AppUser appUser = findById(id);
        MultipartFile imageFile = command.getImage();

        securityCheck(appUser, "You are not allowed to update this image");

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Map imageData = cloudinaryImageService.upload(imageFile);
                appUser.setImageUrl(imageData.get("url").toString());
                appUser.setImage(imageFile.getBytes());
            } catch (IOException e) {
                log.error("Error with the picture upload.", e);
            }
        } else {
            log.warn("Image file is null or empty.");
        }

        AppUser savedAccount = appUserDetailsRepository.save(appUser);
        return modelMapper.map(savedAccount, AppUserImageInfo.class);
    }

    public boolean verifyEmail(String code) throws MessagingException, UnsupportedEncodingException {
        Optional<AppUser> appUserOptional = appUserDetailsRepository.findByVerificationCode(code);
        if (appUserOptional.isEmpty() || appUserOptional.get().getActive()) {
            return false;
        } else {
            appUserOptional.get().setActive(true);
            appUserOptional.get().setRoles(List.of(Role.ROLE_USER));
            appUserOptional.get().setVerificationCode(null);
            emailService.sendSuccessfulRegistration(appUserOptional.get());
            return true;
        }
    }

    private void accountPasswordValidation(String password) {
        boolean isPasswordValid = PasswordMacther.passwordMatcher(password);
        if (!isPasswordValid) {
            throw new PasswordAlreadyExistsException(password);
        }

        boolean passwordExists = appUserDetailsRepository.findAll()
                .stream()
                .anyMatch(account -> account.getPassword().equals(password));

        if (passwordExists) {
            throw new PasswordAlreadyExistsException(password);
        }
    }

    private void accountNameValidator(String userName) {
        boolean nameIsExists = appUserDetailsRepository.findAll()
                .stream()
                .anyMatch(account -> account.getUserName().equals(userName));
        if (nameIsExists) {
            throw new AccountAlreadyExistsException(userName);
        }
    }

    public AppUser findById(Long id) {
        Optional<AppUser> accountOptional = appUserDetailsRepository.findById(id);
        if (accountOptional.isEmpty()) {
            throw new NotFoundAccountById(id);
        }
        return accountOptional.get();
    }

    public void deleteAccountById(Long id) throws MessagingException, UnsupportedEncodingException {
        AppUser appUser = findById(id);

        securityCheck(appUser, "You are not allowed to delete this account");
        emailService.deletedAccountNotification(appUser);
        appUser.setActive(false);
        appUser.setUserName(INACTIVE);
        appUser.setPassword(INACTIVE);
        appUser.setImageUrl(INACTIVE);

    }

    public AppUserInfo updateEmail(AppUserEmailUpdateCommand command, Long id) {
        AppUser appUser = findById(id);

        securityCheck(appUser, "You are not allowed to update this account");

        emailValidator(command.getEmail());
        appUser.setEmail(command.getEmail());
        return modelMapper.map(appUser, AppUserInfo.class);
    }

    public AppUserInfo updateUserName(Long id, AppUserUserNameUpdateCommand command) {
        AppUser appUser = findById(id);

        securityCheck(appUser, "You are not allowed to update this account");


        accountNameValidator(command.getUserName());
        appUser.setUserName(command.getUserName());
        return modelMapper.map(appUser, AppUserInfo.class);
    }

    public AppUserInfo updateUserPassword(AppUserUpdatePasswordCommand command, Long id) {
        AppUser appUser = findById(id);

        securityCheck(appUser, "You are not allowed to update this account");


        accountPasswordValidation(command.getPassword());
        appUser.setPassword(command.getPassword());

        appUser.setPassword(passwordEncoder.encode(command.getPassword()));

        return modelMapper.map(appUser, AppUserInfo.class);
    }


    public AuthenticationResponseDto login(AuthentitacionRequestDto authentitacionRequestDto) {
        AppUser verifiedUser = findAccountByUserName(authentitacionRequestDto.getUserName());
        if (verifiedUser.getVerificationCode() != null) {
            throw new NotVerifiedUserException(verifiedUser.getEmail());
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(authentitacionRequestDto.getUserName(), authentitacionRequestDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);


        User user = (User) authentication.getPrincipal();

        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .withIssuer("http://localhost:8080/api/appusers/login")
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                .withIssuer("http://localhost:8080/api/appusers/login")
                .sign(algorithm);

        return new AuthenticationResponseDto(accessToken, refreshToken, new hu.progmasters.backend.dto.securitydto.AppUserDetails(user));

    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {

        String refreshToken = refreshTokenRequest.getRefreshToken();

        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(refreshToken);
        String username = decodedJWT.getSubject();

        AppUser account = findAccountByUserName(username);
        String accessToken = JWT.create()
                .withSubject(account.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .withIssuer("http://localhost:8080/api/appusers/token/refresh")
                .withClaim("roles", account.getRoles().stream().map(Role::getRoles).collect(Collectors.toList()))
                .sign(algorithm);

        return new RefreshTokenResponse(accessToken);
    }


    public AppUserInfoWithFollowers follow(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        AppUser loggedAppUser = findAccountByUserName(currentUserName);
        AppUser appUserToFollow = findById(id);

        if (loggedAppUser.getFollowings().contains(id)) {
            throw new AlreadyFollowedUser(appUserToFollow.getUserName());

        }
        loggedAppUser.getFollowings().add(id);
        appUserToFollow.getFollowers().add(loggedAppUser.getId());

        return getAppUserInfoWithFollowers(appUserToFollow);
    }

    @NotNull
    private AppUserInfoWithFollowers getAppUserInfoWithFollowers(AppUser appUserToFollow) {
        AppUserInfoWithFollowers appUserInfoWithFollowers = new AppUserInfoWithFollowers();
        appUserInfoWithFollowers.setUserName(appUserToFollow.getUserName());
        appUserInfoWithFollowers.setFollowerUserNames(appUserToFollow.getFollowers().stream()
                .map(aLong -> {
                    AppUser appUser = findById(aLong);
                    return appUser.getUserName();
                }).collect(Collectors.toList()));
        appUserInfoWithFollowers.setFollowers(appUserToFollow.getFollowers().size());
        return appUserInfoWithFollowers;
    }

    public AppUserInfoWithFollowers followedUsers(Long id) {
        AppUser appUser = findById(id);
        return getAppUserInfoWithFollowers(appUser);
    }

    public AppUserInfoWithFollowers myFollowers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        AppUser appUser = findAccountByUserName(currentUserName);

        return getAppUserInfoWithFollowers(appUser);
    }

    public List<AppUserInfoWithFollowers> mostFollowedAppuser() {
        List<AppUser> appUsers = appUserDetailsRepository.findMostFollowedAppuser();

        List<AppUser> mostFollowedAppusers = appUsers.stream()
                .filter(appUser -> appUser.getFollowers().size() == appUsers.get(0).getFollowers().size())
                .collect(Collectors.toList());
        return mostFollowedAppusers.stream().map(this::getAppUserInfoWithFollowers).collect(Collectors.toList());
    }

    public void deleteFollow(Long id) {
        AppUser followedAppUser = findById(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        AppUser appUser = findAccountByUserName(currentUserName);

        followedAppUser.getFollowers().remove(appUser.getId());
        appUser.getFollowings().remove(followedAppUser.getId());

    }
}

