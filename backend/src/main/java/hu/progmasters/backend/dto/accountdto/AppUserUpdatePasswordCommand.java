package hu.progmasters.backend.dto.accountdto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
public class AppUserUpdatePasswordCommand {

    @NotEmpty(message = "Cannot be null")
    @NotNull(message = "Cannot be null")
    @NotBlank(message = "Cannot be blank")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&(){}_.])[A-Za-z\\d@$!%*?&(){}_.]{9,22}$",
            message = "Password length must be 9-22, must contain at least 1 uppercase, 1 lowercase, 1 digit and 1 special character ( @$!%*?&(){}_. )!")
    private String password;

}
