package th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemContent;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrivilegeData {
    private String code;
    private String description;
    private String timestamp;
    private ContentData content;
}
