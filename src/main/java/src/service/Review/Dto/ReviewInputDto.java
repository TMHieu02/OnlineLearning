package src.service.Review.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReviewInputDto {
    @JsonProperty(value = "content", required = true)
    private String content;
}
