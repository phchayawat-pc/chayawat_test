package th.co.truecorp.commonapi.reward.cache.reward.model;

import lombok.*;
import th.co.truecorp.commonapi.reward.endpoint.RelatedParty;

import java.util.ArrayList;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GetCustomerProductDetailRequest {
    public String id;
    public String limit;
    public String page;
    public String type;
    public String channel;
    public RelatedParty relatedParty;
    public ArrayList<Characteristic> characteristic;
}
