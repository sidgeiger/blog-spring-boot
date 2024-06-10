package hu.progmasters.backend.repository;

import hu.progmasters.backend.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {


    @Query("SELECT p FROM Post p JOIN p.tags t WHERE LOWER(t.name) IN :tagWords")
    List<Post> findByTagWords(@Param("tagWords") List<String> tagWords);


    @Query("SELECT p FROM Post p ORDER BY SIZE(p.likes) DESC")
    List<Post> findMostLikedPost();
}
