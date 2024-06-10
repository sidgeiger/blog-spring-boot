package hu.progmasters.backend.dto.commentdto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CommentCreateCommand {

    //this account the userID who writes the comment
    @NotNull(message = "Cannot be null")
    private Long accountId;

    //this id  is the postID that the comment written to
    @NotNull(message = "Cannot be null")
    private Long postId;


    @Length(min = 3, message = "Cannot be shorter than 3 characters")
    private String commentText;

    private MultipartFile image;

    private String imageUrl;

}
