package src.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id", nullable = false)
    private int Id;
    @Basic
    @Column(name = "fullname", length = 50)
    private String fullname;
    @Basic
    @Column(name = "email", nullable = false, length = 50)
    private String email;
    @Basic
    @Column(name = "phone", length = 11)
    private String phone;
    @Basic
    @Column(name = "avatar", length = 255)
    private String avatar;
    @Basic
    @Column(name = "description", length = 255)
    private String description;
    @Basic
    @Column(name = "bank_name", length = 50)
    private String bank_name;
    @Basic
    @Column(name = "account_number", length = 50)
    private String account_number;
    @Basic
    @Column(name = "account_name", length = 50)
    private String account_name;
    @Basic
    @Column(name = "password", nullable = false, length = 50)
    private String password;
    @Basic
    @Column(name = "created_at")
    private Date createAt = new Date(new java.util.Date().getTime());
    @Basic
    @Column(name = "updated_at")
    private Date updateAt = new Date(new java.util.Date().getTime());
    @Basic
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    // Khóa ngoại
    @Basic
    @Column(name = "role_id", nullable = false)
    private int roleId;

    // Id bảng khác là khóa ngoại của bảng này
    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "role_id", nullable = false, insertable = false, updatable = false)
    private Role roleByRoleId;

    // Id  bảng này là khóa ngoại của bảng khác
    @OneToMany(mappedBy = "userByUserId")
    private Collection<Course> courseByUserId;
    @OneToMany(mappedBy = "userByUserId")
    private Collection<Cart> cartByUserId;
    @OneToMany(mappedBy = "userByUserId")
    private Collection<Orders> ordersByUserId;
    @OneToMany(mappedBy = "userByUserId")
    private Collection<CourseRegister> courseRegisterByUserId;
    @OneToMany(mappedBy = "userByUserId")
    private Collection<Review> reviewByUserId;
    @OneToMany(mappedBy = "userByUserId")
    private Collection<Comment> commentByUserId;
    @OneToMany(mappedBy = "userByUserId")
    private Collection<Feedback> feedbackByUserId;
    @OneToMany(mappedBy = "userByUserId")
    private Collection<Rating> ratingByUserId;


    //
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(roleByRoleId.getName()));
    }
    @Override
    public String getUsername() {
        return this.email;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !this.isDeleted;
    }
    public User(int id, String fullname, String email, String phone,
                String avatar, String description,String back_name,
                String account_number, String account_name, String password)
    {
        Id = id;
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.avatar = avatar;
        this.description = description;
        this.bank_name = back_name;
        this.account_number = account_number;
        this.account_name = account_name;
        this.password = password;
    }
    public User() {

    }

}