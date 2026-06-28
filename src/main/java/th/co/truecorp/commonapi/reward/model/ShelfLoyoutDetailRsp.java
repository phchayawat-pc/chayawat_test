package th.co.truecorp.commonapi.reward.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class ShelfLoyoutDetailRsp {

	private Integer seq;
	private String sectionId;
	private String sectionName;
	private String sectionDisplayType;
	private String sectionDisplayName;
	private String sectionDisplayImage;
	private String showSectionNameFlag;
	private String displaySectionType;
	private String autoSlideFlag;
	private String seeAllFlag;
	private String useCmsContent;
	private String goToSectionID;
	private String templateCode;

	@Override
	public String toString(){
		return "[ShelfLoyoutDetailRsp] seq : " + getSeq()
				+" ,sectionId : " + getSectionId()
				+" ,sectionName : " + getSectionName()
				+" ,sectionDisplayType : " + getSectionDisplayType()
				+" ,sectionDisplayName : " + getSectionDisplayName()
				+" ,sectionDisplayImage : " + getSectionDisplayImage()
				+" ,showSectionNameFlag : " + getShowSectionNameFlag()
				+" ,displaySectionType : " + getDisplaySectionType()
				+" ,autoSlideFlag : " + getAutoSlideFlag()
				+" ,seeAllFlag : " + getSeeAllFlag()
				+" ,useCmsContent : " + getUseCmsContent()
				+" ,goToSectionID : " + getGoToSectionID()
				+" ,templateCode : " + getTemplateCode();
	}
}