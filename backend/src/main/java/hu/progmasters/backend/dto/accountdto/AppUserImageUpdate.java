package hu.progmasters.backend.dto.accountdto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class AppUserImageUpdate {

    private MultipartFile image;

    private String imageUrl;

    @NotNull(message = "Cannot be null")
    private Long accountId;
}
