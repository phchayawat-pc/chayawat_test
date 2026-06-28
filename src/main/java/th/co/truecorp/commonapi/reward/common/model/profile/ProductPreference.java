package th.co.truecorp.commonapi.reward.common.model.profile;

import lombok.Data;

@Data
public class ProductPreference {
	
	private String size;
	
	private String system;
	
	private String mainConvergenceCode;
	
	private Convergence convergenceList;
	
	private Customer customer;
	
	private Account account;
	
	private Subscriber subscriber;
	
	public ProductPreference() {
		this.convergenceList = new Convergence();
		this.customer = new Customer();
		this.account = new Account();
		this.subscriber = new Subscriber();
	}

}
