package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

import java.util.List;

@Data
public class ShelfSectionDetailDtacInquiryShelfDetailRsp {

    private String code;
    private String description;
    private String timestamp;
    private String message;
    private String businessError;
    private String error;
    private String id;
    private String lifecycleStatus;
    private String name;
    private String listMode;
    private ValidFor validFor;
    private List<PromotionPattern> promotionPattern;

    @Data
    public static class ValidFor {
        private String startDateTime;
        private String endDateTime;
        private String remainingDays;

    }

    @Data
    public static class PromotionPattern{
        private String id;
        private String href;
        private String name;
        private String priority;
        private String description;
        private String shortDescription;
        private String type;
        private String category;
        private String subCategory;
        private ValidFor validFor;
        private List<RelatedParty> relatedParty;
        private List<PromotionCriteriaGroup> promotionCriteriaGroup;
        private List<PromotionAction> promotionAction;
        private Banner banner;
    }

    @Data
    public static class Banner {
        private String id;
        private String type;
        private String name;
        private String href;
        private String description;
        private List<Campaign> campaigns;
    }

    @Data
    public static class Campaign {
        private String id;
        private String name;
        private String href;
        private String relationTypeInGroup;
        private String promotionType;
        private String type;
        private ValidFor validFor;
        private RelatedParty relatedParty;
        private Banner banner;
        private List<PromotionCriteria> promotionCriteria;
        private PromotionCriteriaGroup promotionCriteriaGroup;
        private String listMode;
    }

    @Data
    public static class RelatedParty {
        private String id;
        private String name;
        private String href;
        private String description;
    }

    @Data
    public static class PromotionCriteriaGroup {
        private String id;
        private String groupName;
        private String name;
        private String type;
        private String href;
        private String description;
        private String shortDescription;
        private ValidFor validFor;
        private String relationTypeInGroup;
        private List<PromotionCriteria> promotionCriteria;
        private List<PromotionAction> promotionAction;
        private List<RelatedParty> relatedParty;
    }

    @Data
    public static class PromotionAction {
        private String actionType;
        private String actionValue;
        private String href;
        private String recommended;
    }

    @Data
    public static class PromotionCriteria {
        private String id;
        private String criteriaPara;
        private String criteriaValue;
    }

}