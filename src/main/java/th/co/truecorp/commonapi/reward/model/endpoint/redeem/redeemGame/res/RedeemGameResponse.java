package th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemGame.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RedeemGameResponse {
    private String code;
    private String description;
    private String timestamp;
    private String businessError;
    private String message;
    private String refId;
    private String campaignId;
    private String name;
    private Quantity quantity;
    private String campaignSerial;
    private String error;
}

