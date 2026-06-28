package th.co.truecorp.commonapi.reward.cms.jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RwdCustomMappingMessageDTO {

    private String customCode;
    private String lang;
    private String displayType;
    private String action;
    private String message;
    private String description;

}

