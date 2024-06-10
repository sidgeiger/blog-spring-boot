package hu.progmasters.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    @Column(unique = true)
    private String userName;

    private String firstName;

    private String lastName;

    @Transient
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] image;

    private String imageUrl;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    private Boolean active;

    @ElementCollection
    private List<Long> followings = new ArrayList<>();

    @ElementCollection
    private List<Long> followers = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    private List<Post> postList;

    @OneToMany(mappedBy = "account")
    private List<Comment> commentList;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role")
    private List<Role> roles;


}
