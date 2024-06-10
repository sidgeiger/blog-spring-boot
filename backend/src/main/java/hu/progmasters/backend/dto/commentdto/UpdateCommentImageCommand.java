package hu.progmasters.backend.dto.commentdto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class UpdateCommentImageCommand {

    private Long accountId;

    //this id  is the postID that the comment written to
    @NotNull(message = "Cannot be null")
    private Long postId;

    private MultipartFile image;

    private String imageUrl;
}
