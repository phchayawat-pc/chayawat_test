package th.co.truecorp.commonapi.reward.model.endpoint;

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
public class ApiGwTransactionHistoryRsp {
    private String code;
    private String description;
    private String timestamp;
    private String message;
    private String businessError;
    private String error;
    private List<RedeemResult> result;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RedeemResult {
        private int accountId;
        private String campaignDescription;
        private String channel;
        private String channelName;
        private int customerId;
        private String date;
        private String identifierNo;
        private String identifierType;
        private int points;
        private String status;
        private String statusName;
        private String subType;
        private int totalValue;
        private long transactionId;
        private String trnNo;
        private String type;
        private String typeName;
        private String couponCode;
        private String messageOut;
        private RedeemContent content;
        private String campaignMid;
        private String couponStatus;
        private String campaignExpireDate;
        private String couponExpireDate;
        private String couponTimeCounter;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RedeemContent {
        private int code;
        private List<ContentData> data;
        private String message;
        private Pages pages;
        private int platform_module;
        private int report_dashboard;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Pages {
        private String cursor;
        private int limit;
        private int total_items;
        private int total_pages;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContentData {
        private String allow_recommend;
        private List<String> article_category;
        private List<ArticleCategoryDetail> article_category_detail;
        private String campaign_code;
        private String campaign_type;
        private List<String> card_type;
        private String content_type;
        private Integer count_likes;
        private Integer count_ratings;
        private Integer count_views;
        private String create_by;
        private String create_by_ssoid;
        private String create_date;
        private String deal_id;
        private String detail;
        private String discount_info;
        private String display_country;
        private String display_lang;
        private String end_date;
        private String expire_date;
        private String id;
        private Info info;
        private String original_id;
        private String product_id;
        private String publish_date;
        private int quota_over_existed;
        private String redeem_point;
        private Integer score;
        private String searchable;
        private Object setting;
        private String show_card;
        private String show_redeem_button;
        private String source_country;
        private String source_url;
        private String start_date;
        private String status;
        private String sub_campaign_type;
        private List<String> tags;
        private String term_and_condition;
        private String thumb;
        private ThumbList thumb_list;
        private String title;
        private String update_by;
        private String update_by_ssoid;
        private String update_date;
        private String ussd;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ArticleCategoryDetail {
        private String name;
        private String parent;
        private String slug;
        private List<ArticleCategoryDetail> sub_category;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Info {
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
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ThumbList {
        private String banner;
        private String highlight16x9;
        private String thumbnail;
    }
}
