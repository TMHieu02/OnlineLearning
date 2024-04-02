package src.service.CourseRegister.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CourseRegisterCreateDto {

    private String email;
    private String otp;
    @JsonProperty(value = "courseId", required = true)
    private int courseId;
    @JsonProperty(value = "userId", required = true)
    private int userId;
    @JsonProperty(value = "isActive", required = true)
    private Boolean isActive;
}
