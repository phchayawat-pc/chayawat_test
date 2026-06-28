package th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemLoyaltyBurn.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoyaltyBurnReq {
    private String id;
    private String href;
    private LoyaltyBurnUser user;
    private List<LoyaltyBurnPrivilege> privilege;
    private String requestDateTime;
    private String lang;
    private String smsFlag;
    private LoyaltyProgramProduct loyaltyProgramProduct;
    private LoyaltyBurnRelatedParty relatedParty;
}


