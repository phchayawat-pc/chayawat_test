package th.co.truecorp.commonapi.reward.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class TemplateAllDealThumbnailRsp {

	private String thumbnail3x2;
	private String thumbnail4x3;
	private String thumbnail16x9;

	@Override
	public String toString(){
		return "[TemplateAllDealThumbnailRsp] thumbnail3x2 : " + getThumbnail3x2()
				+" ,thumbnail4x3 : " + getThumbnail4x3()
				+" ,thumbnail16x9 : " + getThumbnail16x9();
	}
}