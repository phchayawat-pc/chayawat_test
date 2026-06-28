package th.co.truecorp.commonapi.reward.cms.jpa.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSectionDisplayName;

import java.util.ArrayList;
import java.util.List;

public class RwdSectionDisplayNameSpecification {

    public static Specification<RwdSectionDisplayName> hasSectionIdAndLang(String sectionId, String lang) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<Predicate>();
            list.add(criteriaBuilder.equal(root.get("sectionId"), sectionId));
            list.add(criteriaBuilder.equal(root.get("lang"), lang));
            return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
        };
    }
}
