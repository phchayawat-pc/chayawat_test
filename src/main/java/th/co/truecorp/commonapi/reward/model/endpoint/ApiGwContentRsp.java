package th.co.truecorp.commonapi.reward.model.endpoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiGwContentRsp {
    private String code;
    private String description;
    private String message;
    private String businessError;
    private String error;
    private String timestamp;
    private List<CampaignInfo> campaignInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CampaignInfo {
        private String txid;
        private long id;
        private LocaleText name;
        private List<RelatedParty> relatedParty;
        private LocaleText description;
        private LocaleText criteria;
        private ValidFor validFor;
        private String type;
        private String subType;
        private String onlyDisplay;
        private int originalPoint;
        private int pointPerUnit;
        private CouponDetail couponDetail;
        private String href;
        private List<Characteristic> characteristic;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocaleText {
        private String th;
        private String en;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RelatedParty {
        private String name;
        private String nameEn;
        private List<String> branch;
        private List<String> branchEn;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ValidFor {
        private String startDateTime;
        private String endDateTime;
        private int remainingDays;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CouponDetail {
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
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Characteristic {
        private String name;
        private String value;
    }
}
