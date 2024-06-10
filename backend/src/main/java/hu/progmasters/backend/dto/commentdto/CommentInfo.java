package hu.progmasters.backend.dto.commentdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentInfo {

    private String userName;

    private String commentText;

    private LocalDateTime creationDate;

    private Long postId;

    private String imageUrl;

}
