package src.Dto;

import lombok.Data;

import java.util.Date;

@Data
public class ReviewUserDTO {
    private int reviewId;
    private String content;
    private String fullname;
    private String avatar;
    private int userId;
    private int courseId;
    private Boolean isDeleted;
    private Date create;
    private Date update;
}
