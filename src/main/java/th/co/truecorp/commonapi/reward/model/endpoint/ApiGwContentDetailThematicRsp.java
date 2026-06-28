package th.co.truecorp.commonapi.reward.model.endpoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiGwContentDetailThematicRsp {
    private String code;
    private String description;
    private String timestamp;
    private String message;
    private String businessError;
    private String error;
    private ContentDetail content;

    // Getters and Setters

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContentDetail {
        private String code;
        private Integer platform_module;
        private Integer report_dashboard;
        private String message;
        private DataDetail data;

        // Getters and Setters
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataDetail {
        private String display_country;
        private String display_lang;
        private String id;
        private String content_type;
        private String original_id;
        private String title;
        private List<String> article_category = new ArrayList<>();
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
        private String count_watch_later;
        private String source_country;
        private String detail;
        private ThumbData thumb_list;
        private String expire_date;
        private SettingData setting;
        private String term_and_condition;
        private String share_url;

        // Getters and Setters
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ThumbData {
        private String highlight16x9;
        private String image_coupon;

        // Getters and Setters
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SettingData {
        private String shelf_id_1;
        private String shelf_ids;
        private String shelf_title_1;
        private String thematic_main_shelf_ids;
        private String themetic_color;

        // Getters and Setters
    }
}
