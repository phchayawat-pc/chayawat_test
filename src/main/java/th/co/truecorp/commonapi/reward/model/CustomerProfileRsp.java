package th.co.truecorp.commonapi.reward.model;

import lombok.Data;
import th.co.truecorp.commonapi.reward.common.model.CommonTrueProfileRsp;
import th.co.truecorp.commonapi.reward.common.model.Points;

import java.util.List;

@Data
public class CustomerProfileRsp {
    private String name;
    private String id;
    private String cardType;
    private MyPoint myPoint;
    private String typeCard;

    @Data
    public static class MyPoint{
        private Integer totalPoint;
        private List<Points> points;
        private CommonTrueProfileRsp.LoyaltyProgramMember loyaltyProgramMember;
    }
}
