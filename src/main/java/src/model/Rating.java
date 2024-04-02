package src.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import java.util.*;
@Entity
@Table(name = "rating")
@Data
public class Rating {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "rating_id", nullable = false)
    private int Id;
    @Basic
    @Column(name = "rating", nullable = false)
    @Min(0)
    @Max(5)
    private int Rating;
    @Basic
    @Column(name = "isDeleted", nullable = true)
    private Boolean isDeleted;
    @Basic
    @Column(name = "created_at")
    private Date createAt = new Date(new java.util.Date().getTime());
    @Basic
    @Column(name = "updated_at")
    private Date updateAt = new Date(new java.util.Date().getTime());

    // Khóa ngoại
    @Basic
    @Column(name = "course_id", nullable = false)
    private int courseId;
    @Basic
    @Column(name = "user_id", nullable = false)
    private int userId;

    // Id bảng khác là khóa ngoại của bảng này
    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "course_id", nullable = false, insertable = false, updatable = false)
    private Course courseByCourseId;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, insertable = false, updatable = false)
    private User userByUserId;

    // Id  bảng này là khóa ngoại của bảng khác

    //
    public Rating(int id,int rating) {
        Id = id;
        Rating = rating;

    }
    public Rating() {

    }

}