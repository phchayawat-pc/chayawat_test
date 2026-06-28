package th.co.truecorp.commonapi.reward.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.model.EarnRequest;
import th.co.truecorp.commonapi.reward.model.jsonResponse.DefaultJsonResponse;
import th.co.truecorp.commonapi.reward.model.redeem.EarnRedeemtionGiftResponse;
import th.co.truecorp.commonapi.reward.transaction.EarnTransaction;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.log.model.TransactionResult;
import th.co.truecorp.commonlib.model.AccessTokenJWTPayload;
import th.co.truecorp.commonlib.service.NetworkService;
import th.co.truecorp.commonlib.util.TransactionValueUtil;
import th.co.truecorp.commonlib.ws.model.GenericJsonResponse;

@Validated
@RestController
@RequestMapping("/v1/earn")
public class EarnController {

	private String host;

	@Autowired
    private NetworkService networkService;

	@Autowired
	private EarnTransaction earnTransaction;

	@PostConstruct
	public void getHost() {
		host = networkService.getHostName();
	}

	@PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<GenericJsonResponse<EarnRedeemtionGiftResponse>> earn(
			@RequestHeader(value = ComnConst.HEADER_AUTHORIZATION, required = false) String auth,
            @RequestHeader(value = ComnConst.HEADER_JWT_DECODE_KEY, required = false) String xCustomerProfile,
			@RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
			@RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
			@RequestHeader(ComnConst.KEY_LANGUAGE) String language,
			@RequestHeader(ComnConst.HEADER_PLATFORM) String platform,
			@RequestHeader(ComnConst.HEADER_VERSION) String version,
			@RequestHeader(ComnConst.HEADER_DEVICE_ID) String deviceId,
			@RequestBody EarnRequest request,
			HttpServletRequest httpRequest) throws Exception {

		AccessTokenJWTPayload jwtPayload = RewardUtill.getAccessTokenFromXCustomerProfile(auth, xCustomerProfile);
		HashMap<String, Object> tv = new HashMap<>();
		TransactionValueUtil.initTransactionValue(tv, host, httpRequest, jwtPayload);
		TransactionResult transactionResult = this.earnTransaction.earn(tv, httpRequest, request, jwtPayload);

		DefaultJsonResponse<EarnRedeemtionGiftResponse> data = new DefaultJsonResponse<>(tv, transactionResult);
		GenericJsonResponse<EarnRedeemtionGiftResponse> getRedeemDealScanCodeRsp = new GenericJsonResponse<>(tv, transactionResult, data.getData());
		return ResponseEntity.status(transactionResult.getHttpStatus()).body(getRedeemDealScanCodeRsp);
		
	}

}
