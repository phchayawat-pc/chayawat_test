package th.co.truecorp.commonapi.reward.cms.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "rwd_display_type")
@Getter
@Setter
public class RwdDisplayType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "display_type_code", length = 25, unique = true)
    private String displayTypeCode;

    @Column(name = "display_type_name", length = 100)
    private String displayTypeName;

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

    @OneToMany(mappedBy = "rwdDisplayType")
    private List<RwdLayoutDetail> rwdLayoutDetails;

    // Getters and Setters
}
