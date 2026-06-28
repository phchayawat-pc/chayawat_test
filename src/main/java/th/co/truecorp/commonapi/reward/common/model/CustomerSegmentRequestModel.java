package th.co.truecorp.commonapi.reward.common.model;

import lombok.Data;

@Data
public class CustomerSegmentRequestModel {
    private String id;
    private String phoneNumber;
    private String customerNumber;
    private String href;
    private String Type;
    private String customerIdnNo;
    private String fields;
    private String idnType;
    private String extensions;
    private User user;
    private RelatedParty relatedParty;
}
