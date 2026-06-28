package th.co.truecorp.commonapi.reward.cms.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSystemConfig;

import java.util.List;

@Repository
public interface RwdSystemConfigRepository extends JpaRepository<RwdSystemConfig, Integer>, JpaSpecificationExecutor<RwdSystemConfig> {

    public List<RwdSystemConfig> findAllByConfigCode(String configCode);

    public List<RwdSystemConfig> findAllByConfigGroup(String configGroup);

}
