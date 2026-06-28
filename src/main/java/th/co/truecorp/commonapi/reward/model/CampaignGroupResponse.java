package th.co.truecorp.commonapi.reward.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignGroupResponse {
	private String id;
	private String code;
	private String description;
	private LocalDateTime timestamp;
	private DataContainer data;
	private String message;
	private String businessError;
	private String error;
}


