package src.service.OrderItem.Dto;/*
Created on 12/4/2023  2:48 AM 2023

@author: tinh2

ProjectName: OnlineLearning
*/

import lombok.Data;


@Data
public class OrdersWithOrderItemDTO {
    private int orderId;
    private String fullname;
    private String email;
    private String phone;
    private double paymentPrice;
    private int status;
    private int userId;
    private int orderItemId;
    private Integer courseId;

    public OrdersWithOrderItemDTO(int orderId, String fullname, String email, String phone, double paymentPrice,
                                  int status, int userId,
                                  int orderItemId, Integer courseId) {
        this.orderId = orderId;
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.paymentPrice = paymentPrice;
        this.status = status;
        this.userId = userId;
        this.orderItemId = orderItemId;
        this.courseId = courseId;
    }
}
