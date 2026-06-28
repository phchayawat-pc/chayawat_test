package th.co.truecorp.commonapi.reward.model.endpoint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import th.co.truecorp.commonlib.ws.model.StatusJsonResponse;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileInfo {
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
    private String customerNumber;
    private String bcBan;
    private String ban;
    private String multiSimLevel;
    private String mainConvergenceCode;
    private String assetGroupId;
    
    private StatusJsonResponse status;
    
}
