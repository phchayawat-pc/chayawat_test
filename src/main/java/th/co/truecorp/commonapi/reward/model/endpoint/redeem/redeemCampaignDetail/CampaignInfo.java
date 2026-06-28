package th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemCampaignDetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignInfo {
    private String txid;
    private String id;
    private Name name;
    private List<RelatedParty> relatedParty;
    private Description description;
    private Criteria criteria;
    private ValidFor validFor;
    private String type;
    private String subType;
    private String originalPoint;
    private String pointPerUnit;
    private CouponDetail couponDetail;
    private String href;
}


