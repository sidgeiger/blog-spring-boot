package hu.progmasters.backend.dto.securitydto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppUserDetails {

    private String userName;

    private List<String> role;

    public AppUserDetails(User user) {
        this.userName = user.getUsername();
        this.role = loadRoles(user.getAuthorities());
    }

    private List<String> loadRoles(Collection<GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

}
