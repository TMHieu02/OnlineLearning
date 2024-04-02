package src.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.*;
@Entity
@Table(name = "feedback")
@Data
public class Feedback {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "feedback_id", nullable = false)
    private int Id;
    @Basic
    @Column(name = "title", length = 30)
    private String title;
    @Basic
    @Column(name = "content", length = 255)
    private String content;
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
    @Column(name = "user_id", nullable = false)
    private int userId;

    // Id bảng khác là khóa ngoại của bảng này

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, insertable = false, updatable = false)
    private User userByUserId;

    // Id  bảng này là khóa ngoại của bảng khác

    //
    public Feedback(int id, String title, String content, String image) {
        Id = id;
        this.title = title;
        this.content = content;
        this.image = image;

    }
    public Feedback() {

    }
}
