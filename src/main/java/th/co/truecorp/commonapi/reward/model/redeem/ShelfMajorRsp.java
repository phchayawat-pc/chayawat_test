package th.co.truecorp.commonapi.reward.model.redeem;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class ShelfMajorRsp {
    private String majorId;
    private String lang;
    private String majorName;
    private Thumbnail thumbnailList;
    private String templateCode;
//    private String sectionId;
    private List<DealList> dealList;

    @Data
    public static class DealList{
        private String seqNo;
        private String campaignId;
        private String campaignCode;
        private String contentType;
        private String timecounterFlag;
        private Thumbnail thumbnailList;
        private String campaignName;
        private String campaignDescription;
        private String campaignExpireDate;
        private String campaignType;
        private List<String> cardType;
        private String regularPoint;
        private String offerPoint;
    }

    @Data
    public static class Thumbnail{
        private String thumbnail3x2;
        private String thumbnail4x3;
        private String thumbnail16x9;
        private String thumbnail18x7;
    }

}
