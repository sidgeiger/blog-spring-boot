package hu.progmasters.backend.repository;

import hu.progmasters.backend.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c ORDER BY size(c.likes) DESC")
    List<Comment> findMostLikedComment();
    //JPQL sux
}
