package src.service.CourseRegister.Dto;/*
Created on 12/4/2023  2:10 AM 2023

@author: tinh2

ProjectName: OnlineLearning
*/


import lombok.Data;

@Data
public class RegisterCourseDTO {
    private int courseId;
    private String title;
    private String name;
    private String image;
    private Boolean active;
    private Boolean isActive;
    private Boolean isDeleted;
    private Boolean Deleted;
    private int userId;
}

