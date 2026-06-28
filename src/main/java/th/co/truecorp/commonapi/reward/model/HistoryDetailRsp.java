package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

@Data
public class HistoryDetailRsp {
    private String campaignCode;
    private String campaignName;
    private String campaignDescription;
    private String couponExpiryDate;
    private String privilegeType;
    private ConditionInfo conditionInfo;
    private String bottomLink;
    private String textButton;

    @Data
    public static class ConditionInfo{
        private String type;
        private String message;
        private String url;
        private String urlName;
    }

    @Override
    public String toString(){
        return "[HistoryDetailRsp] campaignCode=" + getCampaignCode()
                +" ,campaignName=" + getCampaignName()
                +" ,campaignDescription=" + getCampaignDescription()
                +" ,privilegeType=" + getPrivilegeType()
                +" ,couponExpiringDate=" + getCouponExpiryDate()
                +" ,conditionInfo=" + getConditionInfo().toString()
                +" ,bottomLink="+getBottomLink()
                +" ,textButton="+getTextButton();
    }
}
