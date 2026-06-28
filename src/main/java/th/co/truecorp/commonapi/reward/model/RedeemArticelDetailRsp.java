package th.co.truecorp.commonapi.reward.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RedeemArticelDetailRsp {
    private String campaignId;
    private String campaignName;
    private String highlight;
    private String detail;

}
