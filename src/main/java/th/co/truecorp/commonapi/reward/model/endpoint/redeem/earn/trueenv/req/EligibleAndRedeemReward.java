package th.co.truecorp.commonapi.reward.model.endpoint.redeem.earn.trueenv.req;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EligibleAndRedeemReward {
    private String pricePlanCode;
    private int quantity;
//    private int points;
}
