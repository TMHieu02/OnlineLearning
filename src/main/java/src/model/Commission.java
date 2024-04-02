package src.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name="commission")
@Data
public class Commission {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "commission_id", nullable = false)
    private int Id;
    @Basic
    @Column(name = "name", length = 255)
    private String name;
    @Basic
    @Column(name = "description", length = 255)
    private String description;
    @Basic
    @Column(name = "cost", precision = 0)
    private double Cost;
    @Basic
    @Column(name = "isDeleted", nullable = true)
    private Boolean isDeleted;
    @Basic
    @Column(name = "createAt", nullable = true)
    private Date createAt = new Date(new java.util.Date().getTime());
    @Basic
    @Column(name = "updateAt", nullable = true)
    private Date updateAt= new Date(new java.util.Date().getTime());
    public Commission(int id, String name, String description, double cost ) {
        Id = id;
        this.name = name;
        this.description = description;
        Cost = cost;
    }
    public Commission() {

    }

}