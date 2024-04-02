package src.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.*;
@Entity
@Table(name = "course")
@Data
public class Course {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "course_id", nullable = false)
    private int Id;
    @Basic
    @Column(name = "title", length = 255)
    private String title;
    @Basic
    @Column(name = "price", precision = 0)
    private double price;
    @Basic
    @Column(name = "promotional_price", precision = 0)
    private double promotional_price;
    @Basic
    @Column(name = "sold", precision = 0)
    private double sold;
    @Basic
    @Column(name = "description", length = 255)
    private String description;
    @Basic
    @Column(name = "active", nullable = false)
    private Boolean active = false;
    @Basic
    @Column(name = "rating", precision = 0)
    private float rating;
    @Basic
    @Column(name = "image", length = 255)
    private String image;
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
    @Column(name = "category_id", nullable = false)
    private int categoryId;
    @Basic
    @Column(name = "user_id", nullable = false)
    private int userId;

    // Id bảng khác là khóa ngoại của bảng này
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", nullable = false, insertable = false, updatable = false)
    private Category categoryByCategoryId;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, insertable = false, updatable = false)
    private User userByUserId;

    // Id  bảng này là khóa ngoại của bảng khác
    @OneToMany(mappedBy = "courseByCourseId")
    private Collection<OrderItem> orderItemByCourseId;
    @OneToMany(mappedBy = "courseByCourseId")
    private Collection<CourseRegister> courseRegisterByCourseId;
    @OneToMany(mappedBy = "courseByCourseId")
    private Collection<Video> videoByCourseId;
    @OneToMany(mappedBy = "courseByCourseId")
    private Collection<Document> documentByCourseId;
    @OneToMany(mappedBy = "courseByCourseId")
    private Collection<Review> reviewByCourseId;
    @OneToMany(mappedBy = "courseByCourseId")
    private Collection<Rating> ratingByCourseId;


    public Course() {

    }

}
