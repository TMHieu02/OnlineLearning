package src.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.*;
@Entity
@Table(name = "orders")
@Data
public class Orders {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "order_id", nullable = false)
    private int Id;
    @Basic
    @Column(name = "fullname", length = 50)
    private String fullname;
    @Basic
    @Column(name = "email", length = 50)
    private String email;
    @Basic
    @Column(name = "phone", length = 11)
    private String phone;
    @Basic
    @Column(name = "payment_price")
    private double payment_price;
    @Basic
    @Column(name = "status")
    private int Status;
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

    // // Id bảng khác là khóa ngoại của bảng này
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, insertable = false, updatable = false)
    private User userByUserId;

    //// Id  bảng này là khóa ngoại của bảng khác
    @OneToMany(mappedBy = "orderByOrderId")
    private Collection<OrderItem> orderItemByOrderId;

    public Orders(int id, String fullname, String email, String phone, int status) {
        Id = id;
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        Status = status;
    }
    public Orders() {

    }
}

