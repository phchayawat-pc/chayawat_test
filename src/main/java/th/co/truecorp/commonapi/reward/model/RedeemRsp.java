package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

@Data
public class RedeemRsp {
    private boolean status;
    private String description;
    private String couponCode;
    private String expireDate;

}
