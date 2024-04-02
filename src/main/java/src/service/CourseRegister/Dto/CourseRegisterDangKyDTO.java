package src.service.CourseRegister.Dto;/*
Created on 12/10/2023  1:13 AM 2023

@author: tinh2

ProjectName: OnlineLearning
*/

import lombok.Data;

@Data
public class CourseRegisterDangKyDTO {
    private int user_id;
    private int course_id;
    public Boolean isActive;
}
