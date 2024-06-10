package hu.progmasters.backend.dto.postdto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
public class PostCreateCommand {

    @NotNull(message = "Cannot be null")
    @NotBlank
    @NotEmpty(message = "Cannot be empty")
    @Length(min = 3, message = "Cannot be shorter than 3 characters")
    private String title;

    @NotNull(message = "Cannot be null")
    private Long category;

    @NotNull(message = "Cannot be null")
    @NotBlank
    @NotEmpty(message = "Cannot be empty")
    private String text;


    @NotNull(message = "Cannot be null")
    private String tags;

    private MultipartFile image;

    private String imageUrl;
}
