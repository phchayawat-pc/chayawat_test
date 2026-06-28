package th.co.truecorp.commonapi.reward.cms.jpa.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "rwd_layout_detail")
@Getter
@Setter
public class RwdLayoutDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "layout_id", length = 12)
    private String layoutId;

    @Column(name = "seq_no")
    private Integer seqNo;

    @Column(name = "section_id", length = 12)
    private String sectionId;

    @Column(name = "display_type_code", length = 25)
    private String displayTypeCode;

    @Column(name = "show_item_flag", length = 1, nullable = false, columnDefinition = "varchar(1) default 'Y'")
    private String showItemFlag;

    @Column(name = "show_highlight_flag", length = 1, nullable = false, columnDefinition = "varchar(1) default 'N'")
    private String showHighlightFlag;

    @Column(name = "used_content_cms_flag", length = 1, nullable = false, columnDefinition = "varchar(1) default 'N'")
    private String usedContentCmsFlag;

    @Column(name = "auto_slide", length = 1, nullable = false, columnDefinition = "varchar(1) default 'N'")
    private String autoSlide;

    @Column(name = "see_all_flag", length = 1, nullable = false, columnDefinition = "varchar(1) default 'N'")
    private String seeAllFlag;

    @Column(name = "goto_section_id", length = 12)
    private String gotoSectionId;

    @Column(name = "template_code", length = 25)
    private String templateCode;

    @Column(name = "display_header_flag", length = 1, nullable = false, columnDefinition = "varchar(1) default 'Y'")
    private String displayHeaderFlag;

    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;

    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    @Column(name = "modified_date")
    private Timestamp modifiedDate;

    @Column(name = "priority")
    private Integer priority;

    @ManyToOne
    @JoinColumn(name = "layout_id", insertable = false, updatable = false)
    private RwdLayout rwdLayout;

    @ManyToOne
    @JoinColumn(name = "section_id", insertable = false, updatable = false)
    private RwdSection rwdSection;

    @ManyToOne
    @JoinColumn(name = "display_type_code", insertable = false, updatable = false)
    private RwdDisplayType rwdDisplayType;

    @ManyToOne
    @JoinColumn(name = "goto_section_id", insertable = false, updatable = false)
    private RwdSection gotoSection;

    @ManyToOne
    @JoinColumn(name = "template_code", insertable = false, updatable = false)
    private RwdTemplate rwdTemplate;
}
