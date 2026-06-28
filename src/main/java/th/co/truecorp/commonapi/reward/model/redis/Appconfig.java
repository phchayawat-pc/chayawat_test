package th.co.truecorp.commonapi.reward.model.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appconfig {
    private String staticPassphrase;
    private Integer reqExprTokenSec;
    private Integer reqExprRefreshSec;
}
