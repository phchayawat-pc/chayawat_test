package th.co.truecorp.commonapi.reward.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class ShelfTemplateDetailRsp {
    private String sectionId;
    private String lang;
    private String templateCode;
    private String shelfId;
    private String expireDate;
    private List<DealList> sectionDetailItem;

    @Data
    public static class DealList{
        private Integer seqNo;
        private String contentType;
        private String campaignId;
        private String campaignCode;
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
            private String thumbnail3x2;
            private String thumbnail16x9;
        }
    }

}
