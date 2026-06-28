package th.co.truecorp.commonapi.reward.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class LayoutDetailDTO {
    private String layoutId;
    private String brandCode;
    private String productTypeCode;
    private String chargeTypeCode;
    private Integer seqNo;
    private String sectionId;
    private String displayHeaderFlag;
    private String displayTypeCode;
    private String usedContentCmsFlag;
    private String autoSlide;
    private String seeAllFlag;
    private String gotoSectionId;
    private Timestamp startDate;
    private Timestamp endDate;
    private String templateCode;
    private String sectionName;
    private String displayNameType;
    private String sectionDisplayName;
    private String sectionDisplayImage;
    private String sectionDisplayNameEn;
    private String sectionDisplayImageEn;
    private String displayImageLang;
    private String displayImageEn;
    private String sectionDisplayNameLang;

    // Getters and setters
}
