package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

import java.util.List;

@Data
public class RedeemListRsp {
    private String campaignCode;
    private String thumbnail;
    private String campaignName;
    private String campaignDescription;
    private String campaignType;
    private List<String> cardType;
    private String regularPoint;
    private String offerPoint;
}
