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
public class HistoryPointResponse {

	private String appInfo;
	private String message;

	private HistoryPointDateRsp data;
	private PageDTO paging;


	public HistoryPointResponse(HashMap<String, Object> tv, TransactionResult transactionResultdata) {

		List<HistoryPointRsp> dateTabPoint = (List<HistoryPointRsp>) tv.get("DateTabPoint");
		List<HistoryPointRsp> dateTabRedemption = (List<HistoryPointRsp>) tv.get("DateTabRedemption");
		HistoryPointDateRsp dataObject = new HistoryPointDateRsp();
		if(dateTabPoint != null){
			dataObject.setPoint(dateTabPoint);
		}
		if(dateTabRedemption != null){
			dataObject.setRedemption(dateTabRedemption);
		}
		PageDTO page1 = (PageDTO) tv.get("page");

		this.data = dataObject;
		this.paging = page1;

		this.message = transactionResultdata.getErrorMessage() != null || !Objects.equals(transactionResultdata.getErrorMessage(), "") ? transactionResultdata.getErrorMessage() : "" ;

	}
}