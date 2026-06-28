package th.co.truecorp.commonapi.reward.cms.jpa.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.EndpointCacheConfigEntity;

import java.util.Optional;

@Repository
public interface EndpointCacheConfigRepository extends JpaRepository<EndpointCacheConfigEntity, Integer> {
    public Optional<EndpointCacheConfigEntity> findFirstBySrvId(Integer srvId);
}
