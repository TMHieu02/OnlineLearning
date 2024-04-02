package src.service.Orders.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrderCreateDto {
    @JsonProperty(value = "fullname", required = true)
    private String fullname;
    @JsonProperty(value = "email", required = true)
    private String email;
    @JsonProperty(value = "phone", required = true)
    private String phone;
    @JsonProperty(value = "payment_price", required = true)
    private double payment_price;
    @JsonProperty(value = "status", required = true)
    private int status;
    @JsonProperty(value = "userId")
    private int userId;
}
