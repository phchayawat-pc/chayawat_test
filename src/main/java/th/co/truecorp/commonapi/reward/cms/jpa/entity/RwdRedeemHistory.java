package th.co.truecorp.commonapi.reward.cms.jpa.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "rwd_redeem_history")
@Data
public class RwdRedeemHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand_code", nullable = false)
    private String brandCode;

    @Column(name = "digital_id", nullable = false)
    private String digitalId;

    @Column(name = "action_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp actionDate;

    @Column(name = "campaign_id", nullable = false)
    private String campaignId;

    @Column(name = "campaign_code", nullable = false)
    private String campaignCode;

    @Column(name = "campaign_type")
    private String campaignType;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "coupon_expire_date")
    private Timestamp couponExpireDate;

    @Column(name = "package_name")
    private String packageName;

    @Column(name = "description")
    private String description;

    @Column(name = "redeem_status", nullable = false)
    private String redeemStatus;

    @Column(name = "redeem_message")
    private String redeemMessage;

    @Column(name = "source", nullable = false)
    private String source;

}