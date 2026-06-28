package th.co.truecorp.commonapi.reward.cache.reward.model.layout;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import th.co.truecorp.commonapi.reward.constant.Constant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetLayoutRequest {
    private String id;
    private String channel;

    public GetLayoutRequest(String layoutId) {
        this.id = layoutId;
        this.channel = Constant.TRUEAPP;
    }
}
