package src.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import java.util.*;
@Entity
@Table(name = "video")
@Data
public class Video {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "video_id", nullable = false)
    private int Id;
    @Basic
    @Column(name = "video_filepath", length = 255)
    private String video_filepath;
    @Basic
    @Column(name = "views")
    @Min(0)
    private int Views = 0;
    @Basic
    @Column(name = "description", length = 255)
    private String description;
    @Basic
    @Column(name = "title", length = 255)
    private String title;
    @Basic
    @Column(name = "hours", precision = 0)
    @Min(0)
    @Max(23)
    private int Hours = 0;
    @Basic
    @Column(name = "minutes", precision = 0)
    @Min(0)
    @Max(59)
    private int Minutes = 0;
    @Basic
    @Column(name = "seconds", precision = 0)
    @Min(0)
    @Max(59)
    private int Seconds = 0;
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
    @Column(name = "course_id", nullable = false)
    private int courseId;

    // Id bảng khác là khóa ngoại của bảng này
    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "course_id", nullable = false, insertable = false, updatable = false)
    private Course courseByCourseId;

    // Id  bảng này là khóa ngoại của bảng khác
    @OneToMany(mappedBy = "videoByVideoId")
    private Collection<Comment> commentByVideoId;

    //
    public Video(int id, String video_filepath, int views,
                  String title, int hours, int minutes,
                  int seconds, String image) {
        Id = id;
        this.video_filepath = video_filepath;
        Views = views;
        this.title = title;
        Hours = hours;
        Minutes = minutes;
        Seconds = seconds;
        this.image = image;

    }
    public Video() {

    }

}
