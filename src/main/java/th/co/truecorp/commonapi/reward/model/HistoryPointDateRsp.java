package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

import java.util.List;

@Data
public class HistoryPointDateRsp {
    private List<HistoryPointRsp> point;
    private List<HistoryPointRsp> redemption;

}
