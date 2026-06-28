package th.co.truecorp.commonapi.reward.common.model;

import lombok.Data;

@Data
public class AccountGrade {
    private String level;
    private String reasonCode;
    private String customerType;
    private String cardType;
}
