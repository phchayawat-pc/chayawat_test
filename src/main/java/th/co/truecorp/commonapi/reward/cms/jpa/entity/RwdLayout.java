package th.co.truecorp.commonapi.reward.cms.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "rwd_layout")
@Getter
@Setter
public class RwdLayout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "layout_id", length = 12, unique = true)
    private String layoutId;

    @Column(name = "layout_name", length = 100)
    private String layoutName;

    @Column(name = "brand_code", length = 25)
    private String brandCode;

    @Column(name = "product_type_code", length = 25)
    private String productTypeCode;

    @Column(name = "charge_type_code", length = 25)
    private String chargeTypeCode;

    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;

    @Column(name = "valid_flag", length = 1, nullable = false, columnDefinition = "varchar(1) default 'Y'")
    private String validFlag;

    @Column(name = "layout_status", length = 25, nullable = false, columnDefinition = "varchar(25) default 'DRAFT'")
    private String layoutStatus;

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

    @ManyToOne
    @JoinColumn(name = "charge_type_code", insertable = false, updatable = false)
    private RwdChargeType rwdChargeType;

    @ManyToOne
    @JoinColumn(name = "product_type_code", insertable = false, updatable = false)
    private RwdProductType rwdProductType;

    @OneToMany(mappedBy = "rwdLayout")
    private List<RwdLayoutDetail> rwdLayoutDetails;

    // Getters and Setters
}
