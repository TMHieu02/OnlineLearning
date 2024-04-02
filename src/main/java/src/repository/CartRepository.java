package src.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import src.model.Cart;
import src.model.CourseRegister;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository  extends JpaRepository<Cart, Integer> {

    @Query("SELECT c FROM Cart c WHERE c.userId = :userId AND c.courseId = :courseId AND (c.isDeleted = false OR c.isDeleted IS NULL)")
    List<Cart> findCart(@Param("userId") int userId, @Param("courseId") int courseId);

    List<Cart> findByUserId(int userId);


    @Query("SELECT cr FROM Cart cr WHERE cr.userId = :userId AND cr.courseId = :courseId AND (cr.isDeleted = false OR cr.isDeleted IS NULL)")
    Optional<Cart> findByUserIdAndCourseIdAndIsActiveAndIsDeletedNot(
            @Param("userId") int userId,
            @Param("courseId") int courseId
    );

    @Query("SELECT cr FROM Cart cr WHERE cr.userId = :userId AND cr.courseId = :courseId AND (cr.isDeleted = false OR cr.isDeleted IS NULL)")
    Optional<Cart> findByCourseIdAndUserId(
            @Param("userId") int userId,
            @Param("courseId") int courseId
    );

    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.userId = :userId AND c.courseId = :courseId")
    void deleteByCourseIdAndUserId(@Param("courseId") int courseId, @Param("userId") int userId);


}
