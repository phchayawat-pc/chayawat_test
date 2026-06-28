package th.co.truecorp.commonapi.reward.common.model;

import lombok.Data;


@Data
public class CommonTrueProfileRsp {
    private String id;
    private String typeCode;
    private Card card;

    @Data
    public static class Card{
        private String type;
    }
    @Data
    public static class Mypoint{
        private Integer totalPoint;
        private String unit;
        private Points points;
    }
    @Data
    public static class LoyaltyProgramMember{
        private String id;
        private String href;
    }

}
