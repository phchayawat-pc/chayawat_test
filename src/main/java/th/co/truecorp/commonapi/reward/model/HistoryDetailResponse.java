package th.co.truecorp.commonapi.reward.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonlib.log.model.TransactionResult;

import java.util.HashMap;
import java.util.Objects;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class HistoryDetailResponse {

	private String appInfo;
	private String message;

	private HistoryDetailRsp data;


	public HistoryDetailResponse(HashMap<String, Object> tv, TransactionResult transactionResultdata) {

		HistoryDetailRsp dataObject = (HistoryDetailRsp) tv.get(Constant.TRANSACTION_RESPONSE_KEY);
		this.data = dataObject;

		this.message = transactionResultdata.getErrorMessage() != null || !Objects.equals(transactionResultdata.getErrorMessage(), "") ? transactionResultdata.getErrorMessage() : "" ;

	}
}