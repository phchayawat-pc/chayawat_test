package th.co.truecorp.commonapi.reward.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import th.co.truecorp.commonapi.reward.common.dto.PageDTO;
import th.co.truecorp.commonlib.log.model.TransactionResult;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class RedeemListResponse {

	private String appInfo;
	private String message;

	private List<RedeemListRsp> data;
	private PageDTO paging;


	public RedeemListResponse(HashMap<String, Object> tv, TransactionResult transactionResultdata) {

		List<RedeemListRsp> dataObject = (List<RedeemListRsp>) tv.get("data");
		this.message = transactionResultdata.getErrorMessage() != null || !Objects.equals(transactionResultdata.getErrorMessage(), "") ? transactionResultdata.getErrorMessage() : "" ;
		PageDTO page1 = (PageDTO) tv.get("page");
		this.data = dataObject;
		this.paging = page1;
	}
}