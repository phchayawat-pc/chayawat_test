package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

@Data
public class HistoryDataRsp {
    private String campaignCode;
    private String thumbnail;
    private String campaignName;
    private String campaignDescription;
    private String points;
    private String date;
    private String couponCode;
    private String campaignType;


    @Override
    public String toString(){
        return "[HistoryRsp] campaignCode=" + getCampaignCode()
                +" ,thumbnail" + getThumbnail()
                +" ,campaignName" + getCampaignName()
                +" ,campaignDescription" + getCampaignDescription()
                +" ,points" + getPoints()
                +" ,date" + getDate()
                +" ,couponCode" + getCouponCode()
                +" ,campaignType" + getCampaignType();
    }
}
