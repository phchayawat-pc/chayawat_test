package th.co.truecorp.commonapi.reward.cms.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSectionDisplayName;

@Repository
public interface RwdSectionDisplayNameRepository extends JpaRepository<RwdSectionDisplayName, Integer>, JpaSpecificationExecutor<RwdSectionDisplayName> {


}
