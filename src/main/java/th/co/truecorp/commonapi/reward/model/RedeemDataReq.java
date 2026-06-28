package th.co.truecorp.commonapi.reward.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RedeemDataReq {
    private String campaignId;
    private String campaignCode;
    private String campaignType;
    private String source;
}
