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
import th.co.truecorp.commonapi.reward.common.model.EmptyJsonResponse;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonapi.reward.model.jsonResponse.HistoryPageJsonResponse;
import th.co.truecorp.commonapi.reward.transaction.HistoryTransaction;
import th.co.truecorp.commonlib.annotations.CustomerProfile;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.log.model.TransactionResult;
import th.co.truecorp.commonlib.model.AccessTokenJWTPayload;
import th.co.truecorp.commonlib.service.NetworkService;
import th.co.truecorp.commonlib.util.TransactionValueUtil;

import java.util.HashMap;
import java.util.List;

@Validated
@RestController
@RequestMapping(value = "/v1")
public class HistoryController {

	@Autowired
	HistoryTransaction historyTransaction;
	@Autowired
	private NetworkService networkService;

	@Autowired
	private APIGWUtill apigwUtill;
	
	String host;

	Gson gson = new Gson();

	private static Logger log = LoggerFactory.getLogger(HistoryController.class);

	@PostConstruct
	public void getHost() {
		host = networkService.getHostName();
	}

	@GetMapping(path = "/history/point", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HistoryPageJsonResponse<HistoryPointDateRsp>> getHistoryPoint(
			@RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
			@RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
			@RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
			@RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
			@RequestHeader(Constant.HEADER_PLATFORM) String platform,
			@RequestHeader(Constant.HEADER_VERSION) String version,
			@RequestHeader(ComnConst.KEY_LANGUAGE) String language,
			@CustomerProfile AccessTokenJWTPayload customerProfile,
			@RequestParam(value="subType") String type,
			@RequestParam(value="date") String date,
			@RequestParam(value="page", required = false) String page,
			@RequestParam(value="limit", required = false) String limit,
			HttpServletRequest httpRequest
	) throws Exception {
		HashMap<String, Object> tv = new HashMap<String, Object>();
		log.info("start get history point with subType is {} and date is {}",type,date);
		TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
		AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);

		String brand = accessTokenJWTPayload.getCustomerInfo().getProductBrand().toLowerCase();
		String digitalId = accessTokenJWTPayload.getCustomerInfo().getDigitalId();

			tv.put("digitalId", digitalId);
			tv.put("brand", brand);
			tv.put("date", date);
			tv.put(ComnConst.KEY_LANGUAGE, language!=null && !language.equals("") ? language : Constant.EN);
			tv.put("productId", accessTokenJWTPayload.getCustomerInfo().getProductId());
			tv.put("paging", page);
			tv.put("limit", limit);
			tv.put("cvgList", accessTokenJWTPayload.getCustomerInfo().getCvgList());
			tv.put("productSegment", accessTokenJWTPayload.getCustomerInfo().getProductSegment());

		TransactionResult transactionResult = historyTransaction.getHistoryPoint(tv, brand, type, date, httpRequest);

		HistoryPointResponse data = new HistoryPointResponse(tv, transactionResult);
		HistoryPageJsonResponse<HistoryPointDateRsp> getHistoryResponse = new HistoryPageJsonResponse<>(tv, transactionResult,
				data.getData(), new EmptyJsonResponse(), data.getPaging());
		log.info("end get history point : "+gson.toJson(getHistoryResponse));
		return ResponseEntity.status(transactionResult.getHttpStatus()).body(getHistoryResponse);
	}

}
