package th.co.truecorp.commonapi.reward.redis;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;

import java.util.concurrent.TimeUnit;

@Service
public class TokenCacheService {

    private static Logger log = LoggerFactory.getLogger(TokenCacheService.class);

    private ObjectMapper objectMapper;

    public TokenCacheService() {
        this.objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @EndpointLog(name = "ALL_REDIS.TokenCacheService.put", type = EndpointLog.Type.Database_redis, logResponse = false)
    public void put(String key, Object object) {
        this.putExpireToken(key, object, null);
    }

    @EndpointLog(name = "ALL_REDIS.TokenCacheService.putExpireToken", type = EndpointLog.Type.Database_redis, logResponse = false)
    public void putExpireToken(String key, Object object, Long expireSec) {
        log.info("TokenCacheService put -> key: {}, value: {}, expireSec: {}", key, object, expireSec);
        if (expireSec != null) {
            redisTemplate.opsForValue().set(key, object, expireSec, TimeUnit.SECONDS);
        } else {
            log.info("enter save redis");
            redisTemplate.opsForValue().set(key, object);
        }

    }

    @EndpointLog(name = "ALL_REDIS.TokenCacheService.get", type = EndpointLog.Type.Database_redis, logResponse = false)
    public <T> T get(String key, Class<T> valueType) {
        log.info("TokenCacheService get -> key: {}", key);
        return this.objectMapper.convertValue(redisTemplate.opsForValue().get(key), valueType);
    }

    @EndpointLog(name = "ALL_REDIS.TokenCacheService.delete", type = EndpointLog.Type.Database_redis, logResponse = false)
    public Object delete(String key) {
        log.info("TokenCacheService delete -> key: {}", key);
        return redisTemplate.delete(key);
    }

}
