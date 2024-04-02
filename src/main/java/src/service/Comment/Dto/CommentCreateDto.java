package src.service.Comment.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommentCreateDto {
    @JsonProperty(value = "content", required = true)
    private String content;
    @JsonProperty(value = "videoId", required = true)
    private int videoId;
    @JsonProperty(value = "userId", required = true)
    private int userId;
    @JsonProperty(value = "parentCommentId", required = true)
    private int parentCommentId;
}
