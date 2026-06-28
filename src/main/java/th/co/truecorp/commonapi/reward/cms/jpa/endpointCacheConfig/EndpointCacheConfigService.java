package th.co.truecorp.commonapi.reward.cms.jpa.endpointCacheConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.EndpointCacheConfigEntity;

import java.util.Optional;

@Service
public class EndpointCacheConfigService {

    @Autowired
    private EndpointConfigService endpointConfigService;
    @Autowired
    private CacheManager cacheManager;

    public Optional<EndpointCacheConfigEntity> findConfigByEndpoint(Integer id) {
        String cacheName = "globalconfig";
        String key = generateKey("EndpointConfigService.findConfigByEndpoint", id);

        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            Optional<EndpointCacheConfigEntity> cachedValue = cache.get(key, Optional.class);
            if (cachedValue != null) {
                return cachedValue;
            }
        }

        Optional<EndpointCacheConfigEntity> data = endpointConfigService.findConfigByEndpoint(id);
        if (cache != null) {
            cache.put(key, data);
        }

        return data;
    }

    private String generateKey(String methodName, Integer id) {
        return methodName + "::" + id;
    }
}
