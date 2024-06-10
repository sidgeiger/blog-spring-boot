package hu.progmasters.backend.dto.accountdto;

import hu.progmasters.backend.domain.Role;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;

public class AuthenticatedUserInfo {

    private String userName;

    private String password;

    private List<Role> roles;

    public AuthenticatedUserInfo(UserDetails userDetails) {
        this.userName = userDetails.getUsername();
        this.password = userDetails.getPassword();
        this.roles = parseRoles(userDetails);
    }


    private List<Role> parseRoles(UserDetails user) {
        return user.getAuthorities()
                .stream()
                .map(authority -> Role.valueOf(authority.getAuthority()))
                .collect(Collectors.toList());
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public List<Role> getRoles() {
        return roles;
    }
}

