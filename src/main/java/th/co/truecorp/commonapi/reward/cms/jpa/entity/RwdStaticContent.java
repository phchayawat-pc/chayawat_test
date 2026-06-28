package th.co.truecorp.commonapi.reward.cms.jpa.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "rwd_static_content")
@Getter
@Setter
public class RwdStaticContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Integer id;

    @Column(name = "content_id", length = 12)
    private String contentId;

    @Column(name = "content_name", length = 100)
    private String contentName;

    @Column(name = "brand_code", length = 25)
    private String brandCode;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "status", length = 25)
    private String status;

    @Column(name = "valid_flag", length = 1)
    private String validFlag;

    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    @Column(name = "modified_date")
    private Timestamp modifiedDate;

}
