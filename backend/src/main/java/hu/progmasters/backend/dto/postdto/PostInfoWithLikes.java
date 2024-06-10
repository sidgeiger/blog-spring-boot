package hu.progmasters.backend.dto.postdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostInfoWithLikes {

    private String title;

    private String category;

    private LocalDate create;

    private String text;

    private int numberOfLikes;
}
