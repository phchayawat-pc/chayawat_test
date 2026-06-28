package th.co.truecorp.commonapi.reward.cms.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "rwd_shelf_type")
@Getter
@Setter
public class RwdShelfType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "shelf_type_code", length = 25, unique = true)
    private String shelfTypeCode;

    @Column(name = "shelf_type_name", length = 100)
    private String shelfTypeName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "valid_flag", length = 1, nullable = false, columnDefinition = "varchar(1) default 'Y'")
    private String validFlag;

    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    @Column(name = "modified_date")
    private Timestamp modifiedDate;

    // Getters and Setters
}
