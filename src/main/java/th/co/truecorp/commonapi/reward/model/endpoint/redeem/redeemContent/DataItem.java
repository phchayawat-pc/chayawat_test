package th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemContent;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataItem {
    private String display_country;
    private String display_lang;
    private String id;
    private String content_type;
    private String original_id;
    private String title;
    private List<String> article_category;
    private String thumb;
    private List<String> tags;
    private String status;
    private String count_views;
    private String publish_date;
    private String create_date;
    private String update_date;
    private String searchable;
    private String create_by;
    private String create_by_ssoid;
    private String update_by;
    private String update_by_ssoid;
    private String source_url;
    private String count_favorites;
    private String count_likes;
    private String count_ratings;
    private String countWatchLater;
    private String source_country;
    private String show_redeem_button;
    private List<String> card_type;
    private String campaign_type;
    private String sub_campaign_type;
    private String campaign_code;
    private String detail;
    private String redeem_point;
    private ThumbList thumb_list;
    private String expire_date;
    private InfoData info;
    private List<String> allow_app;
//    private String setting;
    private String term_and_condition;
    private String share_url;
}
