package th.co.truecorp.commonapi.reward.endpoint;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import th.co.truecorp.commonapi.reward.cms.jpa.service.ErrorService;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.model.EndpointResultRWD;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.earn.dtacenv.DtacPointAndCoinReq;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.earn.dtacenv.DtacPointAndCoinRes;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemEligibleAndRedeem.req.EligibleAndRedeemReq;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemEligibleAndRedeem.res.EligibleAndRedeemResponse;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.constant.LoggingKey;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.service.APIGWService;

@Component
public class RedeemTrueEligibleAndRedeemEndpoint {

    private static final Logger log = LoggerFactory.getLogger(RedeemTrueEligibleAndRedeemEndpoint.class);

    @Autowired
    private LogContextService logContextService;

    @Autowired
    private APIGWService apigwService;

    @Autowired
    private ResultService resultService;

    @Autowired
    private ErrorService errorService;

    @Autowired
    private RewardUtill rewardUtill;

    @EndpointLog(name = Constant.ENDPOINT_SOURCE_SYSTEM_ID_APIGW+".getRedeemTrueEndpoint")
    public EndpointResult getRedeemTrueEndpoint(Map<String, Object> tv, Map<String, Object> pathParams, Map<String,Object> queryParams, EligibleAndRedeemReq req) throws Exception {

        EndpointLogContext logContext = logContextService.getEndpointLoggingContext();

        try {

            EndpointResult endpointResult = null;
            EndpointResultRWD endpointResult2 = new EndpointResultRWD();

            log.info("Call RedeemTrueEndpoint");
            HttpHeaders headers = createHeaders(tv);

            log.info("Calling API RedeemTrueEndpoint with endpoint");
            ResponseEntity<EligibleAndRedeemResponse> gwResponse = apigwService.execute(
                    logContext,
                    HttpMethod.POST,
                    Constant.ENDPOINT_SOURCE_SYSTEM_ID,
                    Constant.ENDPOINT_SERVICE_GET_REDEEM_ELIGIBLE_AND_REDEEM,
                    EligibleAndRedeemResponse.class,
                    tv,
                    headers,
                    pathParams,
                    queryParams,
                    req
            );

            log.info("Response from RedeemTrueEndpoint API: {}", gwResponse);
            logContext.putA("srvTxId",req.getTrnNo());

            if (gwResponse != null && !gwResponse.getStatusCode().is2xxSuccessful()) {
                log.error("api failed!");
                String errorCode = Objects.requireNonNull(gwResponse.getBody()).getCode();
                String descriptionApigw = Objects.requireNonNull(gwResponse.getBody()).getDescription();
                String messageApigw = Objects.requireNonNull(gwResponse.getBody()).getMessage();
                String errorApigw = Objects.requireNonNull(gwResponse.getBody()).getError();
                String timestamp = Optional.ofNullable(gwResponse.getBody().getTimestamp()).orElse("");
                String errorMessage = rewardUtill.mapError(descriptionApigw,messageApigw,errorApigw,timestamp);

                try {
                    endpointResult2 = errorService.mapErrorCode(Constant.REDEEM,
                            Constant.TRUE.toUpperCase(),
                            Objects.requireNonNull(gwResponse.getBody()).getCode(),
                            tv.get(ComnConst.KEY_LANGUAGE).toString(),
                            errorMessage,
                    null != gwResponse.getBody().getBusinessError() ? Objects.requireNonNull(gwResponse.getBody()).getBusinessError() : Constant.N_A,
                            Constant.MESSAGE);
                    tv.put(Constant.ENDPOINT_RESULT_RWD, Objects.requireNonNullElse(endpointResult2, new EndpointResultRWD()));
                    endpointResult = resultService.mapEndpointResultAPIGW(tv,Constant.ENDPOINT_SOURCE_SYSTEM_ID,Constant.ENDPOINT_SERVICE_GET_SHELF_CONTENT,gwResponse.getBody().getCode(),gwResponse.getStatusCode().value(),
                            endpointResult2.getEndpointStatusType(),
                            endpointResult2.getHttpStatus(),
                            endpointResult2.getEndpointResponseCode(),
                            errorCode,
                            errorMessage);
                } catch (Exception e){
                    log.error("not found error code in database: "+ Objects.requireNonNull(gwResponse.getBody()).getCode());
                    endpointResult2 = errorService.convertMapResult(errorService.mapErrorException(e,tv));
                    tv.put(Constant.ENDPOINT_RESULT_RWD, endpointResult2);
                }
            } else {
                log.info("api success!");
                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                endpointResult2 = errorService.convertMapResult(endpointResult);
                tv.put(Constant.ENDPOINT_RESULT_RWD, endpointResult2);
            }

            tv.put(Constant.ENDPOINT_SERVICE_GET_REDEEM_ELIGIBLE_AND_REDEEM, gwResponse.getBody());

            return endpointResult;
//            throw new RuntimeException("Simulated exception for testing");
        } catch (Exception exception) {
            log.error("Error during execute apigw: {}", Constant.ENDPOINT_SERVICE_GET_REDEEM_ELIGIBLE_AND_REDEEM, exception);
            EndpointResult endpointResult = errorService.mapErrorException(exception,tv);
            tv.put(Constant.ENDPOINT_RESULT_RWD, errorService.convertMapResult(endpointResult));
            return endpointResult;
        }
    }

    private HttpHeaders createHeaders(Map<String, Object> tv) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tv.get(Constant.AUTHORIZATION).toString());
        return headers;
    }
    
    @EndpointLog(name = Constant.ENDPOINT_SOURCE_SYSTEM_ID_APIGW+".getEarnRedeemTrueEndpoint")
    public EndpointResult getEarnRedeemTrueEndpoint(Map<String, Object> tv, Map<String, Object> pathParams, Map<String,Object> queryParams, th.co.truecorp.commonapi.reward.model.endpoint.redeem.earn.trueenv.req.EligibleAndRedeemReq req) throws Exception {

        EndpointLogContext logContext = logContextService.getEndpointLoggingContext();

        try {
            EndpointResult endpointResult = null;
            EndpointResultRWD endpointResult2 = new EndpointResultRWD();

            log.info("Call RedeemTrueEndpoint");
            HttpHeaders headers = createHeaders(tv);

            log.info("Calling API RedeemTrueEndpoint with endpoint");
            ResponseEntity<EligibleAndRedeemResponse> response = apigwService.execute(
                    logContext,
                    HttpMethod.POST,
                    Constant.ENDPOINT_SOURCE_SYSTEM_ID,
                    Constant.ENDPOINT_SERVICE_GET_REDEEM_ELIGIBLE_AND_REDEEM,
                    EligibleAndRedeemResponse.class,
                    tv,
                    headers,
                    pathParams,
                    queryParams,
                    req
            );

            log.info("Response from RedeemTrueEndpoint API: {}", response);
            logContext.putA("srvTxId",req.getTrnNo());

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("api failed!");
                String errorCode = Objects.requireNonNull(response.getBody()).getCode();
                String descriptionApigw = Objects.requireNonNull(response.getBody()).getDescription();
                String messageApigw = Objects.requireNonNull(response.getBody()).getMessage();
                String errorApigw = Objects.requireNonNull(response.getBody()).getError();
                String timestamp = Optional.ofNullable(response.getBody().getTimestamp()).orElse("");

                String errorMessage = descriptionApigw != null ? descriptionApigw :
                        messageApigw != null ? messageApigw :
                                errorApigw != null ? errorApigw :
                                        Constant.NO_ERROR_MESSAGE_FROM_API_GW+timestamp;
                try {
                    endpointResult2 = errorService.mapErrorCode(Constant.REDEEM,
                            Constant.TRUE.toUpperCase(),
                            Objects.requireNonNull(response.getBody()).getCode(),
                            tv.get(ComnConst.KEY_LANGUAGE).toString(),
                            errorMessage,
                            null != response.getBody().getBusinessError() ? Objects.requireNonNull(response.getBody()).getBusinessError() : Constant.N_A,
                            Constant.MESSAGE);
                    tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
                    endpointResult = resultService.mapEndpointResultAPIGW(tv,Constant.ENDPOINT_SOURCE_SYSTEM_ID,Constant.ENDPOINT_SERVICE_GET_SHELF_CONTENT,response.getBody().getCode(),response.getStatusCode().value(),
                            endpointResult2.getEndpointStatusType(),
                            endpointResult2.getHttpStatus(),
                            endpointResult2.getEndpointResponseCode(),
                            errorCode,
                            errorMessage);
                } catch (Exception e){
                    log.error("not found error code in database: "+ Objects.requireNonNull(response.getBody()).getCode());
                    endpointResult2 = errorService.convertMapResult(errorService.mapErrorException(e,tv));
                    tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
                }
            } else {
                log.info("api success!");
                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                endpointResult2 = errorService.convertMapResult(endpointResult);
                tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
            }

            tv.put(Constant.ENDPOINT_SERVICE_GET_REDEEM_ELIGIBLE_AND_REDEEM, response.getBody());

            return endpointResult;
        } catch (Exception exception) {
            log.error("Error during execute apigw: {}", Constant.ENDPOINT_SERVICE_GET_REDEEM_ELIGIBLE_AND_REDEEM, exception);
            EndpointResult endpointResult = errorService.mapErrorException(exception,tv);
            tv.put(Constant.ENDPOINT_RESULT_RWD,errorService.convertMapResult(endpointResult));
            return endpointResult;
        }
    }
    
    @EndpointLog(name = Constant.ENDPOINT_SOURCE_SYSTEM_ID_APIGW + ".getEarnDtacPoint")
    public EndpointResult getEarnDtacPoint(Map<String, Object> tv, Map<String, Object> pathParams, DtacPointAndCoinReq req, String transactionId) throws Exception {
    	log.debug("[getEarnDtacPoint] start");
    	EndpointResult endpointResult = null;
        EndpointResultRWD endpointResult2 = new EndpointResultRWD();
        try {
        	final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();

        	HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			Map<String, Object> queryParams = new LinkedHashMap<>();
			
			logContext.putA(LoggingKey.ENDPOINT_SRV_TX_ID, transactionId);

            ResponseEntity<DtacPointAndCoinRes> response = apigwService.execute(logContext, HttpMethod.POST, Constant.ENDPOINT_SOURCE_SYSTEM_ID, "GetPointService", DtacPointAndCoinRes.class, tv, headers, pathParams, queryParams, req);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("api failed!");
                String errorCode = Objects.requireNonNull(response.getBody()).getCode();
                String descriptionApigw = Objects.requireNonNull(response.getBody()).getError();
                String messageApigw = Objects.requireNonNull(response.getBody()).getMessage();
                String errorApigw = Objects.requireNonNull(response.getBody()).getError();
                String timestamp = Optional.ofNullable(response.getBody().getTimestamp()).orElse("");

                String errorMessage = descriptionApigw != null ? descriptionApigw :
                        messageApigw != null ? messageApigw :
                                errorApigw != null ? errorApigw :
                                        Constant.NO_ERROR_MESSAGE_FROM_API_GW+timestamp;
                try {
                    endpointResult2 = errorService.mapErrorCode(Constant.EARN_POINT,
                            Constant.DTAC.toUpperCase(),
                            Objects.requireNonNull(response.getBody()).getCode(),
                            tv.get(ComnConst.KEY_LANGUAGE).toString(),
                            errorMessage,
                            null != response.getBody().getError() ? Objects.requireNonNull(response.getBody()).getError() : Constant.N_A,
                            Constant.MESSAGE);
                    tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
                    endpointResult = resultService.mapEndpointResultAPIGW(tv,Constant.ENDPOINT_SOURCE_SYSTEM_ID,Constant.ENDPOINT_SERVICE_GET_SHELF_CONTENT,response.getBody().getCode(),response.getStatusCode().value(),
                            endpointResult2.getEndpointStatusType(),
                            endpointResult2.getHttpStatus(),
                            endpointResult2.getEndpointResponseCode(),
                            errorCode,
                            errorMessage);
                } catch (Exception e){
                    log.error("not found error code in database: "+ Objects.requireNonNull(response.getBody()).getCode());
                    endpointResult2 = errorService.convertMapResult(errorService.mapErrorException(e,tv));
                    tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
                }
            } else {
                log.info("api success!");
                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                endpointResult2 = errorService.convertMapResult(endpointResult);
                tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
            }

            tv.put("GetPointService", response.getBody());

//            tv.put("GetPointService", response.getBody());

			int httpCode = response.getStatusCode().value();

			String errorCode = null;
			if (response.getBody() != null) {
				errorCode = response.getBody().getCode();
			}

			return resultService.mapEndpointResultAPIGW(tv, Constant.ENDPOINT_SOURCE_SYSTEM_ID, "GetPointService", errorCode, httpCode);
        } catch (Exception exception) {
            endpointResult = errorService.mapErrorException(exception,tv);
            tv.put(Constant.ENDPOINT_RESULT_RWD,errorService.convertMapResult(endpointResult));
            return endpointResult;

        }
    }
    
}
