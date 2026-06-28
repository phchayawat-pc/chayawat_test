package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProgressiveShelvesRsp {

    private String code;
    private String description;
    private String timestamp;
    private String message;
    private String businessError;
    private String error;
    private ProgressiveShelves progressive_shelves;

    // Getters and Setters

    @lombok.Data
    public static class ProgressiveShelves {
        private int code;
        private int platform_module;
        private int report_dashboard;
        private String message;
        private Pages pages;
        private Data data;

        // Getters and Setters
    }

    @lombok.Data
    public static class Pages {
        private int page;
        private int limit;
        private int total_items;
        private int total_pages;

        // Getters and Setters
    }

    @lombok.Data
    public static class Data {
        private String title;
        private String status;
        private String publish_date;
        private Setting setting; // Since setting has an empty object, you can use Map or create a class if it has fields.
        private String country;
        private String content_type;
        private String create_by;
        private String create_by_ssoid;
        private String create_date;
        private String update_date;
        private String update_by;
        private String update_by_ssoid;
        private String id;
        private String timestamp;
        private List<ShelfItem> shelf_items;

        // Getters and Setters
    }

    @lombok.Data
    public static class ShelfItem {
        private String id;
        private String content_type;
        private String lang;
        private String original_id;
        private String title;
        private String thumb;
        private String status;
        private String publish_date;
        private String expire_date;
        private String create_date;
        private String create_by;
        private String create_by_ssoid;
        private String update_date;
        private String update_by;
        private String update_by_ssoid;
        private ThumbList thumb_list;
        private ArrayList<String> card_type;
        private String campaign_type;
        private String sub_campaign_type;
        private String campaign_code;
        private Integer redeem_point;
        private String detail;
        private String term_and_condition;
        private Info info;
        private ArrayList<String> allow_app;
        private Setting setting;
        private String item_type;

        // Getters and Setters
    }

    @lombok.Data
    public static class Setting {
        private String gradient_background_end;
        private String gradient_background_start;
        private String view_type;
        private boolean is_hide_see_more;
        private String placement_id;
        private String title_th;
        private String limit;
        private String web_limit;
        private String auto_time_slide;
        private String title_my;
        private String title_en;
        private String shelf_id;
        private String id;
        private String thematic_main_shelf_ids;
        private String deeplink;
        private String thumb_en;
        private String thumb_th;
        private String truecard_type;
        private String time_counter_show;

        // Getters and Setters
    }

    @lombok.Data
    public static class ThumbList {
        private String highlight;
        private String banner;
        private String highlight16x9;
        private String thumbnail;
        private String logo_m;
        private String logo_s;
    }

    @lombok.Data
    public static class Info {
        private String budget_save_amount;
        private String budget_save_show;
        private String default_code_format;
        private String ex_link;
        private String privilege_version;
        private String requireLocation;
        private String text_redeem_btn;
        private String time_counter_show;
        private String budget_save_currency_en;
        private String budget_save_currency_th;
        private String budget_save_text_en;
        private String budget_save_text_th;
        private String merchant_id;

        private String merchant_name_th;
        private String merchant_name_en;
    }


}
