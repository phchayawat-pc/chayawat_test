package th.co.truecorp.commonapi.reward.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import th.co.truecorp.commonapi.reward.common.model.Points;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetProfileResponse {

    private Status status;
    private Data data;

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Status {
        private String statusType;
        private String errorCode;
        private String errorMessage;
        private String errorDescription;
        private String hostId;
        private String transactionId;
        private String description;
    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Data {
        private Profile profile;
        private Object parent;
        private Object packageCurrent;
        private Reward reward;
        private Object nontelco;
        private Object sun;
    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Profile {
        private String mssidn;
        private Object id;
        private Object businessLine;
        private Object telephoneType;
        private Object customerType;
        private Object status;
    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Reward {
        private String statusType;
        private String errorCode;
        private String errorMessage;
        private String errorDescription;
        private String hostId;
        private String transactionId;
        private String id;
        private String typeCard;
        private String cardName;
        private String detailResponseData;
        private Card card;
        private MyPoint myPoint;
    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Card {
        private String type;
        private Object typeCode;
        private Object no;
        private Object mastercardNo;
        private Object expirationDate;
    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MyPoint {
        private int totalPoint;
        private String unit;
        private List<Points> points;
        private Object loyaltyProgramMember;
    }

//    @Getter
//    @Setter
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    public static class Points {
//        private String expirationDate;
//        private Object points;
//    }
}

