package th.co.truecorp.commonapi.reward.cache.reward.annotation;

import jakarta.validation.constraints.NotNull;
import th.co.truecorp.commonapi.reward.cache.reward.Interface.CacheAnnServiceInterface;
import th.co.truecorp.commonapi.reward.cache.reward.Interface.CacheConfigAnnInterface;
import th.co.truecorp.commonapi.reward.cache.reward.Interface.CacheKeyMapperAnnInterface;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.redis.services.cache.CacheRedisConfigService;
import th.co.truecorp.commonapi.reward.redis.services.cache.CacheRedisService;
import th.co.truecorp.commonapi.reward.redis.services.cache.mapper.RedisSrvNameMapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CacheAnn {

    @NotNull
    String srvName();

    @NotNull
    String srcSystemId() default Constant.ENDPOINT_SOURCE_SYSTEM_ID;

    @NotNull
    Class<?> responseType();

    @NotNull
    String transactionValue() default "tv";

    @NotNull
    Class<? extends CacheAnnServiceInterface> cacheService() default CacheRedisService.class;

    @NotNull
    Class<? extends CacheKeyMapperAnnInterface> keyMapper() default RedisSrvNameMapper.class;

    @NotNull
    Class<? extends CacheConfigAnnInterface> cacheConfig() default CacheRedisConfigService.class;

    @NotNull
    String request() default "request";
}
