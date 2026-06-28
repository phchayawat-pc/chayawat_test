package th.co.truecorp.commonapi.reward.controller;

import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.model.jsonResponse.DefaultJsonResponse;
import th.co.truecorp.commonapi.reward.transaction.ClearCacheTransaction;
import th.co.truecorp.commonlib.annotations.CustomerProfile;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.log.model.TransactionResult;
import th.co.truecorp.commonlib.model.AccessTokenJWTPayload;
import th.co.truecorp.commonlib.service.NetworkService;
import th.co.truecorp.commonlib.util.TransactionValueUtil;
import th.co.truecorp.commonlib.ws.model.GenericJsonResponse;

import java.util.HashMap;

@Validated
@RestController
@RequestMapping(value = "/v1")
public class ClearCacheController {
	private static Logger log = LoggerFactory.getLogger(ClearCacheController.class);
	@Autowired
	ClearCacheTransaction clearCacheTransaction;
	@Autowired
	private NetworkService networkService;

	@Autowired
	private APIGWUtill apigwUtill;

	String host;

	Gson gson = new Gson();

	@PostConstruct
	public void getHost() {
		host = networkService.getHostName();
	}

	@GetMapping(path = "/clearCache", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericJsonResponse<String>> getClearCache(
			@RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
			@RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
			@RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
			@RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
			@RequestHeader(Constant.HEADER_PLATFORM) String platform,
			@RequestHeader(Constant.HEADER_VERSION) String version,
			@RequestHeader(ComnConst.KEY_LANGUAGE) String language,
			@CustomerProfile AccessTokenJWTPayload customerProfile,
			@RequestParam(value="layoutId") String layoutId,
			HttpServletRequest httpRequest
	) throws Exception {
		HashMap<String, Object> tv = new HashMap<String, Object>();
		TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
		AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);

		tv.put(ComnConst.KEY_LANGUAGE, language!=null && !language.equals("") ? language : Constant.EN);

		TransactionResult transactionResult = clearCacheTransaction.getShelfClearCache(tv, layoutId, httpRequest);

		DefaultJsonResponse<String> data = new DefaultJsonResponse(tv, transactionResult);
		GenericJsonResponse<String> getHistoryResponse = new GenericJsonResponse<>(tv, transactionResult,
				data.getData());
		log.info("End ClearCache : "+gson.toJson(getHistoryResponse));
		return ResponseEntity.status(transactionResult.getHttpStatus()).body(getHistoryResponse);
	}

	@GetMapping(path = "/clearCacheByType", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericJsonResponse<String>> getClearCacheByType(
			@RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
			@RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
			@RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
			@RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
			@RequestHeader(Constant.HEADER_PLATFORM) String platform,
			@RequestHeader(Constant.HEADER_VERSION) String version,
			@RequestHeader(ComnConst.KEY_LANGUAGE) String language,
			@CustomerProfile AccessTokenJWTPayload customerProfile,
			@RequestParam(value="type") String type,
			HttpServletRequest httpRequest
	) throws Exception {
		HashMap<String, Object> tv = new HashMap<String, Object>();
		TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
		AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);

		String digitalId = accessTokenJWTPayload.getCustomerInfo().getDigitalId();

		tv.put("digitalId",digitalId);
		tv.put("brand", accessTokenJWTPayload.getCustomerInfo().getProductBrand());
		tv.put(ComnConst.KEY_LANGUAGE, language!=null && !language.equals("") ? language : Constant.EN);

		TransactionResult transactionResult = clearCacheTransaction.clearCache(tv, type, httpRequest);

		DefaultJsonResponse<String> data = new DefaultJsonResponse(tv, transactionResult);
		GenericJsonResponse<String> getHistoryResponse = new GenericJsonResponse<>(tv, transactionResult,
				data.getData());
		log.info("End ClearCache By Type : "+gson.toJson(getHistoryResponse));
		return ResponseEntity.status(transactionResult.getHttpStatus()).body(getHistoryResponse);
	}
}
