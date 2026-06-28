package th.co.truecorp.commonapi.reward.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class LayoutSectionDTO {
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
    private String startDate;
    private String endDate;
    private String templateCode;
    private String sectionName;
    private String displayNameType;
    private String sectionDisplayName;
    private String sectionDisplayImage;
    private String sectionDisplayNameEn;
    private String sectionDisplayImageEn;

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("[LayoutSectionDTO |");
        sb.append(" layoutId=").append(getLayoutId());
        sb.append(" brandCode=").append(getBrandCode());
        sb.append(" productTypeCode=").append(getProductTypeCode());
        sb.append(" chargeTypeCode=").append(getChargeTypeCode());
        sb.append(" seqNo=").append(getSeqNo());
        sb.append(" sectionId=").append(getSectionId());
        sb.append(" displayHeaderFlag=").append(getDisplayHeaderFlag());
        sb.append(" displayTypeCode=").append(getDisplayTypeCode());
        sb.append(" usedContentCmsFlag=").append(getUsedContentCmsFlag());
        sb.append(" autoSlide=").append(getAutoSlide());
        sb.append(" seeAllFlag=").append(getSeeAllFlag());
        sb.append(" gotoSectionId=").append(getGotoSectionId());
        sb.append(" startDate=").append(getStartDate());
        sb.append(" endDate=").append(getEndDate());
        sb.append(" templateCode=").append(getTemplateCode());
        sb.append(" sectionName=").append(getSectionName());
        sb.append(" displayNameType=").append(getDisplayNameType());
        sb.append(" sectionDisplayName=").append(getSectionDisplayName());
        sb.append(" sectionDisplayImage=").append(getSectionDisplayImage());
        sb.append(" sectionDisplayNameEn=").append(getSectionDisplayNameEn());
        sb.append(" sectionDisplayImageEn=").append(getSectionDisplayImageEn());
        sb.append("]");
        return sb.toString();
    }
}
