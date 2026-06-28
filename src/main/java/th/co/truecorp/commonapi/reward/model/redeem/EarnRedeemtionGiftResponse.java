package th.co.truecorp.commonapi.reward.model.redeem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class EarnRedeemtionGiftResponse {
	private Integer earnNumber;
	private String trxId;
}
