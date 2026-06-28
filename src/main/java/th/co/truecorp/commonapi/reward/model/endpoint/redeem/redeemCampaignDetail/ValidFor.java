package th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemCampaignDetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidFor {
    private String startDateTime;
    private String endDateTime;
    private int remainingDays;

}
