package src.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;
@Entity
@Table(name = "course_register")
@Data
public class CourseRegister {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "course_register_id", nullable = false)
    private int Id;
    @Basic
    @Column(name = "isDeleted", nullable = true)
    private Boolean isDeleted;
    @Basic
    @Column(name = "otp", nullable = true)
    private String otp;
    @Basic
    @Column(name = "otpGeneratedTime", nullable = true)
    private LocalDateTime otpGeneratedTime;
    @Basic
    @Column(name = "isActive", nullable = true)
    private Boolean isActive;
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



}
