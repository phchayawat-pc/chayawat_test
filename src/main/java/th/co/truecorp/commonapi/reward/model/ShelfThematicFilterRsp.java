package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

import java.util.List;

@Data
public class ShelfThematicFilterRsp {
    private String campaignId;
    private String templateCode;
    private String lang;
    private List<FilterItem> filterItem;

    @Data
    public static class FilterItem {
        private String itemNo;
        private String itemName;
        private String itemDisplayName;
        private ItemImage itemImageList;
        private String itemType;
        private String itemSubtype;
        private String shelfType;
        private String itemMapping;
        private String itemMapping2;
    }

    @Data
    public static class ItemImage {
        private String imageIcon;
        private String image1x1;
        private String image3x2;
        private String image4x3;
        private String image16x9;
        private String image9x16;
    }

}
