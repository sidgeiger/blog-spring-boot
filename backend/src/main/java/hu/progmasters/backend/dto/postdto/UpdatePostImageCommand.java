package hu.progmasters.backend.dto.postdto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class UpdatePostImageCommand {


    private MultipartFile image;

    private String imageUrl;

    @NotNull(message = "Cannot be null")
    private Long accountId;
}
