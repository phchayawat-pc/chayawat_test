package th.co.truecorp.commonapi.reward.controller;

import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import th.co.truecorp.commonapi.reward.common.model.EmptyJsonResponse;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonapi.reward.model.redeem.RedeemDealScanCodeResponse;
import th.co.truecorp.commonapi.reward.model.jsonResponse.DefaultJsonResponse;
import th.co.truecorp.commonapi.reward.model.redeem.RedeemTakeRedeemResponse;
import th.co.truecorp.commonapi.reward.model.jsonResponse.RedeemPageDefaultResponse;
import th.co.truecorp.commonapi.reward.model.jsonResponse.ShelfGetLayoutPageJsonResponse;
import th.co.truecorp.commonapi.reward.transaction.RedeemTransaction;
import th.co.truecorp.commonlib.annotations.CustomerProfile;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.log.model.TransactionResult;
import th.co.truecorp.commonlib.model.AccessTokenJWTPayload;
import th.co.truecorp.commonlib.service.NetworkService;
import th.co.truecorp.commonlib.util.TransactionValueUtil;
import th.co.truecorp.commonlib.ws.model.GenericJsonResponse;
import java.util.*;

@Validated
@RestController
@RequestMapping(value = "/v1/redeem")
public class RedeemController {

	@Autowired
	RedeemTransaction redeemTransaction;
	
	String host;

	private static Logger log = LoggerFactory.getLogger(RedeemController.class);

	Gson gson = new Gson();

	@Autowired
	private NetworkService networkService;

	@PostConstruct
	public void getHost() {
		host = networkService.getHostName();
	}

	@GetMapping(path = "/detail", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericJsonResponse<RedeemDealDetailRsp>> getDetail(
			@RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
			@RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
			@RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
			@RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
			@RequestHeader(Constant.HEADER_PLATFORM) String platform,
			@RequestHeader(Constant.HEADER_VERSION) String version,
			@RequestHeader(ComnConst.KEY_LANGUAGE) String language,
			@CustomerProfile AccessTokenJWTPayload customerProfile,
			@RequestParam(value = "campaignId") String campaignId,
			@RequestParam(value = "campaignCode", required = false) String campaignCode,
			@RequestParam(value = "timeCounterFlag", required = false) String timeCounterFlag,
			HttpServletRequest httpRequest
	) throws Exception {

		log.info("start process redeem detail");
		HashMap<String, Object> tv = new HashMap<String, Object>();
		TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
		AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);

		String brand = accessTokenJWTPayload.getCustomerInfo().getProductBrand().toLowerCase();

		tv.put("cvgList", accessTokenJWTPayload.getCustomerInfo().getCvgList());
		tv.put("productSegment", accessTokenJWTPayload.getCustomerInfo().getProductSegment());
		tv.put("brand", brand);
		tv.put("digitalId", accessTokenJWTPayload.getCustomerInfo().getDigitalId());
		tv.put("productId", accessTokenJWTPayload.getCustomerInfo().getProductId());
		tv.put("productType", accessTokenJWTPayload.getCustomerInfo().getProductType());
		tv.put(ComnConst.KEY_LANGUAGE, language!=null && !language.equals("") ? language : Constant.EN);
		tv.put("campaignId", campaignId);
		tv.put("campaignCode", campaignCode);
		tv.put("timeCounterFlag", timeCounterFlag);

		TransactionResult transactionResult = redeemTransaction.getDetail(tv, httpRequest, brand);

		RedeemPageDefaultResponse<RedeemDealDetailRsp> data = new RedeemPageDefaultResponse(tv, transactionResult);
		ShelfGetLayoutPageJsonResponse<RedeemDealDetailRsp> getGenericJsonResponse = new ShelfGetLayoutPageJsonResponse<>(tv, transactionResult,
				data.getData(), new EmptyJsonResponse(), data.getPaging());
		log.info("End Get RedeemDeal Detail : "+gson.toJson(getGenericJsonResponse));
		return ResponseEntity.status(transactionResult.getHttpStatus()).body(getGenericJsonResponse);

	}

	@PostMapping(path = "/prize", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericJsonResponse<RedeemDealScanCodeResponse>> postPrize(
			@RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
			@RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
			@RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
			@RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
			@RequestHeader(Constant.HEADER_PLATFORM) String platform,
			@RequestHeader(Constant.HEADER_VERSION) String version,
			@RequestHeader(ComnConst.HEADER_LANGUAGE) @NotBlank @Size(min = 2, max = 2) String language,
			@CustomerProfile AccessTokenJWTPayload customerProfile,
			@RequestBody RedeemDataReq redeemDataReq,
			HttpServletRequest httpRequest
	) throws Exception {
		HashMap<String, Object> tv = new HashMap<String, Object>();
		TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
		AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);

		language = language!=null && !language.equals("") ? language : Constant.EN;
		String brand = accessTokenJWTPayload.getCustomerInfo().getProductBrand().toLowerCase();
		tv.put("productId", accessTokenJWTPayload.getCustomerInfo().getProductId());
		tv.put("productType", accessTokenJWTPayload.getCustomerInfo().getProductType());
		tv.put("digitalId", accessTokenJWTPayload.getCustomerInfo().getDigitalId());
		tv.put("brand", accessTokenJWTPayload.getCustomerInfo().getProductBrand().toLowerCase());

		TransactionResult transactionResult = redeemTransaction.getRedeemDealDetailTransaction(tv, language, brand, redeemDataReq.getCampaignId(), httpRequest);

		DefaultJsonResponse<RedeemDealScanCodeResponse> data = new DefaultJsonResponse<>(tv, transactionResult);
		GenericJsonResponse<RedeemDealScanCodeResponse> getRedeemDealScanCodeRsp = new GenericJsonResponse<>(tv, transactionResult,
				data.getData());
		log.info("End post Prize :" + gson.toJson(getRedeemDealScanCodeRsp));
		return ResponseEntity.status(transactionResult.getHttpStatus()).body(getRedeemDealScanCodeRsp);
	}

	@PostMapping
	public ResponseEntity<GenericJsonResponse<RedeemTakeRedeemResponse>> getTakeRedeem(
			@RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
			@RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
			@RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
			@RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
			@RequestHeader(Constant.HEADER_PLATFORM) String platform,
			@RequestHeader(Constant.HEADER_VERSION) String version,
			@RequestHeader(ComnConst.HEADER_LANGUAGE) @NotBlank @Size(min = 2, max = 2) String language,
			@CustomerProfile AccessTokenJWTPayload customerProfile,
			@RequestBody RedeemDataReq redeemDataReq,
			HttpServletRequest httpRequest
	) throws Exception {
		HashMap<String, Object> tv = new HashMap<String, Object>();
		TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
		AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);

		language = language!=null && !language.equals("") ? language : Constant.EN;
		String brand = accessTokenJWTPayload.getCustomerInfo().getProductBrand().toLowerCase();
		tv.put("productId", accessTokenJWTPayload.getCustomerInfo().getProductId());
		tv.put("productType", accessTokenJWTPayload.getCustomerInfo().getProductType());
		tv.put("digitalId", accessTokenJWTPayload.getCustomerInfo().getDigitalId());
		tv.put("brand", accessTokenJWTPayload.getCustomerInfo().getProductBrand().toLowerCase());
		tv.put("cvgList", accessTokenJWTPayload.getCustomerInfo().getCvgList());
		tv.put("productSegment", accessTokenJWTPayload.getCustomerInfo().getProductSegment());

		TransactionResult transactionResult = redeemTransaction.getTakeRedeemTransaction(tv, language, brand, redeemDataReq, httpRequest);

		DefaultJsonResponse<RedeemTakeRedeemResponse> data = new DefaultJsonResponse<>(tv, transactionResult);
		GenericJsonResponse<RedeemTakeRedeemResponse> getRedeemDealScanCodeRsp = new GenericJsonResponse<>(tv, transactionResult,
				data.getData());
		return ResponseEntity.status(transactionResult.getHttpStatus()).body(getRedeemDealScanCodeRsp);
	}

	@GetMapping(path = "/articleDetail", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericJsonResponse<RedeemArticelDetailRsp>> articleDetail(
			@RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
			@RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
			@RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
			@RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
			@RequestHeader(Constant.HEADER_PLATFORM) String platform,
			@RequestHeader(Constant.HEADER_VERSION) String version,
			@RequestHeader(ComnConst.KEY_LANGUAGE) String language,
			@CustomerProfile AccessTokenJWTPayload customerProfile,
			@RequestParam(value = "campaignId") String campaignId,
			HttpServletRequest httpRequest
	) throws Exception {
		HashMap<String, Object> tv = new HashMap<String, Object>();
		Gson gson = new Gson();
		TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
		AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);

		String brand = accessTokenJWTPayload.getCustomerInfo().getProductBrand().toLowerCase();

		tv.put("cvgList", accessTokenJWTPayload.getCustomerInfo().getCvgList());
		tv.put("productSegment", accessTokenJWTPayload.getCustomerInfo().getProductSegment());
		tv.put("brand", brand);
		tv.put("digitalId", accessTokenJWTPayload.getCustomerInfo().getDigitalId());
		tv.put("productId", accessTokenJWTPayload.getCustomerInfo().getProductId());
		tv.put(ComnConst.KEY_LANGUAGE, language!=null && !language.equals("") ? language : Constant.EN);
		tv.put("campaignId", campaignId);

		TransactionResult transactionResult = redeemTransaction.getArticleDetail(tv, httpRequest, brand);

		RedeemPageDefaultResponse<RedeemArticelDetailRsp> data = new RedeemPageDefaultResponse(tv, transactionResult);
		ShelfGetLayoutPageJsonResponse<RedeemArticelDetailRsp> getGenericJsonResponse = new ShelfGetLayoutPageJsonResponse<>(tv, transactionResult,
				data.getData(), new EmptyJsonResponse(), data.getPaging());
		log.info("End Get ArticleDetail : "+gson.toJson(getGenericJsonResponse));
		return ResponseEntity.status(transactionResult.getHttpStatus()).body(getGenericJsonResponse);

	}
}
