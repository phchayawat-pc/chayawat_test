package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class ShelfThematicHeaderRsp {
    private String campaignId;
    private String templateCode;
    private String sectionId;
    private thumbnail thumbnailList;
    private String campaignName;
    private String campaignDescription;
    private String campaignExpireDate;
    private String campaignType;
    private List<String> cardType = new ArrayList<>();
    private String autoSlideDisplayName;
    private List<autoSlide> autoSlideList  = new ArrayList<>();


    @Data
    public static class thumbnail{
        private String thumbnail3x2;
        private String thumbnail4x3;
        private String thumbnail16x9;
    }

    @Data
    public static class autoSlide{
        private String itemNo;
        private String itemName;
        private String itemDisplayName;
        private itemImage itemImageList;
        private String itemType;
        private String itemSubtype;
        private String shelfType;
        private String itemMapping;
        private String itemMapping2;
    }

    @Data
    public static class itemImage{
        private String image3x2;
        private String image4x3;
        private String image16x9;
        private String image9x16;

    }
}
