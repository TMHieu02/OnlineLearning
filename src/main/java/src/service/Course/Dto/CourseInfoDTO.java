package src.service.Course.Dto;/*
Created on 12/2/2023  10:02 AM 2023

@author: tinh2

ProjectName: OnlineLearning
*/

import lombok.Data;

import java.util.Date;

@Data
public class CourseInfoDTO {
    private int course_id;

    private String title;
    private int category_id;

    private String category_name;

    private int user_id;

    private String user_name;

    private double price;

    private double promotional_price;

    private double sold;
    private String description;

    private String image;

    private boolean active;

    private Date created_at;

    private Date update_at;

    private double rating;

    private int user_registers;

    public Date createAt ;

    public Date updateAt ;
}
