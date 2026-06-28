package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class Campaign {
	private String id;
	private Map<String, String> nameLanguage;
	private String originalPoint;
	private String pointPerUnit;
	private String imageUrl;
	private String userLevel;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private List<Characteristic> characteristic;
}
