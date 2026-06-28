package th.co.truecorp.commonapi.reward.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class ShelfTemplateHighlightRsp {
    private String sectionId;
    private String lang;
    private String templateCode;
    private List<HighlightItem> highlightItem;

    @Data
    public static class HighlightItem{
        private String itemNo;
        private String itemName;
        private String itemDisplayName;
        private ItemImageList itemImageList;
        private String itemType;
        private String itemSubtype;
        private String shelfType;
        private String itemMapping;
        private String itemMapping2;

        @Data
        public static class ItemImageList{
            private String imageIcon;
            private String image1x1;
            private String image3x2;
            private String image4x3;
            private String image16x9;
            private String image9x16;
        }
    }

}
