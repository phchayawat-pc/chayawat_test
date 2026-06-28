package th.co.truecorp.commonapi.reward.cms.jpa.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "rwd_section")
@Getter
@Setter
public class RwdSection {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Integer id;

    @Id
    @Column(name = "section_id", length = 12)
    private String sectionId;

    @Column(name = "section_name", length = 100)
    private String sectionName;

    @Column(name = "display_name_type", length = 25, nullable = false, columnDefinition = "varchar(25) default 'TEXT'")
    private String displayNameType;

    @Column(name = "brand_code", length = 25)
    private String brandCode;

    @Column(name = "grading_flag", length = 1, nullable = false, columnDefinition = "varchar(1) default 'N'")
    private String gradingFlag;

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

    @ManyToOne
    @JoinColumn(name = "brand_code", insertable = false, updatable = false)
    private RwdBrand rwdBrand;

    @OneToMany(mappedBy = "rwdSection")
    private List<RwdLayoutDetail> rwdLayoutDetails;

    @OneToMany(mappedBy = "sectionId")
    private List<RwdSectionDisplayName> rwdSectionDisplayNames;

    @OneToMany(mappedBy = "rwdSection")
    private List<RwdSectionDetail> rwdSectionDetails;

    @OneToMany(mappedBy = "rwdSection")
    private List<RwdSectionDetailDisplayName> rwdSectionDetailDisplayNames;
}
