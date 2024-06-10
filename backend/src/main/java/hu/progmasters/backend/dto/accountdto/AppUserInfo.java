package hu.progmasters.backend.dto.accountdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUserInfo {

    private String email;

    private String userName;

    private String imageUrl;


    public AppUserInfo(String email, String userName) {
        this.email = email;
        this.userName = userName;
    }

}
