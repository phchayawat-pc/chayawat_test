package th.co.truecorp.commonapi.reward.common.model.profile;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Convergence {
	
	private String size;
	
	private List<ConvergenceInfo> convergenceInfoArray;
	
	public Convergence() {
		this.convergenceInfoArray = new ArrayList<>();
	}

}
