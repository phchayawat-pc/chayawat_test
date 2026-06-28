package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DataContainer {
	private Map<String, String> detailLanguage;
	private BannerInfo bannerInfo;
	private List<Campaign> campaigns;
}
