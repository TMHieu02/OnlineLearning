package src.service.Rating.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RatingCreateDto {
    @JsonProperty(value = "rating", required = true)
    private int rating;
    @JsonProperty(value = "courseId", required = true)
    private int courseId;
    @JsonProperty(value = "userId", required = true)
    private int userId;
}
