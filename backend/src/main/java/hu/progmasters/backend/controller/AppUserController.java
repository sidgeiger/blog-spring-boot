package hu.progmasters.backend.controller;

import hu.progmasters.backend.dto.securitydto.AuthenticationResponseDto;
import hu.progmasters.backend.dto.securitydto.AuthentitacionRequestDto;
import hu.progmasters.backend.dto.securitydto.RefreshTokenRequest;
import hu.progmasters.backend.dto.securitydto.RefreshTokenResponse;
import hu.progmasters.backend.dto.accountdto.*;
import hu.progmasters.backend.service.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/api/appusers")
@Slf4j
public class AppUserController {

    private final AppUserService appUserService;

    @Autowired
    public AppUserController(AppUserService accountService) {
        this.appUserService = accountService;
    }

    @PostMapping(value ="/register",  consumes = "multipart/form-data")
    public ResponseEntity<AppUserInfo> createAccount(@Valid @ModelAttribute AppUserCreateCommand command) throws MessagingException, UnsupportedEncodingException {
        log.info("Http request POST / /api/appusers with data: " + command.toString());
        AppUserInfo accountInfo = appUserService.saveAccount(command);
        return new ResponseEntity<>(accountInfo, HttpStatus.CREATED);
    }


    @GetMapping("/me")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<UserDetails> getLoggedInUser() {
        log.info("Http request, GET / /appusers/me");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = appUserService.loadUserByUsername((String) authentication.getPrincipal());
        return new ResponseEntity<>(userDetails, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody AuthentitacionRequestDto requestDto) {
        log.info("Http request, POST, / /api/appusers/login: " + requestDto);
        AuthenticationResponseDto responseDto = appUserService.login(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/profile/verify")
    public ResponseEntity<String> verifyRegistration(@Param("code") String code) throws MessagingException, UnsupportedEncodingException {
        log.info("Http request, GET / /users/appusers/verify/{code} with code: " + code);
        boolean isVerify = appUserService.verifyEmail(code);
        String result = isVerify ? "Verification succeeded! Hurray!" : "Verification have not completed yet!";
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/allappuser")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<List<AppUserInfo>> getAllAccount() {
        log.info("Http request, GET / /api/users/findAllUsers");
        List<AppUserInfo> appUserListInfos = appUserService.findAllAppUsers();
        return new ResponseEntity<>(appUserListInfos, HttpStatus.FOUND);
    }


    @PutMapping("/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<AppUserInfo> updateAccount(@PathVariable Long id, @RequestBody @Valid AppUserUpdateCommand command) {
        log.info("Http request PUT / /api/appusers/{id} with id: " + command.toString());
        AppUserInfo updateAccount = appUserService.updateAccount(command, id);
        return new ResponseEntity<>(updateAccount, HttpStatus.OK);
    }

    @PutMapping(value = "/image/{id}", consumes = "multipart/form-data")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<AppUserImageInfo> updateAppUserImage(@PathVariable Long id, @Valid @ModelAttribute AppUserImageUpdate command) {
        log.info("Http request PUT / /api/appusers/image/{id} with id: " + command.toString());
        AppUserImageInfo updateAccountImage = appUserService.updateAccountImage(command, id);
        return new ResponseEntity<>(updateAccountImage, HttpStatus.OK);
    }

    @PutMapping("/username/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<AppUserInfo> updateUserName(@PathVariable Long id, @RequestBody @Valid AppUserUserNameUpdateCommand command) {
        log.info("Http request, PUT / /api/appusers/username/{id} with id: " + id + " + " + command.toString());
        AppUserInfo appUserInfo = appUserService.updateUserName(id, command);
        return new ResponseEntity<>(appUserInfo, HttpStatus.OK);
    }


    @PutMapping("/email/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<AppUserInfo> updateEmail(@PathVariable Long id, @RequestBody @Valid AppUserEmailUpdateCommand command) {
        log.info("Http request, PUT / /api/appusers/email/{id} with id: " + id + " body: " + command.toString());
        AppUserInfo accountInfo = appUserService.updateEmail(command, id);
        return new ResponseEntity<>(accountInfo, HttpStatus.OK);
    }


    @PutMapping("/password/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<AppUserInfo> updateUserPassword(@PathVariable Long id, @RequestBody @Valid AppUserUpdatePasswordCommand command) {
        log.info("Http request, PUT / /api/appusers/password/{id} with id: " + id + " body: " + command.toString());
        AppUserInfo accountInfo = appUserService.updateUserPassword(command, id);
        return new ResponseEntity<>(accountInfo, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<String> deleteAccountById(@PathVariable Long id) throws MessagingException, UnsupportedEncodingException {
        log.info("Http request DELETE / /api/appusers/{id} with id: " + id);
        appUserService.deleteAccountById(id);
        String result = "Deleting account succeeded!";
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshTokenResponse tokenResponse = appUserService.refreshToken(refreshTokenRequest);
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Http request, GET / /api/appusers/logout");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return new ResponseEntity<>("Logout successful", HttpStatus.OK);
    }


    @PostMapping("/follow/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<AppUserInfoWithFollowers> follow(@PathVariable Long id) {
        log.info("Http request POST / /api/appusers/follow/{id} with variable: " + id);
        AppUserInfoWithFollowers appUserInfoWithLikes = appUserService.follow(id);
        return new ResponseEntity<>(appUserInfoWithLikes, HttpStatus.OK);
    }

    @GetMapping("/follow/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<AppUserInfoWithFollowers> followers(@PathVariable Long id) {
        log.info("Http request / /api/appusers/follow/{id} " + id);
        AppUserInfoWithFollowers appUserInfoWithFollowers = appUserService.followedUsers(id);
        return new ResponseEntity<>(appUserInfoWithFollowers, HttpStatus.OK);
    }

    @GetMapping("/follow")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<AppUserInfoWithFollowers> myFollower() {
        log.info("Http request / /api/appusers/follow");
        AppUserInfoWithFollowers appUserInfoWithFollowers = appUserService.myFollowers();
        return new ResponseEntity<>(appUserInfoWithFollowers, HttpStatus.OK);
    }


    @GetMapping("/follow/most")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<List<AppUserInfoWithFollowers>> mostFollowers() {
        log.info("Http request / /api/appusers/follow/most");
        List<AppUserInfoWithFollowers> appUserInfoWithFollowersList = appUserService.mostFollowedAppuser();
        return new ResponseEntity<>(appUserInfoWithFollowersList, HttpStatus.OK);
    }


    @DeleteMapping("/follow/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Void> deleteFollow(@PathVariable Long id) {
        log.info("Http request DELETE / /api/appusers/follow/{id} with variable: " + id);
        appUserService.deleteFollow(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

