package th.co.truecorp.commonapi.reward.model.jsonResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import th.co.truecorp.commonapi.reward.common.dto.PageDTO;
import th.co.truecorp.commonlib.log.model.TransactionResult;
import th.co.truecorp.commonlib.ws.model.GenericJsonResponse;

import java.util.HashMap;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShelfGetLayoutPageJsonResponse<T> extends GenericJsonResponse<T> {
    private Object debug;
    private PageDTO paging;


    public void setPaging(PageDTO paging){
        this.paging = paging;
    }
    public void setDebug(Object debug){
        this.debug = debug;
    }

    public PageDTO getPaging(){
        return paging;
    }
    public Object getDebug(){
        return debug;
    }
    public ShelfGetLayoutPageJsonResponse(HashMap<String, Object> tv, TransactionResult transactionResult, T resp, Object debug, PageDTO page) {
        super(tv, transactionResult, resp);
        if(resp == null){
            this.setData(null);
        } else {
            this.setPaging(page);
            this.setDebug(debug);
        }
    }

}
