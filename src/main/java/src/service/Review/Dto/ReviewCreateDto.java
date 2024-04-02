package src.service.Review.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReviewCreateDto {
    @JsonProperty(value = "content", required = true)
    private String content;
    @JsonProperty(value = "courseId", required = true)
    private int courseId;
    @JsonProperty(value = "userId", required = true)
    private int userId;
}
