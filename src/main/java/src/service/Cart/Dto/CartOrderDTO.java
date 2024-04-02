package src.service.Cart.Dto;/*
Created on 12/4/2023  2:42 AM 2023

@author: tinh2

ProjectName: OnlineLearning
*/



import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import lombok.Data;

import java.util.Date;
@Data
public class CartOrderDTO {
    private int cart_id;
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

    public Boolean isDeleted  = false;

    public Date createAt ;

    public Date updateAt ;
}
