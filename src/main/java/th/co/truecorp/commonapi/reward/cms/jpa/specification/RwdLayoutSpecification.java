package th.co.truecorp.commonapi.reward.cms.jpa.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdLayout;

import java.util.ArrayList;
import java.util.List;

public class RwdLayoutSpecification {

    public static Specification<RwdLayout> hasByBrand(String layoutStatus, String validFlag, String brandCode) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<Predicate>();
            list.add(criteriaBuilder.equal(root.get("layoutStatus"), layoutStatus));
            list.add(criteriaBuilder.equal(root.get("validFlag"), validFlag));
            list.add(criteriaBuilder.equal(root.get("brandCode"), brandCode));
            return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
        };
    }

    public static Specification<RwdLayout> hasByBrandAndProductType(String layoutStatus, String validFlag, String brandCode, String productType) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<Predicate>();
            list.add(criteriaBuilder.equal(root.get("layoutStatus"), layoutStatus));
            list.add(criteriaBuilder.equal(root.get("validFlag"), validFlag));
            list.add(criteriaBuilder.equal(root.get("brandCode"), brandCode));
            list.add(criteriaBuilder.equal(root.get("productTypeCode"), productType));
            return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
        };
    }

    public static Specification<RwdLayout> hasByBrandAndChargeType(String layoutStatus, String validFlag, String brandCode, String chargeType) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<Predicate>();
            list.add(criteriaBuilder.equal(root.get("layoutStatus"), layoutStatus));
            list.add(criteriaBuilder.equal(root.get("validFlag"), validFlag));
            list.add(criteriaBuilder.equal(root.get("brandCode"), brandCode));
            list.add(criteriaBuilder.equal(root.get("chargeTypeCode"), chargeType));
            return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
        };
    }

    public static Specification<RwdLayout> hasByBrandAndProductTypeAndChargeType(String layoutStatus, String validFlag, String brandCode, String productType, String chargeType) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<Predicate>();
            list.add(criteriaBuilder.equal(root.get("layoutStatus"), layoutStatus));
            list.add(criteriaBuilder.equal(root.get("validFlag"), validFlag));
            list.add(criteriaBuilder.equal(root.get("brandCode"), brandCode));
            list.add(criteriaBuilder.equal(root.get("productTypeCode"), productType));
            list.add(criteriaBuilder.equal(root.get("chargeTypeCode"), chargeType));
            return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
        };
    }
}
