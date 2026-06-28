package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

import java.util.List;

@Data
public class RedeemDealScanCodeRsp {
    private String campaignId;
    private String campaignName;
    private String campaignDescription;
    private String couponExpiryDate;
    private String privilegeType;
    private List<ConditionInfo> conditionInfo;
    private String bottomLink;
    private String textButton;
    private String timeCounterShow;

    @Data
    public static class ConditionInfo{
        private String type = null;
        private String message = null;
        private String url = null;
        private String urlName = null;
    }

    @Override
    public String toString(){
        return "[HistoryDetailRsp] campaignId=" + getCampaignId()
                +" ,campaignName=" + getCampaignName()
                +" ,campaignDescription=" + getCampaignDescription()
                +" ,privilegeType=" + getPrivilegeType()
                +" ,couponExpiringDate=" + getCouponExpiryDate()
                +" ,conditionInfo=" + getConditionInfo().toString()
                +" ,bottomLink="+getBottomLink()
                +" ,textButton="+getTextButton();
    }
}
