package th.co.truecorp.commonapi.reward.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class ContentCategoryResponse {

	private String code;
	private String description;
	private String timestamp;
	private Resource resource;
	private List<Characteristic> characteristic;
	private List<Content> content;

	private String message;
	private String businessError;
	private String error;

	@Data
	public static class Resource{
		private String limit;
		private String next;
		private String totalItem;
		private String totalPage;
	}

	@Data
	public static class Characteristic{
		private String name;
		private String value;
	}

	@Data
	public static class Content{
		private String id;
		private String type;
		private String originalID;
		private String title;
		private List<String> articleCategory;
		private List<String> tag;
		private String status;
		private ValidFor validFor;
		private Statistics statistics;
		private List<Characteristic> characteristic;
		private String createBy;
		private String createDate;
		private String createBySsoid;
		private String updateBy;
		private String updateDate;
		private String updateBySsoId;
		private String detail;
		private ThumbList thumbList;
		private Campaign campaign;
		private String redeemPoint;
		private List<String> cardType;
		private ContentSpecification contentSpecification;
		private AddtionalInfo addtionalInfo;
		private List<String> allowApp;
		private String setting;
		private List<Content> subContent;
		private ArticleCategoryDetail articleCategoryDetail;
	}

	@Data
	public static class ThumbList{
		private String banner;
		private Highlight highlight;
		private Logo logo;
		private String thumbnail;
	}

	@Data
	public static class Highlight{
		private String standard;
		private String highlight16x9;
	}

	@Data
	public static class Logo{
		private String sizeS;
		private String sizeM;
	}

	@Data
	public static class ValidFor{
		private String startDate;
		private String endDate;
	}

	@Data
	public static class Statistics{
		private String views;
		private String likes;
		private String watchLater;
		private String favorite;
		private Rating rating;
	}

	@Data
	public static class Rating{
		private String average;
		private String total;
	}

	@Data
	public static class Campaign{
		private String type;
		private String subType;
		private String id;
	}

	@Data
	public static class ContentSpecification{
		private List<Characteristic> characteristic;
	}


	@Data
	public static class AddtionalInfo{
		private String enableReview;
		private String telNo;
		private String budgetSaveAmount;
		private String budgetSaveShow;
		private String defaultCodeFormat;
		private String exLink;
		private Merchant merchant;
		private String privilegeVersion;
		private String requireLocation;
		private String textRedeemButton;
		private String timeCounterShow;
	}

	@Data
	public static class Merchant{
		private String id;
		private String nameEn;
		private String nameTh;
	}

	@Data
	public static class ArticleCategoryDetail{
		private String slug;
		private String name;
		private String parent;
		private SubCategory subCategory;
	}

	@Data
	public static class SubCategory{
		private String slug;
		private String name;
		private String parent;
	}

}