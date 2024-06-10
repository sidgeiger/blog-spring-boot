package hu.progmasters.backend.dto.accountdto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
public class AppUserCreateCommand {

    @NotNull(message = "Cannot be null")
    @NotEmpty(message = "Cannot be empty")
    @NotBlank(message = "Cannot be blank")
    @Size(max = 255, message = "Max 255 character")
    @Email(message = "Not a valid email address...")
    private String email;

    @NotEmpty(message = "Cannot be empty")
    @NotNull(message = "Cannot be null")
    @NotBlank(message = "Cannot be blank")
    @Size(min = 9, max = 22, message = "Minimum 5 characters and max 30")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&(){}_.])[A-Za-z\\d@$!%*?&(){}_.]{9,22}$",
            message = "Password length must be 9-22, must contain at least 1 uppercase, 1 lowercase, 1 digit and 1 special character ( @$!%*?&(){}_ )!")
    private String password;

    @NotNull(message = "Cannot be null")
    @NotEmpty(message = "Cannot be empty")
    @NotBlank(message = "Cannot be blank")
    @Size(min = 3, max = 50, message = "Minimum 3 characters and max 50")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name should have letters only!")
    private String firstName;

    @NotNull(message = "Cannot be null")
    @NotEmpty(message = "Cannot be empty")
    @NotBlank(message = "Cannot be blank")
    @Size(min = 3, max = 50, message = "Minimum 3 characters and max 50")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Last name should have letters only!")
    private String lastName;

    @NotNull(message = "Cannot be null")
    @NotEmpty(message = "Cannot be empty")
    @NotBlank(message = "Cannot be blank")
    @Size(min = 5, max = 20, message = "Minimum 5 characters and max 20")
    private String userName;

    private MultipartFile image;

    private String imageUrl;
}
