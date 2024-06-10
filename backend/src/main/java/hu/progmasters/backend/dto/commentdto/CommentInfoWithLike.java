package hu.progmasters.backend.dto.commentdto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentInfoWithLike {

    private String commentText;

    private LocalDateTime creationDate;

    private int numberOfLikes;
}
