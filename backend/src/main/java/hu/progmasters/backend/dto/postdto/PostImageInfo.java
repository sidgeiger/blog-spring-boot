package hu.progmasters.backend.dto.postdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostImageInfo {

    private String title;

    private LocalDate create;

    private String text;

    private String accountUserName;

    private String imageUrl;

    private List<String> tags;

    private String category;
}
