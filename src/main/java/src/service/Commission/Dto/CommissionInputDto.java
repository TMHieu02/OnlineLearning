package src.service.Commission.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommissionInputDto {
    @JsonProperty(value = "name", required = true)
    private String name;
    @JsonProperty(value = "description", required = true)
    private String description;
    @JsonProperty(value = "cost", required = true)
    private double cost;
}
