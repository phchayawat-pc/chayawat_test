package th.co.truecorp.commonapi.reward.model.endpoint.redeem.earn.dtacenv;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtacPointAndCoinReq {

	private List<Characteristic> characteristic;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Characteristic {
		private String name;
		private String value;

	}

}
