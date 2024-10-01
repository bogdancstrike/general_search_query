package bogdan.dev.general_query.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "departments")
@Data
@Getter
@Setter
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String departmentName;

    private Integer departmentEmployeesNumber;

    @Temporal(TemporalType.DATE)
    private Date createdDate;

    private Boolean isActive;
}