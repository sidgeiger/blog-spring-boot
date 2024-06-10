package hu.progmasters.backend.dto.postdto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostInfoLikes {

    private List<String> userNames;

    private int likes;
}
