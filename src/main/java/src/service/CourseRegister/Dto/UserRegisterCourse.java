package src.service.CourseRegister.Dto;/*
Created on 11/29/2023  3:46 PM 2023

@author: tinh2

ProjectName: OnlineLearning
*/

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class UserRegisterCourse {

    private String fullname;
    private String phone;

    public Date createAt ;

    public Date updateAt ;
    private double price;
    private int courseId;

    private Boolean isActive;
    private String course_name;

    private String otp;
    private int userId;
    private int teacherId;

    private String email;

    private int register_course_id;
}
