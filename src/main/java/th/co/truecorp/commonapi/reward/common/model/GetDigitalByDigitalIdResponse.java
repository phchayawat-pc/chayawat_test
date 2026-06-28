package th.co.truecorp.commonapi.reward.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import th.co.truecorp.commonapi.reward.model.CustomerProfileRsp;
import th.co.truecorp.commonapi.reward.model.ServiceProfileRsp;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetDigitalByDigitalIdResponse {
    private Status status;
    private DataWrapper data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Status {
        private String statusType;
        private String errorCode;
        private String errorMessage;
        private String errorDescription;
        private String hostId;
        private String transactionId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataWrapper {
        private Profile profile;
        private Object childList;
        private Object packageCurrent;
        private Reward reward;
        private Object nontelco;
        private Object payment;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Profile {
        private Status status;
        private String digitalId;
        private String productId;
        private String productBrand;
        private String productType;
        private String productStatus;
        private String productSegment;
        private String customerIdentity;
        private String customerIdentityType;
        private String customerType;
        private String customerNumber;
        private String bcBan;
        private String ban;
        private String multiSimLevel;
        private String mainConvergenceCode;
        private String assetGroupId;
        private String multiSimIndicator;
//        private String companyCode;
//        private String companyDescription;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reward {
        public Status status;
        public RewardData data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RewardData {
        private String id;
        private String typeCard;
        private String cardName;
        private Card card;
        private MyPoint myPoint;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Card {
        private String type;
        private String typeCode;
        private String no;
        private String mastercardNo;
        private String expirationDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPoint {
        private int totalPoint;
        private String unit;
        private List<PointDetail> points;
        private LoyaltyProgramMember loyaltyProgramMember;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointDetail {
        private String expirationDate;
        private String points;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoyaltyProgramMember {
        private String id;
        private String href;
    }
}

