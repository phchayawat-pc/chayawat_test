package th.co.truecorp.commonapi.reward.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class RedeemDealDetailRsp {
    private String campaignId;
    private String campaignCode;
    private List<String> partnerImage;
    private String highLight;
    private String regularPoint;
    private String offerPoint;
    private String campaignName;
    private String campaignDescription;
    private String campaignExpiryDate;
    private CouponTimeCouter couponTimeCouter;
    private String campaignMessage;
    private CampagnInfo campagnInfo;
    private String campaignType;
    private List<String> cardType;
    private Boolean eligibleStatus;
    private String eligibleDesc;
    private String timeCounterErr;
    private Boolean displayRedeem;

    @Data
    public static class CouponTimeCouter{
       private String day;
       private String hour;
       private String min;
    }

    @Data
    public static class CampagnInfo{
        private String detail;
        private String condition;
        private List<String> branch;
    }

}
