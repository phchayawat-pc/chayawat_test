package th.co.truecorp.commonapi.reward.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class ShelfSectionNoneRelatedRsp {

	private String sectionId;
	private String lang;
	private String displayTypeCode;
	private List<SectionItemDetailRsp> sectionItem;

	@Data
	public static class SectionItemDetailRsp {
		private Integer itemNo;
		private String itemName;
		private String itemDisplayName;
		private SectionItemImageDetailRsp itemImageList;
		private String itemType;
		private String itemSubtype;
		private String shelfType;
		private String itemMapping;
		private String itemMapping2;

		@Data
		public static class SectionItemImageDetailRsp {
			private String imageIcon;
			private String image1x1;
			private String image3x2;
			private String image4x3;
			private String image16x9;
			private String image9x16;
		}
	}
}