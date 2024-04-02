package src.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import src.model.CourseRegister;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRegisterRepository extends JpaRepository<CourseRegister, Integer> {
    long countByCourseIdAndIsActive(int courseId, boolean isActive);
    Optional<CourseRegister> findByUserIdAndCourseId(int userId, int courseId);

    List<CourseRegister> findByUserId(int userId);

    @Query("SELECT COUNT(DISTINCT r.userByUserId) FROM CourseRegister r WHERE r.courseId = :courseId")
    Long countDistinctUsersByCourseId(int courseId);

    @Query("SELECT cr FROM CourseRegister cr WHERE cr.updateAt BETWEEN :startDateTime AND :endDateTime AND cr.isActive = :isActive AND (cr.isDeleted = :isDeleted OR cr.isDeleted IS NULL)")
    List<CourseRegister> findTotalSoldCourse(
            @Param("startDateTime") Date startDateTime,
            @Param("endDateTime") Date endDateTime,
            @Param("isActive") Boolean isActive,
            @Param("isDeleted") Boolean isDeleted
    );

    @Query("SELECT cr FROM CourseRegister cr WHERE cr.isActive = :isActive AND (cr.isDeleted = :isDeleted OR cr.isDeleted IS NULL)")
    List<CourseRegister> findTotalSoldCourseAll(
            @Param("isActive") Boolean isActive,
            @Param("isDeleted") Boolean isDeleted
    );

    @Query("SELECT cr FROM CourseRegister cr WHERE cr.userId = :userId AND cr.courseId = :courseId AND cr.isActive = true AND (cr.isDeleted = false OR cr.isDeleted IS NULL)")
    Optional<CourseRegister> findByUserIdAndCourseIdAndIsActiveAndIsDeletedNot(
            @Param("userId") int userId,
            @Param("courseId") int courseId
    );

    @Query("SELECT COUNT(cr) FROM CourseRegister cr WHERE cr.courseByCourseId.userId = :userId")
    int countTotalRegistrationsByUserId(@Param("userId") int userId);

}
