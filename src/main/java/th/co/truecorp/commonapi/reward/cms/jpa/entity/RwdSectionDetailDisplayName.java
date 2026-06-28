package th.co.truecorp.commonapi.reward.cms.jpa.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Data
@Table(name = "rwd_section_detail_display_name")
public class RwdSectionDetailDisplayName implements Serializable {

    @Serial
    private static final long serialVersionUID = -4913181774362291420L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "section_id")
    private String sectionId;

    @Column(name = "seq_no")
    private Integer seqId;

    @Column(name = "lang")
    private String lang;

    @Column(name = "item_display_name")
    private String itemDisplayName;

    @ManyToOne
    @JoinColumn(name = "section_id", insertable = false, updatable = false)
    private RwdSection rwdSection;

    @ManyToOne
    @JoinColumn(name = "section_id", insertable = false, updatable = false)
    private RwdSectionDetail rwdSectionDetail;

}
