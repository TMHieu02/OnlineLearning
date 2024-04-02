package src.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import src.model.Rating;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {
    Iterable<Rating> findByCourseId(int courseId);

    @Query("SELECT AVG(r.Rating) FROM Rating r WHERE r.courseId = :courseId AND (r.isDeleted IS NULL OR r.isDeleted = false)")
    Double calculateOverallRating(@Param("courseId") int courseId);

    @Query("SELECT r.Rating, COUNT(r) FROM Rating r WHERE r.courseId = :courseId AND (r.isDeleted IS NULL OR r.isDeleted = false)  GROUP BY r.Rating")
    List<Object[]> calculateRatingDistribution(@Param("courseId") int courseId);

    @Query("SELECT COUNT(DISTINCT r.userByUserId) FROM Rating r WHERE r.courseId = :courseId")
    Long countDistinctUsersByCourseId(int courseId);

    @Query("SELECT r FROM Rating r WHERE r.userId = :userId AND r.courseId = :courseId AND (r.isDeleted = false OR r.isDeleted IS NULL)")
    List<Rating> findRatingByUserCourse(@Param("userId") int userId, @Param("courseId") int courseId);

    @Query("SELECT AVG(r.Rating) FROM Rating r WHERE r.courseId = :courseId AND (r.isDeleted = false OR r.isDeleted IS NULL)")
    Double calculateAverageRatingByCourseId(@Param("courseId") int courseId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.courseByCourseId.userId = :userId")
    int countTotalRatingByUserId(@Param("userId") int userId);
}
