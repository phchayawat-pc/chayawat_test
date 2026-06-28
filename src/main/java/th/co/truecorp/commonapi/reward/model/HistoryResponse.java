package th.co.truecorp.commonapi.reward.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import th.co.truecorp.commonapi.reward.common.dto.PageDTO;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonlib.log.model.TransactionResult;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class HistoryResponse {

	private String appInfo;
	private String message;

	private List<HistoryDataRsp> data;
	private PageDTO paging;


	public HistoryResponse(HashMap<String, Object> tv, TransactionResult transactionResultdata) {

		List<HistoryDataRsp> dataObject = (List<HistoryDataRsp>) tv.get(Constant.TRANSACTION_RESPONSE_KEY);
		PageDTO page1 = (PageDTO) tv.get("page");

		this.data = dataObject;
		this.paging = page1;

		this.message = transactionResultdata.getErrorMessage() != null || !Objects.equals(transactionResultdata.getErrorMessage(), "") ? transactionResultdata.getErrorMessage() : "" ;

	}
}