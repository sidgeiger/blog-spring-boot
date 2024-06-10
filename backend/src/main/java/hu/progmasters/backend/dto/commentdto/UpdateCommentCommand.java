package hu.progmasters.backend.dto.commentdto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class UpdateCommentCommand {

    @NotNull(message = "Cannot be null")
    @NotBlank(message = "Cannot be blank")
    @NotEmpty(message = "Cannot be empty")
    @Length(min = 3, message = "Cannot be shorter than 3 characters")
    private String commentText;

}
