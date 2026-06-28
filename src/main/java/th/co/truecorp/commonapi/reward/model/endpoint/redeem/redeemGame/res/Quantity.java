package th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemGame.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Quantity {
    private String unit;
    private Integer balance;
}
