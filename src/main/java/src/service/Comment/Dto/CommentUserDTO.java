package src.service.Comment.Dto;/*
Created on 12/4/2023  4:05 AM 2023

@author: tinh2

ProjectName: OnlineLearning
*/

import lombok.Data;

import java.util.Date;

@Data
public class CommentUserDTO {
    private int commentId;
    private String content;
    private String fullname;
    private String avatar;
    private int userId;
    private int videoId;
    private Boolean isDeleted;
    private Date create;
    private Date update;
}