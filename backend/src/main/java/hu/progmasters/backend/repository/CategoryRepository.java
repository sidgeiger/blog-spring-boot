package hu.progmasters.backend.repository;

import hu.progmasters.backend.domain.Category;
import hu.progmasters.backend.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
