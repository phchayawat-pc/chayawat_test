package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

@Data
public class EarnRequest {

	private String digitalId;
	
	private String campaignCode;
	
	private String bzbProductId;
	
	private String bzbAmount;

}
