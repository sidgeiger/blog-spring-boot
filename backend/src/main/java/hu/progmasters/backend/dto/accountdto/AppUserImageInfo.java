package hu.progmasters.backend.dto.accountdto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppUserImageInfo {

    private String email;

    private String userName;

    private String imageUrl;


}
