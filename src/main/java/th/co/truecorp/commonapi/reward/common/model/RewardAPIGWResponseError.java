package th.co.truecorp.commonapi.reward.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewardAPIGWResponseError {
    private String code;
    private String description;
    private String timestamp;
}
