package th.co.truecorp.commonapi.reward.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class TemplateAllRsp {

	private String sectionId;
	private String lang;
	private String sectionName;
	private String sectionHeader;
	private String templateCode;
	private List<TemplateAllDealListRsp> dealList;

	@Override
	public String toString(){
		return "[TemplateAllRsp] sectionId : " + getSectionId()
				+" ,lang : " + getLang()
				+" ,sectionName : " + getSectionName()
				+" ,sectionHeader : " + getSectionHeader()
				+" ,templateCode : " + getTemplateCode()
				+" ,dealList : " + getDealList();
	}
}