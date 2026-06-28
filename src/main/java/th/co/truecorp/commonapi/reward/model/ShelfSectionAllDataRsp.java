package th.co.truecorp.commonapi.reward.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
public class ShelfSectionAllDataRsp {
    private String sectionId;
    private String lang;
    private String displayTypeCode;
    private Date expireDate;
    private List<DealList> dealList;

    @Data
    public static class DealList{
        private Integer itemNo;
        private String itemName;
        private String itemDisplayName;
        private ThumbnailList itemImageList;
        private String itemType;
        private String itemSubtype;
        private String shelfType;
        private String itemMapping;
        private String itemMapping2;
        private List<ItemList> itemList;

        @Data
        public static class ThumbnailList{
            private String imageIcon;
            private String image1x1;
            private String image3x2;
            private String image4x3;
            private String image16x9;
            private String image9x16;
        }

        @Data
        public static class ItemList{
            private Integer seqNo;
            private String campaignId;
            private String campaignCode;
            private String contentType;
            private String timeCounterFlag;
            private ThumbnailList thumbnailList;
            private String campaignName;
            private String campaignDescription;
            private String campaignExpireDate;
            private String campaignType;
            private List<String> cardType;
            private String regularPoint;
            private String offerPoint;

            @Data
            public static class ThumbnailList{
                private String thumbnail4x3;
                private String thumbnail16x9;
            }
        }
    }

}
