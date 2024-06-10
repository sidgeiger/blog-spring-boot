package hu.progmasters.backend.dto.accountdto;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
public class AppUserEmailUpdateCommand {

    @NotNull(message = "Cannot be null")
    @NotEmpty(message = "Cannot be empty")
    @NotBlank(message = "Cannot be blank")
    @Size(max = 255, message = "Max 255 character")
    @Email(message = "Not a valid email address...")
    private String email;
}
