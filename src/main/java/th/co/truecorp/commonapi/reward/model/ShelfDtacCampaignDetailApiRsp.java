package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

import java.util.List;

@Data
public class ShelfDtacCampaignDetailApiRsp {
    private String txid;
    private String bzbId;
    private Name name;
    private List<RelatedParty> relatedParty;
    private Description description;
    private Criteria criteria;
    private ValidFor validFor;
    private String type;
    private String subType;
    private String onlyDisplay;
    private Integer originalPoint;
    private Integer pointPerUnit;
    private CouponDetail couponDetail;
    private String href;
    private List<characteristic> characteristic;

    @Data
    public static class Name{
        private String th;
        private String en;
    }

    @Data
    public static class RelatedParty{
        private String name;
        private String nameEn;
        private List<String> branch;
        private List<String> branchEn;
    }

    @Data
    public static class Description{
        private String th;
        private String en;
    }

    @Data
    public static class Criteria{
        private String th;
        private String en;
    }

    @Data
    public static class ValidFor{
        private String startDateTime;
        private String endDateTime;
        private Integer remainingDays;
    }

    @Data
    public static class CouponDetail{
        private String couponApp_th;
        private String couponApp_en;
        private String couponApp_my;
        private String couponApp_km;
        private String couponApp_url;
        private String couponWeb_th;
        private String couponWeb_en;
        private String couponWeb_my;
        private String couponWeb_km;
        private String couponWeb_url;
    }

    @Data
    public static class characteristic{
        private String name;
        private String value;
    }
}