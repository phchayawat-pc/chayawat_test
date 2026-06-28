package th.co.truecorp.commonapi.reward.common.model;

import lombok.Data;

@Data
public class TrueCard {
    private String status;
    private String type;
    private String no;
    private String mastercardNo;
    private String expirationDate;
    private String gradingDate;
}
