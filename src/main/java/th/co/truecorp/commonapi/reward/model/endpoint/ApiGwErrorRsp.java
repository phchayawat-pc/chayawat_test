package th.co.truecorp.commonapi.reward.model.endpoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiGwErrorRsp {
    private String code;
    private String description;
    private String timestamp;
    private String message;
    private String businessError;
    private String error;
}
