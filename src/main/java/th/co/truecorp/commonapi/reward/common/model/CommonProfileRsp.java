package th.co.truecorp.commonapi.reward.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonProfileRsp {
    private String id; //Optional for True
    private String cardName; //True: firstname + lastname, Optional for Dtac
    private String typeCode; //Optional for True
    private Card card;
    private MyPoint myPoint;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Card{
        private String type;
        private String typeCode; //Optional for True
        private String no; //Optional for Dtac
        private String mastercardNo; //Optional for Dtac
        private String expirationDate; //Optional for Dtac

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPoint {
        private String id; //Optional for Dtac
        private int totalPoint;
        private String unit;
        private List<Point> points;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Point {
            private int point;
            private String expirationDate;
        }

        /*Optional for TRUE*/
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class LoyaltyProgramMember {
            private String id;
            private String href;
        }
    }

}
