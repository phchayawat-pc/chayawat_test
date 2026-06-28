package th.co.truecorp.commonapi.reward.cms.jpa.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "rwd_section_display_name")
@Getter
@Setter
public class RwdSectionDisplayName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "section_id", length = 12)
    private String sectionId;

    @Column(name = "lang", length = 3)
    private String lang;

    @Column(name = "display_image", length = 256)
    private String displayImage;

    @Column(name = "section_display_name", length = 100)
    private String sectionDisplayName;

    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    @Column(name = "modified_date")
    private Timestamp modifiedDate;

    @ManyToOne
    @JoinColumn(name = "section_id", insertable = false, updatable = false)
    private RwdSection rwdSection;
}

