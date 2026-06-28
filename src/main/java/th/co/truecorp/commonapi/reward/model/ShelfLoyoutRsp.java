package th.co.truecorp.commonapi.reward.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class ShelfLoyoutRsp {

	private String brandCode;
	private String productType;
	private String chargeType;
	private String layOutId;
	private String lang;
	private List<ShelfLoyoutDetailRsp> itemList;

	@Override
	public String toString(){
		return "[ShelfLoyoutRsp] brandCode : " + getBrandCode()
				+" ,productType : " + getProductType()
				+" ,chargeType : " + getChargeType()
				+" ,layOutId : " + getLayOutId()
				+" ,lang : " + getLang()
				+" ,itemList : " + getItemList();
	}
}