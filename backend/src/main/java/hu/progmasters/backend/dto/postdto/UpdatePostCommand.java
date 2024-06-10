package hu.progmasters.backend.dto.postdto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class UpdatePostCommand {



    @NotNull(message = "Cannot be null")
    @NotBlank
    @NotEmpty(message = "Cannot be empty")
    private String text;


}
