package src.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import src.model.Review;
import src.model.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @Query("SELECT r FROM Review r WHERE r.courseId = :courseId AND (r.isDeleted = false OR r.isDeleted IS NULL)")
    List<Review> findByCourseId(@Param("courseId") int courseId);
    List<Review> findByUserId(int userId);
}
