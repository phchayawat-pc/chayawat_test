package th.co.truecorp.commonapi.reward.redis.services.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cache.reward.Interface.CacheConfigAnnInterface;
import th.co.truecorp.commonapi.reward.cache.reward.model.CacheAnnModel;
import th.co.truecorp.commonapi.reward.cms.jpa.endpointCacheConfig.EndpointCacheConfigService;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.EndpointCacheConfigEntity;
import th.co.truecorp.commonlib.jpa.entity.CommonServiceEndpoint;
import th.co.truecorp.commonlib.jpa.service.ConfigService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;

import java.util.Optional;

@Service
public class CacheRedisConfigService implements CacheConfigAnnInterface {

    @Autowired
    private ConfigService configService;

    @Autowired
    private EndpointCacheConfigService cacheConfigService;

    @EndpointLog(name = "ALL_REDIS.ProfileCacheRedisConfigService.isCacheEnabled", type = EndpointLog.Type.Database_redis, logResponse = false)
    public boolean isCacheEnabled(CommonServiceEndpoint serviceEndpoint) {
        Optional<EndpointCacheConfigEntity> config = cacheConfigService
                .findConfigByEndpoint(serviceEndpoint.getSrvId());
        return config.isPresent();
    }

    @EndpointLog(name = "ALL_REDIS.ProfileCacheRedisConfigService.getPeriodTime", type = EndpointLog.Type.Database_redis, logResponse = false)
    public Integer getPeriodTime(CommonServiceEndpoint serviceEndpoint) {
        Optional<EndpointCacheConfigEntity> config = cacheConfigService
                .findConfigByEndpoint(serviceEndpoint.getSrvId());
        return config.get().getPeriodTime();
    }

    @Override
    @EndpointLog(name = "ALL_REDIS.ProfileCacheRedisConfigService.isEnabled", type = EndpointLog.Type.Database_redis, logResponse = false)
    public boolean isEnabled(CacheAnnModel model) throws Exception {
        CommonServiceEndpoint serviceEndpoint = configService.findEndpoint(model.getSrcSystemId(), model.getSrvName());
        return isCacheEnabled(serviceEndpoint);
    }

    @Override
    @EndpointLog(name = "ALL_REDIS.ProfileCacheRedisConfigService.getTTl", type = EndpointLog.Type.Database_redis, logResponse = false)
    public Integer getTTl(CacheAnnModel model) throws Exception {
        CommonServiceEndpoint serviceEndpoint = configService.findEndpoint(model.getSrcSystemId(), model.getSrvName());
        return getPeriodTime(serviceEndpoint);
    }
}