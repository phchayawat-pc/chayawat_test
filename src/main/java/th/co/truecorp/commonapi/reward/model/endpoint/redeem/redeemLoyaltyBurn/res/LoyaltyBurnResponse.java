package th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemLoyaltyBurn.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemEligibleAndRedeem.res.Redemption;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoyaltyBurnResponse {
    private String code;
    private String error;
    private String message;
    private String businessError;
    private String status;
    private String description;
    private String name;
    private String productNumber;
    private String timestamp;
}

