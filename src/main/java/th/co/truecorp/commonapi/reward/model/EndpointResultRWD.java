package th.co.truecorp.commonapi.reward.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EndpointResultRWD {
    @JsonProperty("srvStatus")
    protected String endpointStatusType;
    @JsonIgnore
    protected String endpointStatusCode;
    @JsonProperty("srvResCode")
    protected String endpointResponseCode;
    @JsonProperty("srvErrCode")
    protected String endpointErrorCode;
    @JsonProperty("srvErrMsg")
    protected String endpointErrorMessage;
    @JsonIgnore
    protected String endpointErrorDescription;
    @JsonIgnore
    protected int httpStatus;
}
