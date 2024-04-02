package src.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import src.model.Category;

import java.util.List;

@Repository
public interface CategoryRepository  extends JpaRepository<Category, Integer> {
    List<Category> findAllByParentCategoryId(int parentCategoryId);
}
