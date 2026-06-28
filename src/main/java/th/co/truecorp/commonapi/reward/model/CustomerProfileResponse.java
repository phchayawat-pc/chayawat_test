package th.co.truecorp.commonapi.reward.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import th.co.truecorp.commonlib.log.model.TransactionResult;

import java.util.HashMap;
import java.util.Objects;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class CustomerProfileResponse<T> {

    private String message;

    private T data;

    public CustomerProfileResponse(HashMap<String, Object> tv, TransactionResult transactionResultdata) {

        T dataObject = (T) tv.get("data");
        this.data = dataObject;

        this.message = transactionResultdata.getErrorMessage() != null || !Objects.equals(transactionResultdata.getErrorMessage(), "") ? transactionResultdata.getErrorMessage() : "" ;

    }

}