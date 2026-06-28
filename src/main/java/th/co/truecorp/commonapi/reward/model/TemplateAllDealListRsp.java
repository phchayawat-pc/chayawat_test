package th.co.truecorp.commonapi.reward.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class TemplateAllDealListRsp {

	private Integer seqNo;
	private String contentType;
	private String campaignId;
	private String campaignCode;
	private String timeCounterFlag;
	private ThumbnailListRsp thumbnailList;
	private String campaignName;
	private String campaignDescription;
	private String campaignExpireDate;
	private String campaignType;
	private List<String> cardType;
	private String regularPoint;
	private String offerPoint;

	@Override
	public String toString(){
		return "[TemplateAllDealListRsp] seqNo : " + getSeqNo()
				+" ,contentType : " + getContentType()
				+" ,campaignId : " + getCampaignId()
				+" ,campaignCode : " + getCampaignCode()
				+" ,timeCounterFlag : " + getTimeCounterFlag()
				+" ,thumbnailList : " + getThumbnailList()
				+" ,campaignName : " + getCampaignName()
				+" ,campaignDescription : " + getCampaignDescription()
				+" ,campaignExpireDate : " + getCampaignExpireDate()
				+" ,campaignType : " + getCampaignType()
				+" ,cardType : " + getCardType()
				+" ,regularPoint : " + getRegularPoint()
				+" ,offerPoint : " + getOfferPoint();
	}
}