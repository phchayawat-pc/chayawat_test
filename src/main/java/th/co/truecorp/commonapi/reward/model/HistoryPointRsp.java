package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

import java.util.Comparator;
import java.util.List;

@Data
public class HistoryPointRsp {
    private String campaignId;
    private String couponExpiryDate;
    private List<Condition> conditionInfo;
    private String bottomLink;
    private String textButton;
    private ThumbnailRsp thumbnail;
    private String campaignName;
    private String campaignDescription;
    private String points;
    private String date;
    private String couponCode;
    private String campaignType;
    private String type;
    private String timeCounterFlag;

    public static Comparator<HistoryPointRsp> StrDataComparator = new Comparator<HistoryPointRsp>() {

        // Comparing attributes of students
        public int compare(HistoryPointRsp s1, HistoryPointRsp s2) {
            String StrData1 = s1.getDate();
            String StrData2 = s2.getDate();

            // Returning in ascending order
//            return StrData1.compareTo(StrData2);
            return StrData2.compareTo(StrData1);
        }
    };
}
