package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

import java.util.List;

@Data
public class ShelfSectionHeaderRsp {

    private String sectionId;
    private String lang;
    private String displayTypeCode;
    private List<SectionItemHeaderRsp> sectionItem;

}
