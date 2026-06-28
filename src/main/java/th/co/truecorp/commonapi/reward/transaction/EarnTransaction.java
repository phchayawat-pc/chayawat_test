package th.co.truecorp.commonapi.reward.transaction;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.common.utils.ValidateUtil;
import th.co.truecorp.commonapi.reward.endpoint.ProfileEndpoint;
import th.co.truecorp.commonapi.reward.model.EarnRequest;
import th.co.truecorp.commonapi.reward.model.EndpointResultRWD;
import th.co.truecorp.commonapi.reward.model.endpoint.GetProfileFromDigitalIdResponse;
import th.co.truecorp.commonapi.reward.service.EarnService;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.TransactionLog;
import th.co.truecorp.commonlib.log.context.LogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.exception.AppCustomException;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.log.model.TransactionResult;
import th.co.truecorp.commonlib.model.AccessTokenJWTPayload;
import th.co.truecorp.commonlib.util.LoggerParameterUtil;

@Service
public class EarnTransaction {
	
	private static Logger log = LoggerFactory.getLogger(EarnTransaction.class);
	
	@Autowired
	private LogContextService logContextService;
	
	@Autowired
	private ResultService resultService;
	
	@Autowired
	private ProfileEndpoint profileEndpoint;
	
	@Autowired
	private EarnService earnService;
	
	@Autowired
	private RewardUtill rewardUtill;
	
	@Autowired
	private ValidateUtil validateUtil;
	
	@TransactionLog(name = "earn")
	public TransactionResult earn(Map<String, Object> tv, HttpServletRequest httpRequest, EarnRequest request, AccessTokenJWTPayload userData) throws Exception {
		log.info("Begin earn...");
		final LogContext logContext = logContextService.getCurrentContext();
		String productBrand = null;
		String productIdDec = null;
		String digitalId = request.getDigitalId();
		EndpointResultRWD response = null;
		String campaignCode = request.getCampaignCode();
		String bzbProductId = request.getBzbProductId();
		String bzbAmount = request.getBzbAmount();
		
		boolean isLogin = (null != userData && userData.isLogin());

		try {
			
			logContext.loadHttpRequest(tv, httpRequest);
			
			if (validateUtil.apiEarnPointAntCoin(isLogin, productBrand, digitalId, campaignCode, bzbProductId, bzbAmount)) {
				throw new AppCustomException("00404", "");
			}
			
			if (!isLogin) {
				log.info("guest mode");
				Map<String, Object> pathParams = new LinkedHashMap<>();
	            pathParams.put("digitalId", request.getDigitalId());
	            Multimap<String, Object> queryParams = ArrayListMultimap.create();
	            queryParams.put("profileType", "profile");
	            log.info("call to get profile");
				EndpointResult endpointResultGetOperator = this.profileEndpoint.getProfileFromDigitalId(tv, request.getDigitalId(), pathParams, queryParams);
				
				if (!ComnConst.STTS_TYPE_SUCC.equals(endpointResultGetOperator.getEndpointStatusType()) && !endpointResultGetOperator.getEndpointErrorCode().contains("00405")) {
					return resultService.findTransactionResult(tv, "57000");
                } else if (!ComnConst.STTS_TYPE_SUCC.equals(endpointResultGetOperator.getEndpointStatusType()) && endpointResultGetOperator.getEndpointErrorCode().contains("00405")) {
                	return resultService.findTransactionResult(tv, "56000");
                }
				
				GetProfileFromDigitalIdResponse getProfileFromDigitalIdResponse = (GetProfileFromDigitalIdResponse) tv.get("GetProfileFromDigitalID");
				
				if (null != getProfileFromDigitalIdResponse.getData().getProfile()
						&& null != getProfileFromDigitalIdResponse.getData().getProfile().getStatus().getErrorCode()
						&& (getProfileFromDigitalIdResponse.getData().getProfile().getStatus().getErrorCode().contains("70050")
								|| getProfileFromDigitalIdResponse.getData().getProfile().getStatus().getErrorCode().contains("70054"))) {
					return resultService.findTransactionResult(tv, "56000");
				}
				
				log.info("get profile success");
				productIdDec = this.rewardUtill.beDecrypt(getProfileFromDigitalIdResponse.getData().getProfile().getProductId());
				productBrand = getProfileFromDigitalIdResponse.getData().getProfile().getProductBrand();
				digitalId = getProfileFromDigitalIdResponse.getData().getProfile().getDigitalId();
			} else {
				log.info("login mode");
				productIdDec = this.rewardUtill.decryptForFE(userData.getClientId(), userData.getCustomerInfo().getProductId());
				productBrand = userData.getCustomerInfo().getProductBrand();
				digitalId = userData.getCustomerInfo().getDigitalId();
			}
			
			if (validateUtil.apiEarnPointAntCoin(isLogin, productBrand, digitalId, campaignCode, bzbProductId, bzbAmount)) {
				throw new AppCustomException("00404", "");
			}
			
			if ("DTAC".equals(productBrand)) {
				log.info("call earn dtac");
				response = this.earnService.getDtacEarnPoint(tv, digitalId, request.getBzbProductId(), request.getBzbAmount());
			} else  {
				log.info("call earn true");
				response = this.earnService.getTrueCoupon(tv, request.getCampaignCode(), productIdDec);
			}

			log.info("earn data retrieved successfully. Converting result to JSON.");
			String json = new Gson().toJson(response);

			EndpointResult endpointResult = new Gson().fromJson(json, EndpointResult.class);

			return  new TransactionResult(endpointResult);
		
		} catch (AppCustomException appCustException) {
			return resultService.findTransactionResult(tv, appCustException.getCode());
		} catch (Exception exception) {
			LoggerParameterUtil.error(log, exception);
			return resultService.getTransactionExceptionResult(tv, exception);
		} finally {
			//MDC.clear(); used framework 0.53
			log.info("End earn...");
		}
	}

}