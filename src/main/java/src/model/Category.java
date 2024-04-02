package src.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "category")
@Data
public class Category {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "category_id", nullable = false)
    private int Id;

    @Basic
    @Column(name = "parent_category_id", nullable = false)
    private int parentCategoryId;
    @Basic
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    @Basic
    @Column(name = "image", nullable = true, length = 255)
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


    // Id  bảng này là khóa ngoại của bảng khác
    @OneToMany(mappedBy = "categoryByCategoryId")
    private Collection<Course> courseByCategoryId;

    public Category(int id, String name, String image) {
        Id = id;
        this.name = name;
        this.image = image;
    }
    public Category() {

    }
}
