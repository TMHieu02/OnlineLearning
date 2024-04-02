package src.Dto;/*
Created on 12/4/2023  4:08 AM 2023

@author: tinh2

ProjectName: OnlineLearning
*/


import lombok.Data;

@Data
public class RatingDTO {
    private int Id;
    private int courseId;
    private int userId;
    private int rating;
}