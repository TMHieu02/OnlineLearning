package src.service.Video.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VideoInputDto {
    @JsonProperty(value = "video_filepath", required = true)
    private String video_filepath;
    @JsonProperty(value = "description", required = true)
    private String description;
    @JsonProperty(value = "title", required = true)
    private String title;
    @JsonProperty(value = "image", required = true)
    private String image;
}
