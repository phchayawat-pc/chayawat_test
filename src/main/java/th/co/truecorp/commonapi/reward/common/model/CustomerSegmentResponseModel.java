package th.co.truecorp.commonapi.reward.common.model;

import lombok.Data;

@Data
public class CustomerSegmentResponseModel {
    private String id;
    private String status;
    private String segmentCode;
    private String firstName;
    private String lastName;
    private String hasCard;
    private TrueCard trueCard;
    private AccountGrade accountGrade;
    private String code;
    private String description;
    private String timestamp;
    private String StatusCode;
}
