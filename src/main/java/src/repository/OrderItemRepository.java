package src.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import src.model.OrderItem;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    @Query("SELECT o FROM OrderItem o WHERE o.courseId = :courseId AND (o.isDeleted = false OR o.isDeleted IS NULL)")
    List<OrderItem> findCourse(@Param("courseId") int courseId);
}
