package th.co.truecorp.commonapi.reward.model.redeem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class RedeemTakeRedeemResponse {
    private String name;
    private String description;
    private String expireDate;
    private String couponCode;
    private String redeemDate;
    private boolean clearCacheFlag = false;
}


