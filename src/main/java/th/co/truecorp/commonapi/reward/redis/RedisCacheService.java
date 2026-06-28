package th.co.truecorp.commonapi.reward.redis;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.service.ErrorService;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.model.EndpointResultRWD;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RedisCacheService {

    private static Logger log = LoggerFactory.getLogger(RedisCacheService.class);
    @Autowired
    private LogContextService logContextService;

    @Value("${app.domain}")
    private String appDomain;

    @Value("${app.zone}")
    private String appZone;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ResultService resultService;

    @Autowired
    private ErrorService errorService;

    private ObjectMapper objectMapper;

    public RedisCacheService() {
        this.objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @EndpointLog(name = "ALL_REDIS.RedisCacheService.put", type = EndpointLog.Type.Database_redis, logResponse = false)
    public void put(String key, Object object) {
        this.putExpireRedis(key, object, null);
    }

    @EndpointLog(name = "ALL_REDIS.RedisCacheService.putExpireRedis", type = EndpointLog.Type.Database_redis, logResponse = false)
    public void putExpireRedis(String key, Object object, Long expireSec) {
        log.info("RedisCacheService put -> key: {}, value: {}, expireSec: {}", key,
                object, expireSec);
        if (expireSec != null) {
            redisTemplate.opsForValue().set(this.getKey(key), object, expireSec, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(this.getKey(key), object);
        }

    }

    @EndpointLog(name = "ALL_REDIS.RedisCacheService.get", type = EndpointLog.Type.Database_redis, logResponse = false)
    public <T> T get(String key, Class<T> valueType) {
        log.info("RedisCacheService get -> key: {}", key);
        return this.objectMapper.convertValue(redisTemplate.opsForValue().get(this.getKey(key)), valueType);
    }

    @EndpointLog(name = "ALL_REDIS.RedisCacheService.hasKey", type = EndpointLog.Type.Database_redis, logResponse = false)
    public Boolean hasKey(String key) {
        log.info("RedisCacheService get -> key: {}", key);
        return redisTemplate.hasKey(getKey(key));
    }

    @EndpointLog(name = "ALL_REDIS.RedisCacheService.delete", type = EndpointLog.Type.Database_redis, logResponse = false)
    public void delete(String key) {
        log.info("RedisCacheService delete -> key: {}", key);
        redisTemplate.delete(this.getKey(key));
    }

    @EndpointLog(name = "ALL_REDIS.RedisCacheService.delete", type = EndpointLog.Type.Database_redis, logResponse = false)
    public EndpointResultRWD deleteByPattern(String key, Map<String, Object> tv) throws Exception {
        log.info("RedisCacheService delete -> pattern: {}", key);
        EndpointResultRWD endpointResultRwd = new EndpointResultRWD();

        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            try {
                Boolean isDeleted = redisTemplate.delete(key);
                if (Boolean.TRUE.equals(isDeleted)) {
                    endpointResultRwd = errorService.convertMapResult(
                            resultService.findEndpointResult(tv, Constant.STTS_CODE_SUCC, Constant.STTS_CODE_SUCC));
                } else {
                    endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
                            tv.get("brand").toString(),
                            Constant.ERROR_DURING_DELETE_CACHE,
                            tv.get(ComnConst.KEY_LANGUAGE).toString(),
                            "Error During Delete Cache",
                            Constant.N_A,
                            Constant.MESSAGE);
                }
            } catch (Exception e) {
                log.error("Error occurred while deleting key: {}", key, e);
                endpointResultRwd = errorService.convertMapResult(errorService.mapErrorException(e, tv));
            }
        } else {
            log.warn("Key not found in Redis: {}", key);
            endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
                    tv.get("brand").toString(),
                    Constant.ERROR_DATA_NOT_FOUND,
                    tv.get(ComnConst.KEY_LANGUAGE).toString(),
                    "Data not found",
                    Constant.N_A,
                    Constant.MESSAGE);
        }
        return endpointResultRwd;
    }

    @EndpointLog(name = "ALL_REDIS.RedisCacheService.deleteCacheKeys", type = EndpointLog.Type.Database_redis, logResponse = false)
    public EndpointResultRWD deleteCacheKeys(List<String> cacheKeys,  Map<String, Object> tv) throws Exception {

        log.info("start delete cache with {} keys: {}", cacheKeys.size(),cacheKeys);
        EndpointResultRWD endpointResultRwd = new EndpointResultRWD();
        // ตรวจสอบว่า key มีอยู่ใน Redis ก่อนที่จะลบ
        List<String> keysToDelete = cacheKeys.stream()
                .filter(key -> Boolean.TRUE.equals(redisTemplate.hasKey(key))) // ตรวจสอบว่ามี key อยู่
                .collect(Collectors.toList());

        boolean retrySuccess = true;

        // ลบคีย์ที่ตรวจสอบว่าอยู่ใน Redis
        if (!keysToDelete.isEmpty()) {
            log.info("Keys found in Redis to delete: {}", keysToDelete);
            List<String> successfullyDeleted = new ArrayList<>();
            List<String> notDeletedKeys = new ArrayList<>();

            for (String key : keysToDelete) {
                Boolean deleted = redisTemplate.delete(key);
                if (Boolean.TRUE.equals(deleted)) {
                    successfullyDeleted.add(key);
                } else {
                    notDeletedKeys.add(key);
                }
            }

            log.info("Successfully deleted {} cache keys", successfullyDeleted.size());
            if (!notDeletedKeys.isEmpty()) {
                log.error("Failed to delete keys initially: {}", notDeletedKeys);

                // ลองลบซ้ำด้วย Exponential Backoff
                retrySuccess = retryDeleteKeys(notDeletedKeys, 3);

                if (!retrySuccess) {
                    log.error("Some keys could not be deleted after retries: {}", notDeletedKeys);
                    String errorMessage = "error during delete catch for key " + notDeletedKeys;
                    endpointResultRwd = errorService.mapErrorCode(
                            Constant.QUERY_DATA,
                            tv.get("brand").toString(),
                            Constant.ERROR_DURING_DELETE_CACHE,
                            tv.get(ComnConst.KEY_LANGUAGE).toString(),
                            errorMessage,
                            Constant.N_A,
                            Constant.MESSAGE
                    );

                    endpointResultRwd.setEndpointErrorDescription(endpointResultRwd.getEndpointErrorDescription()+" "+notDeletedKeys);
                } else {
                    log.info("Successfully deleted keys after retry: {}", notDeletedKeys);
                    endpointResultRwd = errorService.convertMapResult(
                            resultService.findEndpointResult(tv, Constant.STTS_CODE_SUCC, Constant.STTS_CODE_SUCC));
                    endpointResultRwd.setEndpointErrorDescription("All keys deleted successfully");
                }
            }else {

                endpointResultRwd = errorService.convertMapResult(
                        resultService.findEndpointResult(tv, Constant.STTS_CODE_SUCC, Constant.STTS_CODE_SUCC));
                endpointResultRwd.setEndpointErrorDescription("All keys deleted successfully");
                log.info("Successfully deleted keys: {}", successfullyDeleted);
            }
            // ✅ ใช้ Exponential Backoff เช็คว่าลบสำเร็จหรือไม่
//            boolean allDeleted = retryCheckKeysDeleted(new HashSet<>(successfullyDeleted), 3);
//            log.info("All keys deleted successfully: {}", retrySuccess);
        } else {
            log.info("No cache keys found in Redis to delete.");
            EndpointResult endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC, ComnConst.STTS_CODE_SUCC);
            endpointResult.setEndpointErrorMessage("data not found in redis");
            endpointResultRwd = errorService.convertMapResult(endpointResult);
        }
        return endpointResultRwd;
    }

    private boolean retryDeleteKeys(List<String> keys, int maxRetries) {

        int attempt = 0;
//        long sleepTime = 200; // เริ่มที่ 200ms

        while (attempt < maxRetries) {
            List<String> failedKeys = new ArrayList<>();

            for (String key : keys) {
                try {
                    Boolean deleted = redisTemplate.delete(key);

                    if (!Boolean.TRUE.equals(deleted)) {
                        failedKeys.add(key);
                    }
                } catch (Exception e) {
                    log.error("Exception occurred while deleting key {}: {}", key, e.getMessage(), e);
                }
            }

            if (failedKeys.isEmpty()) {
                return true; // ลบสำเร็จหมด
            }

//            try {
//                Thread.sleep(sleepTime); // ใช้ Exponential Backoff
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//
//            sleepTime *= 2; // เพิ่มเวลาเป็น 400ms -> 800ms -> 1600ms
            keys = failedKeys; // อัปเดตรายการคีย์ที่ลบไม่สำเร็จ
            attempt++;
        }

        log.error("Keys still not deleted after {} retries: {}", maxRetries, keys);
        return false; // ยังมีคีย์ที่ลบไม่สำเร็จ
    }

    @EndpointLog(name = "ALL_REDIS.RedisCacheService.getKey", type = EndpointLog.Type.Database_redis, logResponse = false)
    private String getKey(String key) {
        return appDomain + "-" + appZone + ":" + key;
    }

    @EndpointLog(name = "ALL_REDIS.RedisCacheService.getOtherService", type = EndpointLog.Type.Database_redis, logResponse = false)
    public <T> T getOtherService(String service, String key, Class<T> valueType) {
        String fullKey = service + "-" + appZone + ":" + key;
        log.info("RedisCacheService getOtherService -> key: {}", fullKey);
        return this.objectMapper.convertValue(redisTemplate.opsForValue().get(fullKey), valueType);
    }
}
