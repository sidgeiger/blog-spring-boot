package hu.progmasters.backend.dto.accountdto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppUserInfoWithFollowers {

    private String userName;

    private List<String> followerUserNames;

    private int followers;
}
