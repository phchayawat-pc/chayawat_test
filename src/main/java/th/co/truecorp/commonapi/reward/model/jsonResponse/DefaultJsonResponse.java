package th.co.truecorp.commonapi.reward.model.jsonResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonlib.log.model.TransactionResult;

import java.util.HashMap;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class DefaultJsonResponse<T> {

	private String message;

	private T data;


	public DefaultJsonResponse(HashMap<String, Object> tv, TransactionResult transactionResultdata) {

		T dataObject = (T) tv.get(Constant.TRANSACTION_RESPONSE_KEY);
		this.data = dataObject;

		this.message = transactionResultdata.getErrorMessage() != null || !Objects.equals(transactionResultdata.getErrorMessage(), "") ? transactionResultdata.getErrorMessage() : "" ;

	}
}