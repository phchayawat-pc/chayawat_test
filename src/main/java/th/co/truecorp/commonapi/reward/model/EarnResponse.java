package th.co.truecorp.commonapi.reward.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EarnResponse {

	private Integer earnNumber;

	private String trxId;

}
