package src.service.Feedback.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FeedbackInputDto {
    @JsonProperty(value = "title", required = true)
    private String title;
    @JsonProperty(value = "content", required = true)
    private String content;
    @JsonProperty(value = "image", required = true)
    private String image;
}
