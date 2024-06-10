package hu.progmasters.backend.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Array;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "post")
@NoArgsConstructor
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "title")
    private String title;


    @Column(name = "create_date")
    private LocalDate create;


    @Column(name = "text")
    private String text;

    @Transient
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] image;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "account")
    private AppUser account;

    @ManyToOne
    @JoinColumn(name = "category")
    private Category category;

    @OneToMany(mappedBy = "post")
    private List<Comment> commentList;

    @ElementCollection
    private List<Long> likes = new ArrayList<>();




    @ManyToMany
    @JoinTable(name = "post_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

}
