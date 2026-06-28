package th.co.truecorp.commonapi.reward.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import th.co.truecorp.commonapi.reward.cms.jpa.service.CustomMappingMessageService;
import th.co.truecorp.commonapi.reward.cms.jpa.service.ErrorService;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.service.APIGWService;

import java.util.*;

@Component
public class CampaignGroupServiceEndpoint {

	private static final Logger log = LoggerFactory.getLogger(CampaignGroupServiceEndpoint.class);

	@Autowired
	private LogContextService logContextService;

	@Autowired
	private ResultService resultService;

	@Autowired
	private APIGWService apigwService;

	@Autowired
	private APIGWUtill apigwUtill;

	@Autowired
	private RewardUtill rewardUtill;

	@Autowired
	private ErrorService errorService;

	@Autowired
	private CustomMappingMessageService customMappingMessageService;

	@EndpointLog(name = Constant.ENDPOINT_SOURCE_SYSTEM_ID_APIGW+".campaignGroup")
	public EndpointResult getCampaignGroup(Map<String, Object> tv) {

		log.info("start process getCampaignGroup");

		EndpointResult endpointResult = null;
		EndpointResultRWD endpointResult2 = new EndpointResultRWD();
		EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
		try {
			HttpHeaders headers = createHeaders(tv);
			Map<String, Object> pathParams = createPathParams(tv);
			Map<String, Object> queryParams = createQueryParams(tv);
			ResponseEntity<CampaignGroupResponse> gwResponse = apigwService.execute(
					logContext,
					HttpMethod.GET,
					Constant.ENDPOINT_SOURCE_SYSTEM_ID,
					Constant.ENDPOINT_SERVICE_CAMPAIGN_GROUP,
					CampaignGroupResponse.class,
					tv,
					headers,
					pathParams,
					queryParams
			);

			logContext.putA("srvTxId",pathParams.get("id"));

			if (gwResponse != null && !gwResponse.getStatusCode().is2xxSuccessful()) {
				log.error("api failed!");
				String errorCode = Objects.requireNonNull(gwResponse.getBody()).getCode();
				String descriptionApigw = Objects.requireNonNull(gwResponse.getBody()).getDescription();
				String messageApigw = Objects.requireNonNull(gwResponse.getBody()).getMessage();
				String errorApigw = Objects.requireNonNull(gwResponse.getBody()).getError();
				String errorMessage = rewardUtill.mapError(descriptionApigw,messageApigw,errorApigw, String.valueOf(gwResponse.getBody().getTimestamp()));

				try {
					endpointResult2 = errorService.mapErrorCode(Constant.REDEEM,
							Constant.TRUE.toUpperCase(),
							Objects.requireNonNull(gwResponse.getBody()).getCode(),
							tv.get(ComnConst.KEY_LANGUAGE).toString(),
							errorMessage,
							null != gwResponse.getBody().getBusinessError() ? Objects.requireNonNull(gwResponse.getBody()).getBusinessError() : Constant.N_A,
							Constant.MESSAGE);
					tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
					endpointResult = resultService.mapEndpointResultAPIGW(tv,Constant.ENDPOINT_SOURCE_SYSTEM_ID,Constant.ENDPOINT_SERVICE_GET_SHELF_CONTENT, gwResponse.getBody().getCode(), gwResponse.getStatusCode().value(),
							endpointResult2.getEndpointStatusType(),
							endpointResult2.getHttpStatus(),
							endpointResult2.getEndpointResponseCode(),
							errorCode,
							errorMessage);
				} catch (Exception e){
					log.error("not found error code in database: "+ Objects.requireNonNull(gwResponse.getBody()).getCode());
					endpointResult2 = errorService.convertMapResult(errorService.mapErrorException(e,tv));
					tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
				}
			} else {
				log.info("api success!");
				endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
				endpointResult2 = errorService.convertMapResult(endpointResult);
				tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
			}

			tv.put(Constant.ENDPOINT_SERVICE_CAMPAIGN_GROUP, gwResponse.getBody());

			return endpointResult;
		} catch (Exception exception) {
			log.error("error in get transaction "+exception.getMessage());
			endpointResult = errorService.mapErrorException(exception,tv);
			endpointResult2 = errorService.convertMapResult(endpointResult);
			tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
		}
		return endpointResult;
	}

	private HttpHeaders createHeaders(Map<String, Object> tv) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", tv.get(ComnConst.HEADER_AUTHORIZATION).toString());
		return headers;
	}

	private Map<String, Object> createPathParams(Map<String, Object> tv) {
		Map<String, Object> pathParams = new HashMap<>();
		return pathParams;
	}

	private Map<String, Object> createQueryParams(Map<String, Object> tv) throws Exception {
		Map<String, Object> queryParams = new HashMap<>();

		queryParams.put("id", apigwUtill.generateRewardRedeemBackendId("RWDGM"));
		queryParams.put("levelSegment", tv.get("levelSegment"));
		queryParams.put("groupId", tv.get("itemMapping"));
		return queryParams;
	}

}
