package hu.progmasters.backend.dto.postdto;


import lombok.*;

import javax.persistence.Column;
import java.time.LocalDate;
import java.util.List;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostInfo {

    private String title;

    private LocalDate create;

    private String text;

    private String accountUserName;

    private String imageUrl;

    private int numOfLikes;

    private List<String> tags;

    private String category;
}
