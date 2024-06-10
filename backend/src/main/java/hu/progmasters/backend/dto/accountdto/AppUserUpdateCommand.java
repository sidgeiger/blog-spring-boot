package hu.progmasters.backend.dto.accountdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUserUpdateCommand {

    @NotNull
    @NotEmpty
    @NotBlank
    @Size(max = 255)
    @Email(message = "Not a valid email address...")
    private String email;

    @NotEmpty
    @NotNull
    @NotBlank
    @Size(min = 9, max = 22)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&(){}_.])[A-Za-z\\d@$!%*?&(){}_.]{9,22}$",
            message = "Password length must be 9-22, must contain at least 1 uppercase, 1 lowercase, 1 digit and 1 special character ( @$!%*?&(){}_ )!")
    private String password;

    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 5, max = 20, message = "Minimum 5 characters and max 20")
    private String userName;
}
