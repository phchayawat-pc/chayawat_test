package th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemCampaignDetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CouponDetail {
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

    // Getters and setters
}
