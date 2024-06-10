package hu.progmasters.backend.dto.commentdto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentInfoLikes {

    private List<String> userNames;

    private int likes;
}
