package th.co.truecorp.commonapi.reward.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShelfContentDataApiRsp {
    private String code;
    private String description;
    private String timestamp;
    private String message;
    private String businessError;
    private String error;
    private ContentData content;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContentData {
        private int code;
        private int platform_module;
        private int report_dashboard;
        private String message;
        private DataDetails data;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class DataDetails {
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
            private Integer count_views;
            private String publish_date;
            private String create_date;
            private String update_date;
            private String searchable;
            private String create_by;
            private String create_by_ssoid;
            private String update_by;
            private String update_by_ssoid;
            private String source_url;
            private Integer count_likes;
            private CountRatings count_ratings;
            private String source_country;
            private List<DataDetails> privilege_list;
            private String detail;
            private ThumbList thumb_list;
            private String expire_date;
            private InfoData info;
            private List<String> allow_app;
            private SettingData setting;
            private String share_url;
            private String term_and_condition;
            private String shelf_content_type;
            private String show_redeem_button;
            private String campaign_type;
            private String sub_campaign_type;
            private String campaign_code;
            private String redeem_point;
            private String item_type;
            private String banner;
            private String highlight16x9;
            private String thumbnail;
            private List<String> card_type;

        }

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ThumbList {
        private String banner;
        private String highlight;
        private String highlight16x9;
        private String thumbnail;
        private String logo_m;
        private String logo_s;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InfoData {
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
        private String enable_review;
        private String tel_no;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SettingData {
        private String deeplink;
        private String thumb_en;
        private String thumb_th;
        private String title_en;
        private String title_th;
        private String truecard_type;
        private String time_counter_show;
        private String thematic_main_shelf_ids;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CountRatings {
        private String avg;
        private String total;
    }
}

