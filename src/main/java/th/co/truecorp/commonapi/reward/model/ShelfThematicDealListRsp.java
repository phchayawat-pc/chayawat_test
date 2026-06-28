package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ShelfThematicDealListRsp {
    private String campaignId;
    private String templateCode;
    private String lang;
    private List<shelfList> shelfList;

    @Data
    public static class shelfList {
        private int seqNo;
        private String shelfId;
        private String shelfName;
        private List<dealList> dealList;
    }

    @Data
    public static class dealList {
        private int seqNo;
        private String campaignId;
        private String campaignCode;
        private String contentType;
        private String timeCounterFlag;
        private thumbnailList thumbnailList;
        private String campaignName;
        private String campaignDescription;
        private String campaignExpireDate;
        private ArrayList<String> cardType;
        private Integer regularPoint;
        private Integer offerPoint;
    }

    @Data
    public static class thumbnailList{
        private String thumbnail4x3;
        private String thumbnail16x9;
    }

    @Data
    public static class cardType{
        private int regularPoint;
        private int offerPoint;
    }

}
