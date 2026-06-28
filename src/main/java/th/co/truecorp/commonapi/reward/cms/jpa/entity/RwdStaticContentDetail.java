package th.co.truecorp.commonapi.reward.cms.jpa.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "rwd_static_content")
@Getter
@Setter
public class RwdStaticContentDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Integer id;

    @Column(name = "content_id", length = 12)
    private String contentId;

    @Column(name = "seq_no")
    private Integer seq_no;

    @Column(name = "lang", length = 3)
    private String lang;

    @Column(name = "customer_grade", length = 50)
    private String customer_grade;

    @Column(name = "topic", length = 100)
    private String topic;

    @Column(name = "description", length = 4000)
    private String description;

    @Column(name = "image", length = 256)
    private String image;

    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    @Column(name = "modified_date")
    private Timestamp modifiedDate;

}
