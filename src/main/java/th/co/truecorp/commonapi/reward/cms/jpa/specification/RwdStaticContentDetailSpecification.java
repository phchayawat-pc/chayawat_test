package th.co.truecorp.commonapi.reward.cms.jpa.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdStaticContentDetail;

import java.util.ArrayList;
import java.util.List;

public class RwdStaticContentDetailSpecification {

    public static Specification<RwdStaticContentDetail> hasContentIdAndLang(String contentId , String lang) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<Predicate>();
            list.add(criteriaBuilder.equal(root.get("contentId"), contentId));
            list.add(criteriaBuilder.equal(root.get("lang"), lang));
            return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
        };
    }

}
