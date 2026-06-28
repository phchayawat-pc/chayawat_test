package th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemGame.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedeemGameDataApiReq {
    private String refId;
    private String campaignId;
}
