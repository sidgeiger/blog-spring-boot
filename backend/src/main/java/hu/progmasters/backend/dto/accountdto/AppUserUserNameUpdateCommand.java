package hu.progmasters.backend.dto.accountdto;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class AppUserUserNameUpdateCommand {


    @NotNull(message = "Cannot be null")
    @Size(min = 5, max = 50, message = "Minimum 5 characters and max 50")
    private String userName;
}
