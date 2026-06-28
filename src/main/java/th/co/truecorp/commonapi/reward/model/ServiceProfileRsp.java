package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

@Data
public class ServiceProfileRsp {
    private status status;
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

    @Data
    public static class status{
        private String statusType;
        private String errorCode;
        private String errorMessage;
        private String errorDescription;
        private String hostId;
        private String transactionId;
    }
}
