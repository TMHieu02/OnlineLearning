package src.service.Document.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DocumentCreateDto {
    @JsonProperty(value = "file_path", required = true)
    private String file_path;
    @JsonProperty(value = "title", required = true)
    private String title;
    @JsonProperty(value = "image", required = true)
    private String image;
    @JsonProperty(value = "courseId", required = true)
    private int courseId;
}
