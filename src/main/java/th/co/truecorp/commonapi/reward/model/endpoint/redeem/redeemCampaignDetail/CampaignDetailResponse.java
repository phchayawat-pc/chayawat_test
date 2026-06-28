package th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemCampaignDetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignDetailResponse {
    private String code;
    private String description;
    private String timestamp;
    private String businessError;
    private String message;
    private List<CampaignInfo> campaignInfo;
    private String error;
}


