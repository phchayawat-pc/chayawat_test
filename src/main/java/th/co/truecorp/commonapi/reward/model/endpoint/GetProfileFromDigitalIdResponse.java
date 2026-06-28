package th.co.truecorp.commonapi.reward.model.endpoint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import th.co.truecorp.commonlib.ws.model.StatusJsonResponse;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetProfileFromDigitalIdResponse {
    private StatusJsonResponse status;
    private GetProfileFromDigitalIdResponse.DataObj data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataObj {
        private ProfileInfo profile;
    }
    
}
