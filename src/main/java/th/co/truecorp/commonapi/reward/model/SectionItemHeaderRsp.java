package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

@Data
public class SectionItemHeaderRsp {
    private String itemNo;
    private String itemName;
    private String itemDisplayName;
    private ShelfSectionImageHeaderRsp itemImageList;
    private String itemType;
    private String itemSubtype;
    private String shelfType;
    private String itemMapping;
    private String itemMapping2;
    private String dummyFlag;
}
