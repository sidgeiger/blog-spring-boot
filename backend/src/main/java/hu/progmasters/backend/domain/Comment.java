package hu.progmasters.backend.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comment")
@Data
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String commentText;

    private LocalDateTime creationDate;

    @Transient
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] image;

    private String imageUrl;

    @ElementCollection
    private List<Long> likes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "post")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "account")
    private AppUser account;
}
