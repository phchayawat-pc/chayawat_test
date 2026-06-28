package th.co.truecorp.commonapi.reward.cache.reward.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import th.co.truecorp.commonapi.reward.cache.reward.model.CacheAnnModel;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonlib.constant.ComnConst;

import th.co.truecorp.commonapi.reward.cache.reward.Interface.CacheAnnServiceInterface;
import th.co.truecorp.commonapi.reward.cache.reward.Interface.CacheConfigAnnInterface;
import th.co.truecorp.commonapi.reward.cache.reward.Interface.CacheKeyMapperAnnInterface;
import th.co.truecorp.commonapi.reward.cache.reward.annotation.CacheAnn;

import java.lang.reflect.Method;
import java.util.Map;

@Aspect
@Component
public class CacheAspect {


    @Autowired
    private ApplicationContext applicationContext;

    @Around("@annotation(th.co.truecorp.commonapi.reward.cache.reward.annotation.CacheAnn)")
    public Object transactionsCache(ProceedingJoinPoint pjp) throws Throwable {

        // Retrieve the annotation from the intercepted method
        CacheAnn CacheAnnotation = getAnnotation(pjp);

        // Get parameters from the method
        Object transactionValue = getRequestParam(pjp, CacheAnnotation.transactionValue());
        Map<String, Object> tv = (Map<String, Object>) transactionValue;

        if (!tv.containsKey(Constant.LAYOUT_ID)) {
            throw new IllegalArgumentException("Error: layout ID not found in tv");
        }

        Object request = getRequestParam(pjp, CacheAnnotation.request());
        String srvName = CacheAnnotation.srvName();
        String sourceSystem = CacheAnnotation.srcSystemId();

        Class<? extends CacheAnnServiceInterface> getCacheService = CacheAnnotation.cacheService();
        CacheAnnServiceInterface cacheService = applicationContext.getBean(getCacheService);
        Class<? extends CacheKeyMapperAnnInterface> getKeyMapper = CacheAnnotation.keyMapper();
        CacheKeyMapperAnnInterface keyMapper = applicationContext.getBean(getKeyMapper);
        Class<? extends CacheConfigAnnInterface> getCacheConfig = CacheAnnotation.cacheConfig();
        CacheConfigAnnInterface cacheConfig = applicationContext.getBean(getCacheConfig);

        CacheAnnModel model = CacheAnnModel.builder()
                .tv(tv)
                .request(request)
                .responseType(getKeyMapper)
                .srvName(srvName)
                .srcSystemId(sourceSystem)
                .build();

        try {
            String cacheKey = keyMapper.generateCacheKey(model);
            Boolean isEnabled = cacheConfig.isEnabled(model);
            // check cache
            try {
                if (isEnabled && cacheService.hasCache(cacheKey)) {
                    return cacheService.getFromCache(cacheKey,
                            CacheAnnotation.responseType());
                }
            } catch (Exception e) {
            }

            // Proceed with the original method execution
            ResponseEntity<?> result;
            try {
                result = (ResponseEntity<?>) pjp.proceed();
            } catch (Throwable throwable) {
                throw throwable;
            }

            // save cache
            try {
                if (isEnabled) {
                    Integer ttl = cacheConfig.getTTl(model);
                    Long seconds = ttl.longValue() / 1000;
                    cacheService.putInCache(cacheKey, result, seconds);
                }
            } catch (Exception e) {
            }

            return result;
        } catch (Exception e) {
            throw e;
        } finally {
        }
    }

    private Object getRequestParam(ProceedingJoinPoint pjp, String param) {
        Object request = null;
        Object[] args = pjp.getArgs();
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();

        String[] parameterNames = methodSignature.getParameterNames();
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(param)) {
                request = args[i];
                break;
            }
        }
        return request;
    }

    private CacheAnn getAnnotation(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        Method method = getTargetMethod(pjp);
        return method.getAnnotation(CacheAnn.class);
    }

    private Method getTargetMethod(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        return pjp.getTarget().getClass().getMethod(signature.getName(), signature.getParameterTypes());
    }

}
