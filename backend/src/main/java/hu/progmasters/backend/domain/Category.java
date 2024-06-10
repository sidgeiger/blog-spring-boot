package hu.progmasters.backend.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "category")
@NoArgsConstructor
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String categoryName;

    @Column
    @OneToMany(mappedBy = "category")
    private List<Post> postList;

   /* @ElementCollection
    List<String> categories = new ArrayList<>(Arrays.asList("NSFW", "Hardware", "Software", "Programming", "OS", "DevOps"));*/

}
