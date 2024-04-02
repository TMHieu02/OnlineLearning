package src.service.Comment.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommentInputDto {
    @JsonProperty(value = "content", required = true)
    private String content;
}
