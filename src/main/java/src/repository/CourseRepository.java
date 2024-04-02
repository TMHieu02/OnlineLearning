package src.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import src.model.Course;
import src.service.Course.Dto.CourseInfoDTO;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query("SELECT c FROM Course c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%')) AND (c.active = true OR c.isDeleted = false)")
    List<Course> searchByTitle(@Param("title") String title);

    List<Course> findByCategoryId(int categoryId);
    List<Course> findByUserId(int userId);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.userId = :userId AND (c.isDeleted = false OR c.isDeleted IS NULL)")
    int countByUserId(int userId);

    Page<Course> findAll(Pageable pageable);
    Page<Course> findByCategoryId(int categoryId, Pageable pageable);


}
