package th.co.truecorp.commonapi.reward.cms.jpa.dto;

import lombok.*;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdCustomMappingMessage;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdErrMapping;

import java.beans.ConstructorProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RwdErrMappingDTO {
    private String action;
    private String errCode;
    private String customCode;
    private String message;
    private String httpCode;
    private String brandCode;
    private String lang;
    private String description;
    private String systemErrorCode;
    private String displayType;
}

