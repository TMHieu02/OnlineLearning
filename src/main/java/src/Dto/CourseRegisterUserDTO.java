package src.Dto;

import lombok.Data;

@Data
public class CourseRegisterUserDTO {
    private int userId;
    private Boolean active;
    private int courseId;
    private int usercourseId;
    private Boolean isActive;
}
