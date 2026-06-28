package th.co.truecorp.commonapi.reward.transaction;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.service.ErrorService;
import th.co.truecorp.commonapi.reward.model.EndpointResultRWD;
import th.co.truecorp.commonapi.reward.redis.RedisCacheService;
import th.co.truecorp.commonapi.reward.service.ClearCacheService;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.TransactionLog;
import th.co.truecorp.commonlib.log.context.ContextSignature;
import th.co.truecorp.commonlib.log.context.LogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.exception.EndpointServiceException;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.log.model.TransactionResult;
import th.co.truecorp.commonlib.service.DecoratedExecutorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class ClearCacheTransaction {

	private static Logger log = LoggerFactory.getLogger(ClearCacheTransaction.class);

	@Autowired
	private ResultService resultService;

	@Autowired
	private LogContextService logContextService;

	@Autowired
	private ClearCacheService clearCacheService;

	@Value("${app.zone}")
	private String zone;

	@Value("${app.domain}")
	private String domain;

	@Autowired
	private RedisCacheService redisCacheService;

	@Autowired
	private ErrorService errorService;

	@Autowired
	private RedisTemplate redisTemplate;

	@TransactionLog(name = "getShelfClearCache")
	public TransactionResult getShelfClearCache(Map<String, Object> tv, String layoutId, HttpServletRequest httpRequest) {

		final LogContext logContext = logContextService.getCurrentContext();
		EndpointResult endpointResult = null;

		try {
			logContext.loadHttpRequest(tv, httpRequest);
			log.info("prepair data");

			ResponseEntity<String> entity = clearCacheService.clearCacheShelf(tv, layoutId);
			endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC, ComnConst.STTS_CODE_SUCC);

			return  new TransactionResult(endpointResult);
		} catch (EndpointServiceException endpointServiceException) {
			return resultService.getTransactionExceptionResult(endpointServiceException);
		} catch (Exception exception) {
			return resultService.getTransactionExceptionResult(tv, exception);
		}
	}

	@TransactionLog(name = "clearCacheByType")
	public TransactionResult clearCache(Map<String, Object> tv, String type, HttpServletRequest httpRequest) {
		final LogContext logContext = logContextService.getCurrentContext();
		final ContextSignature contextSignature = logContextService.getCurrentContextSignature();
		AtomicReference<EndpointResultRWD> endpointResultRwd = new AtomicReference<>(new EndpointResultRWD());

		log.info("Starting clearCache operation - Type: {}", type);

		try {
			logContext.loadHttpRequest(tv, httpRequest);

			if (Objects.equals(type, "profile")) {
				log.info("Clearing profile-related cache keys.");

				List<String> cacheKeys = List.of(
						"PRF-" + zone + ":" + tv.get("digitalId") + ":loyaltyManagement_v1_getExpiration",
						"PRF-" + zone + ":" + tv.get("digitalId") + ":loyaltyManagement_v1_loyaltyAccount_%7BaccountId%7D_loyaltyBalance",
						"PRF-" + zone + ":" + tv.get("digitalId") + ":loyaltyManagement_v1_loyaltyProgramMember"
				);

				endpointResultRwd.set(redisCacheService.deleteCacheKeys(cacheKeys, tv));

			} else if (Objects.equals(type, "error")) {
				log.info("Clearing error-related cache.");
				String cacheKey = domain + "-ERR:" + zone;
				log.info("Deleting cache type error with key: {}", cacheKey);
				endpointResultRwd.set(redisCacheService.deleteByPattern(cacheKey, tv));
				log.info("Cache deletion type error result for key {}: {}", cacheKey, endpointResultRwd.get().getHttpStatus());
			} else {
				log.info("Unsupported cache type: {}", type);
			}

			log.info("Ending clearCache operation - Type: {}", type);
			return new TransactionResult(errorService.revertMapResult(endpointResultRwd.get()));
		} catch (EndpointServiceException e) {
			return resultService.getTransactionExceptionResult(e);
		} catch (Exception e) {
			log.error("clear cache exception: {}", e.getMessage(), e);
			return resultService.getTransactionExceptionResult(tv, e);
		}
	}
}
