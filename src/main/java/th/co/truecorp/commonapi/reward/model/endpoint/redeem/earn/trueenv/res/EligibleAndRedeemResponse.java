package th.co.truecorp.commonapi.reward.model.endpoint.redeem.earn.trueenv.res;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EligibleAndRedeemResponse {
    private String code;
    private String description;
    private String timestamp;
    private String error;
    private String message;
    private Redemption redemption;
    private String businessError;
}

