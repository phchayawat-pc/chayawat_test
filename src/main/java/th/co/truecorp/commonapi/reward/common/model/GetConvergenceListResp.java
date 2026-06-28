package th.co.truecorp.commonapi.reward.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import th.co.truecorp.commonlib.ws.model.StatusJsonResponse;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class GetConvergenceListResp {
    private StatusJsonResponse status;
    private List<DataObj> data;


    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class DataObj {
        // mandatory
        private String digitalId;
        /**
         * prefix 66 , circuit id
         */
        private String productId;
        private String productBrand;
        private String productType;
        private String productStatus;
        private String productSegment;
        private String customerIdentity;
        private String customerIdentityType;
        private String customerType;
        // optional
        private String customerNumber;
        private String bcBan;
        private String ban;
        private String multiSimLevel;
    }
}
