package src.service.Video.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Date;

@Data
public class VideoDto extends VideoUpdateDto {
    @JsonProperty(value = "Id", required = true)
    public int Id;
    @JsonProperty(value = "isDeleted")
    public Boolean isDeleted  = false;
    @JsonProperty(value = "createAt", required = true)
    public Date createAt ;
    @JsonProperty(value = "updateAt", required = true)
    public Date updateAt ;
    @JsonProperty(value = "views", required = true)
    private int views;
    @JsonProperty(value = "hours", required = true)
    private int hours;
    @JsonProperty(value = "minutes", required = true)
    private int minutes;
    @JsonProperty(value = "seconds", required = true)
    private int seconds;
}
