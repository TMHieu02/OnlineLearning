package src.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.*;
@Entity
@Table(name = "comment")
@Data
public class Comment {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "comment_id", nullable = false)
    private int Id;
    @Basic
    @Column(name = "parent_comment_id", nullable = false)
    private int parentCommentId;
    @Basic
    @Column(name = "content", length = 255)
    private String content;
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
    @Column(name = "video_id", nullable = false)
    private int videoId;
    @Basic
    @Column(name = "user_id", nullable = false)
    private int userId;


    // Id bảng khác là khóa ngoại của bảng này
    @ManyToOne
    @JoinColumn(name = "video_id", referencedColumnName = "video_id", nullable = false, insertable = false, updatable = false)
    private Video videoByVideoId;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, insertable = false, updatable = false)
    private User userByUserId;

    // Id  bảng này là khóa ngoại của bảng khác

    //
    public Comment(int id, String content) {
        Id = id;
        this.content = content;

    }
    public Comment() {

    }
}
