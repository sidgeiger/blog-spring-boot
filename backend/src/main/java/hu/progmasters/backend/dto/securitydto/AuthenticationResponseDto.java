package hu.progmasters.backend.dto.securitydto;

import hu.progmasters.backend.dto.securitydto.AppUserDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponseDto {
    private String accessToken;
    private String refreshToken;
    private AppUserDetails appUserDetails;

}
