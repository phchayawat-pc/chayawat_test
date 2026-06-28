package th.co.truecorp.commonapi.reward.cms.jpa.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data

@Table(name = "rwd_section_detail")
public class RwdSectionDetail {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Integer id;

    @Id
    @Column(name = "section_id")
    private String sectionId;

    @Column(name = "seq_no")
    private Integer seqNo;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_icon")
    private String itemIcon;

    @Column(name = "item_image1x1")
    private String itemImage1x1;

    @Column(name = "item_image4x3")
    private String itemImage4x3;

    @Column(name = "item_image16x9")
    private String itemImage16x9;

    @Column(name = "item_image9x16")
    private String itemImage9x16;

    @Column(name = "item_type_code")
    private String itemTypeCode;

    @Column(name = "item_subtype")
    private String itemSubtype;

    @Column(name = "shelf_type_code")
    private String shelfTypeCode;

    @Column(name = "item_mapping")
    private String itemMapping;

    @Column(name = "item_mapping2")
    private String itemMapping2;

    @Column(name = "customer_grade")
    private String customerGrade;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "priority")
    private Integer priority;

    @OneToMany(mappedBy = "rwdSectionDetail")
    private List<RwdSectionDetailDisplayName> rwdSectionDetailDisplayNames;

    @MapsId("sectionId")
    @ManyToOne
    @JoinColumn(name = "section_id", insertable = false, updatable = false)
    private RwdSection rwdSection;

}
