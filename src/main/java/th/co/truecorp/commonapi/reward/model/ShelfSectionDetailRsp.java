package th.co.truecorp.commonapi.reward.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class ShelfSectionDetailRsp {

	private String sectionId;
	private String lang;
	private String displayTypeCode;
	private String expireDate;
	private List<ShelfSectionDetailItemRsp> sectionDetailItem;

}