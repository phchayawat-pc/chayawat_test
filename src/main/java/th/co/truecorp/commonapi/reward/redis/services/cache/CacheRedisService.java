package th.co.truecorp.commonapi.reward.redis.services.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cache.reward.Interface.CacheAnnServiceInterface;
import th.co.truecorp.commonapi.reward.redis.RedisCacheService;

@Service
public class CacheRedisService implements CacheAnnServiceInterface {

    @Autowired
    private RedisCacheService redisCacheService;

    @Override
    public <T> T getFromCache(String cacheKey, Class<T> responseType) throws Exception {
        return (T) getResponseEntityFromCache(cacheKey, responseType);
    }

    public <T> ResponseEntity<T> getResponseEntityFromCache(String cacheKey, Class<T> responseType) throws Exception {
        T response = redisCacheService.get(cacheKey, responseType);
        if (response != null) {
            return ResponseEntity.ok().body(response);
        }
        return null;
    }

    @Override
    public boolean hasCache(String cacheKey) {
        return redisCacheService.hasKey(cacheKey);
    }

    @Override
    public void putInCache(String cacheKey, Object response, Long seconds) {
        if (response instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity) response;
            if (responseEntity.getStatusCode().equals(HttpStatus.OK))
                redisCacheService.putExpireRedis(cacheKey, responseEntity.getBody(), seconds);
        }
    }
}
