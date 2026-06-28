package th.co.truecorp.commonapi.reward.model.jsonResponse;

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
public class ProfileDefaultResponse<T> {

	private String message;

	private T data;


	public ProfileDefaultResponse(HashMap<String, Object> tv, TransactionResult transactionResultdata) {

		T dataObject = (T) tv.get(Constant.TRANSACTION_RESPONSE_KEY);
		this.data = dataObject;

		this.message = transactionResultdata.getErrorMessage() != null || !Objects.equals(transactionResultdata.getErrorMessage(), "") ? transactionResultdata.getErrorMessage() : "" ;

	}
}