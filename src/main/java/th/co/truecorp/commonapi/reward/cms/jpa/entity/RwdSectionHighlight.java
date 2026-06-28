package th.co.truecorp.commonapi.reward.cms.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "rwd_section_highlight", uniqueConstraints = @UniqueConstraint(columnNames = {"section_id", "seq_no"}))
@Getter
@Setter
public class RwdSectionHighlight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "section_id", length = 25, unique = true)
    private String sectionId;

    @Column(name = "seq_no")
    private Integer seqNo;

    @Column(name = "item_image1x1", length = 500)
    private String itemImage1x1;

    @Column(name = "item_image4x3", length = 500)
    private String itemImage4x3;

    @Column(name = "item_image16x9", length = 500)
    private String itemImage16x9;

    @Column(name = "item_image9x16", length = 500)
    private String itemImage9x16;

    @Column(name = "item_type_code", length = 25)
    private String itemTypeCode;

    @Column(name = "item_subtype", length = 25)
    private String itemSubtype;

    @Column(name = "shelf_type_code", length = 25)
    private String shelfTypeCode;

    @Column(name = "item_mapping", length = 500)
    private String itemMapping;

    @Column(name = "item_mapping2", length = 500)
    private String itemMapping2;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    @Column(name = "modified_date")
    private Timestamp modifiedDate;
}
