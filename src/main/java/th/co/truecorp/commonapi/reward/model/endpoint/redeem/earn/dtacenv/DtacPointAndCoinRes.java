package th.co.truecorp.commonapi.reward.model.endpoint.redeem.earn.dtacenv;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtacPointAndCoinRes {

	private String id;
	private Quantity quantity;
	private List<Characteristic> characteristic;
	private String code;
	private String error;
	private String message;
	private String timestamp;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Quantity {
		private String unit;
		private String balance;

	}
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Characteristic {
		private String name;
		private String value;

	}

}
