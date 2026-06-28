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
public class ApiGwBurnHistoryRsp {
    private String id;
    private String type;
    private String name;
    private String description;
    private String productSpecId;
    private String accountId;
    private String href;
    private LoyaltyAccount loyaltyAccount;
    private List<Characteristic> characteristics;
    private CouponDetail couponDetail;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LoyaltyAccount {
        private String id;
        private LoyaltyBalance loyaltyBalance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LoyaltyBalance {
        private String id;
        private Quantity quantity;
        private ValidFor validFor;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Quantity {
        private String unit;
        private String balance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ValidFor {
        private String startDateTime;
        private String endDateTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Characteristic {
        private String name;
        private String value;
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
}
