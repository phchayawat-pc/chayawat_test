package th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemContent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentData {
    private int code;
    private int platform_module;
    private int report_dashboard;
    private String message;
    private DataItem data;
}
