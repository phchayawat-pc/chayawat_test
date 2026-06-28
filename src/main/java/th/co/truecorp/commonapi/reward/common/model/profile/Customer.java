package th.co.truecorp.commonapi.reward.common.model.profile;

import lombok.Data;

@Data
public class Customer {
	
	private String certificateNumber;
	
	private String custNumber;
	
	private String certificateType;
	
	private CustomerType customerType;
	
	private Subscriber subscriber;
	
	public Customer() {
		this.customerType = new CustomerType();
		this.subscriber = new Subscriber();
	}

}
