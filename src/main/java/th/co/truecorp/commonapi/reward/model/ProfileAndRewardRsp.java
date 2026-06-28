package th.co.truecorp.commonapi.reward.model;

import lombok.Data;
import th.co.truecorp.commonapi.reward.common.model.CommonTrueProfileRsp;
import th.co.truecorp.commonapi.reward.common.model.Points;

import java.util.List;

@Data
public class ProfileAndRewardRsp {
    private CustomerProfileRsp customerProfileRsp;
    private ServiceProfileRsp serviceProfileRsp;
}
