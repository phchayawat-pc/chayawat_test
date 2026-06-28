package th.co.truecorp.commonapi.reward.model.redeem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class RedeemDealScanCodeResponse {
    private String campaignId;
    private String campaignName;
    private String campaignDescription;
    private String timeCounterFlag;
    private String couponCode;
    private String couponExpiryDate;
    private String privilegeType;
    private List<RedeemDealScanCodeConditionResponse> conditionInfo;
    private String bottomLink;
    private String textButton;
}


