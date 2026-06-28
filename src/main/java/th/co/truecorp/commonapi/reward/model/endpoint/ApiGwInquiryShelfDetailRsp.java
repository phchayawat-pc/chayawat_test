package th.co.truecorp.commonapi.reward.model.endpoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import th.co.truecorp.commonapi.reward.model.ShelfDtacInquiryShelfDetailApiRsp;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiGwInquiryShelfDetailRsp {
    private String code;
    private String description;
    private String timestamp;
    private String message;
    private String businessError;
    private String error;
    private String id;
    private List<Pattern> pattern;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Pattern {
        private String type;
        private List<PromotionPattern> promotionPattern;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PromotionPattern {
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
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Banner {
        private String id;
        private String subCategory;
        private String href;
        private String description;
        private String type;
        private String category;
        private List<Campaign> campaigns;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ValidFor {
        private String startDateTime;
        private String endDateTime;
        private String remainingDays;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RelatedParty {
        private String name;
        private String type;
        private String href;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PromotionCriteriaGroup {
        private String id;
        private String type;
        private String description;
        private String shortDescription;
        private List<PromotionCriteria> promotionCriteria;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PromotionCriteria {
        private String criteriaPara;
        private String criteriaValue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
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

}