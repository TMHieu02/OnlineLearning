package src.service.OrderItem.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrderItemCreateDto {
    @JsonProperty(value = "orderId")
    private int orderId;
    @JsonProperty(value = "courseId")
    private int courseId;
}
