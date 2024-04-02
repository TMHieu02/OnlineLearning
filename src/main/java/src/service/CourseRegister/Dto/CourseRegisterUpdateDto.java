package src.service.CourseRegister.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CourseRegisterUpdateDto extends CourseRegisterCreateDto {
    @JsonProperty(value = "isDeleted")
    public Boolean isDeleted  = false;
}
