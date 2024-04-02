package src.service.Course.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CourseUpdateDto extends CourseCreateDto {
    @JsonProperty(value = "isDeleted")
    public Boolean isDeleted  = false;
}
