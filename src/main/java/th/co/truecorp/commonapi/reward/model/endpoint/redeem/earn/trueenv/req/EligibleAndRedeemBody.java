package th.co.truecorp.commonapi.reward.model.endpoint.redeem.earn.trueenv.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EligibleAndRedeemBody {
    private String id;
    private String trnNo;
    private String identifierType;
    private String channel;
    private List<EligibleAndRedeemReward> rewards;
    private String comment;
    private String simulate;
}


