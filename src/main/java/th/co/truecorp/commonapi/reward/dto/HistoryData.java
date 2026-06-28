package th.co.truecorp.commonapi.reward.dto;

import lombok.Data;

@Data
public class HistoryData {
    private String campaignCode;
    private String thumbnail;
    private String campaignName;
    private String campaignDescription;
    private String points;
    private String date;
    private String couponCode;
    private String campaignType;
    private String expireDate;
    private String privilegeType;
    private String couponExpiryDate;
    private ConditionInfo conditionInfo;

    @Data
    public static class ConditionInfo {
        private String type;
        private String message;
        private String url;
        private String urlName;
    }


    @Override
    public String toString(){
        return "[HistoryRsp] campaignCode=" + getCampaignCode()
                +" ,thumbnail=" + getThumbnail()
                +" ,campaignName=" + getCampaignName()
                +" ,campaignDescription=" + getCampaignDescription()
                +" ,points=" + getPoints()
                +" ,date=" + getDate()
                +" ,couponCode=" + getCouponCode()
                +" ,campaignType=" + getCampaignType()
                +" ,expireDate=" + getExpireDate();
    }
}
