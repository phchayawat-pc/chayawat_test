package th.co.truecorp.commonapi.reward.common.model.profile;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class GetCustPrfByProdIdResAPIGW {
	
	private String id;
	
	private String code;
	
	private String description;
	
	private String timestamp;
	
	private String size;
	
	private List<ProductPreference> productPreferenceList;

	public GetCustPrfByProdIdResAPIGW() {
		this.productPreferenceList = new ArrayList<>();
	}
	
}
