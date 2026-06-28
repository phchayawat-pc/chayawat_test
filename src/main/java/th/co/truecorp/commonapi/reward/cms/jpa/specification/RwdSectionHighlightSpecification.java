package th.co.truecorp.commonapi.reward.cms.jpa.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSectionHighlight;

public class RwdSectionHighlightSpecification {

    public static Specification<RwdSectionHighlight> hasSectionId(String sectionId) {
        return (Root<RwdSectionHighlight> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(root.get("sectionId"), sectionId);
    }

}
