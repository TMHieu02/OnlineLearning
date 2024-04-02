package src.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;
import java.util.Date;

@Entity
@Table(name="role")
@Data
public class Role {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "role_id", nullable = false)
    private int Id;
    @Basic
    @Column(name = "role_name", nullable = false, length = 50)
    private String name;
    @Basic
    @Column(name = "isDeleted", nullable = true)
    private Boolean isDeleted;
    @Basic
    @Column(name = "created_at")
    private Date createAt = new Date(new java.util.Date().getTime());
    @Basic
    @Column(name = "updated_at")
    private Date updateAt = new Date(new java.util.Date().getTime());

    //// Id  bảng này là khóa ngoại của bảng khác
    @OneToMany(mappedBy = "roleByRoleId")
    private Collection<User> usersByRoleId;

    //
    public Role(int id, String name ) {
        Id = id;
        this.name = name;
    }
    public Role() {

    }

}
