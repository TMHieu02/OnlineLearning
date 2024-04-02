package src.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import src.model.Orders;
import src.service.OrderItem.Dto.OrdersWithOrderItemDTO;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    @Query("SELECT o FROM Orders o WHERE o.userId = :userId AND (o.isDeleted = false OR o.isDeleted IS NULL)")
    List<Orders> findUser(@Param("userId") int userId);
    @Query("SELECT o.Id AS orderId, o.fullname, o.email, o.phone, o.payment_price AS paymentPrice, o.Status AS status, o.isDeleted, o.userId, oi.Id AS orderItemId, oi.courseId FROM Orders o JOIN o.orderItemByOrderId oi WHERE o.userId = :userId")
    List<OrdersWithOrderItemDTO> findOrdersWithOrderItemByUserId(@Param("userId") int userId);
}
