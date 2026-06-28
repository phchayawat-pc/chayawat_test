package th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemContent;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InfoData {
    private String budget_save_amount;
    private String budget_save_currency_en;
    private String budget_save_currency_th;
    private String budget_save_show;
    private String budget_save_text_en;
    private String budget_save_text_th;
    private String default_code_format;
    private String ex_link;
    private String merchant_id;
    private String merchant_name_en;
    private String merchant_name_th;
    private String privilege_version;
    private String requireLocation;
    private String text_redeem_btn;
    private String time_counter_show;

    // Getters and setters
}
