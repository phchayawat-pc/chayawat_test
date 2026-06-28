package th.co.truecorp.commonapi.reward.cms.jpa.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSectionDetail;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSystemConfig;

import java.util.ArrayList;
import java.util.List;

public class RwdSpecification {

    public static Specification<RwdSystemConfig> hasConfigCodeGroup(String configCode, String configGroup) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<Predicate>();
            list.add(criteriaBuilder.equal(root.get("configCode"), configCode));
            list.add(criteriaBuilder.equal(root.get("configGroup"), configGroup));
            return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
        };
    }


    public static Specification<RwdSystemConfig> hasConfigCode(String configCode) {
        return (Root<RwdSystemConfig> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(root.get("configCode"), configCode);
    }

    public static Specification<RwdSectionDetail> countTotalSectionDetail(String sectionId) {
        return (Root<RwdSectionDetail> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(root.get("sectionId"), sectionId);
    }

    public static Specification<RwdSectionDetail> findSectionDetailBySectionIdAndItemTypeCode(String sectionId, List<String> itemTypeCode) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<Predicate>();
            list.add(criteriaBuilder.equal(root.get("sectionId"), sectionId));
            list.add(root.get("itemTypeCode").in(itemTypeCode));
            return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
        };
    }

    public static Specification<RwdSectionDetail> findSectionDetailBySectionIdAndLang(String sectionId, String lang) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<Predicate>();
            list.add(criteriaBuilder.equal(root.get("sectionId"), sectionId));
            list.add(root.get("lang").in(lang));
            return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
        };
    }
}
