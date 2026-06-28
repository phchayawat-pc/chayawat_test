package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

import java.util.List;

@Data
public class ShelfDtacInquiryShelfDetailApiRsp {
    private String id;
    private List<Pattern> pattern;

    @Data
    public static class Pattern{
        private String type;
        private List<PromotionPattern> promotionPattern;

        @Data
        public static class PromotionPattern{
            private String id;
            private String name;
            private String type;
            private String relationTypeInGroup;
            private String href;
            private String description;
            private String shortDescription;
            private Banner banner;
            private ValidFor validFor;
            private RelatedParty relatedParty;
            private PromotionCriteriaGroup promotionCriteriaGroup;

        }

        @Data
        public static class Banner {
            private String id;
            private String type;
            private String category;
            private String subCategory;
            private String href;
            private String description;
            private List<Campaign> campaigns;
        }

        @Data
        public static class Campaign {
            private String id;
            private String name;
            private String href;
            private String type;
            private String relationTypeInGroup;
            private RelatedParty relatedParty;
            private Banner banner;
            private PromotionCriteriaGroup promotionCriteriaGroup;
            private ValidFor validFor;
            private List<PromotionCriteria> promotionCriteria;
        }

        @Data
        public static class ValidFor {
            private String startDateTime;
            private String endDateTime;
            private String remainingDays;

        }

        @Data
        public static class RelatedParty {
            private String name;
            private String type;
            private String href;
            private String description;
        }

        @Data
        public static class PromotionCriteriaGroup {
            private String id;
            private String type;
            private String description;
            private String shortDescription;
            private List<PromotionCriteria> promotionCriteria;
        }

        @Data
        public static class PromotionCriteria {
            private String criteriaPara;
            private String criteriaValue;
        }
    }



}