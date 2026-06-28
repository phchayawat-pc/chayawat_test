package th.co.truecorp.commonapi.reward.cms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Object getCache(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setCache(String key, Object value, long ttl) {
        redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
    }

    public boolean evictCache(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    // Method to check if any key exists matching the given pattern
    public boolean anyKeyExists(String pattern) {
        // Using scan for better performance in production environments
        ScanOptions scanOptions = ScanOptions.scanOptions().match(pattern).count(1000).build();

        return redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            try (Cursor<byte[]> cursor = connection.scan(scanOptions)) {
                return cursor.hasNext();
            }
        });
    }
}


