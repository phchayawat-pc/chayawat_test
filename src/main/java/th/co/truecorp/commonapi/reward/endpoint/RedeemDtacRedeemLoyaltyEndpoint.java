package th.co.truecorp.commonapi.reward.endpoint;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import th.co.truecorp.commonapi.reward.cms.jpa.service.ErrorService;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.model.EndpointResultRWD;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemLoyaltyBurn.req.LoyaltyBurnReq;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemLoyaltyBurn.res.LoyaltyBurnResponse;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.service.APIGWService;

import java.util.Map;
import java.util.Objects;

@Component
public class RedeemDtacRedeemLoyaltyEndpoint {

    private static final Logger log = LoggerFactory.getLogger(RedeemDtacRedeemLoyaltyEndpoint.class);

    @Autowired
    private LogContextService logContextService;

    @Autowired
    private APIGWService apigwService;

    @Autowired
    private ResultService resultService;

    @Autowired
    private RewardUtill rewardUtill;

    @Autowired
    private ErrorService errorService;

    @EndpointLog(name = Constant.ENDPOINT_SOURCE_SYSTEM_ID_APIGW+".getRedeemLoyaltyBurnEndpoint")
    public EndpointResult getRedeemLoyaltyBurnEndpoint(Map<String, Object> tv, Map<String, Object> pathParams, Map<String,Object> queryParams, LoyaltyBurnReq req, int startRetry) throws Exception {

        EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        int attempCount = startRetry;
        int retryAttemp = Integer.parseInt(Constant.RETRY_ATTEMP);
        int retryInterval = Integer.parseInt(Constant.RETRY_INTERVAL);
        boolean retryFlag = true;

        EndpointResult endpointResult = null;
        EndpointResultRWD endpointResult2 = new EndpointResultRWD();

        while(retryFlag) {
            try {
                log.info("Call getRedeemLoyaltyBurnEndpoint");
                HttpHeaders headers = createHeaders(tv);

                log.debug("Calling API getRedeemLoyaltyBurnEndpoint with endpoint");
                ResponseEntity<LoyaltyBurnResponse> gwResponse = apigwService.execute(
                        logContext,
                        HttpMethod.POST,
                        Constant.ENDPOINT_SOURCE_SYSTEM_ID,
                        Constant.ENDPOINT_SERVICE_GET_REDEEM_LOYALTY,
                        LoyaltyBurnResponse.class,
                        tv,
                        headers,
                        pathParams,
                        queryParams,
                        req
                );

                log.info("Response from getRedeemLoyaltyBurnEndpoint API: {}", new Gson().toJson(gwResponse));
                logContext.putA("srvTxId", req.getId());

                if (gwResponse != null && !gwResponse.getStatusCode().is2xxSuccessful()) {
                    attempCount++;
                    if (attempCount <= retryAttemp) {
                        log.debug("Retry Call : attemp=(" + attempCount + "/" + retryAttemp + "), interval=" + retryInterval + "s");
                        Thread.sleep(retryInterval * 1000);
                        retryFlag = true;
                        continue;
                    } else {
                        if (retryAttemp > 0) {
                            log.error("Out of retry!");
                        } else {
                            log.debug("No retry config");
                        }
                        retryFlag = false;
                    }
                    log.error("api failed!");
                    String errorCode = Objects.requireNonNull(gwResponse.getBody()).getCode();
                    String description = Objects.requireNonNull(gwResponse.getBody()).getDescription();
                    String message = Objects.requireNonNull(gwResponse.getBody()).getMessage();
                    String error = Objects.requireNonNull(gwResponse.getBody()).getError();
                    String errorMessage = rewardUtill.mapError(description,message,error, gwResponse.getBody().getTimestamp());

                    endpointResult2 = errorService.mapErrorCode(Constant.REDEEM,
                            tv.get("brand").toString().toUpperCase(),
                            Objects.requireNonNull(gwResponse.getBody()).getCode(),
                            tv.get(ComnConst.KEY_LANGUAGE).toString().toUpperCase(),
                            errorMessage,
                            null != gwResponse.getBody().getBusinessError() ? Objects.requireNonNull(gwResponse.getBody()).getBusinessError() : Constant.N_A,
                            Constant.MESSAGE);
                    tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
                    endpointResult = resultService.mapEndpointResultAPIGW(tv,Constant.ENDPOINT_SOURCE_SYSTEM_ID,Constant.ENDPOINT_SERVICE_GET_SHELF_CONTENT,Objects.requireNonNull(gwResponse.getBody()).getCode(), gwResponse.getStatusCode().value(),
                            endpointResult2.getEndpointStatusType(),
                            endpointResult2.getHttpStatus(),
                            endpointResult2.getEndpointResponseCode(),
                            errorCode,
                            errorMessage);
                } else {
                    log.debug("api success!");
                    endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                    endpointResult2 = errorService.convertMapResult(endpointResult);
                    tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
                    retryFlag = false;
                }

                tv.put(Constant.ENDPOINT_SERVICE_GET_REDEEM_LOYALTY, gwResponse.getBody());

                if(Objects.equals(tv.get("cmpgType").toString(), Constant.DTAC_REWARD)){
                    rewardUtill.saveRWDHistory(tv,Constant.ENDPOINT_SERVICE_GET_REDEEM_LOYALTY);
                }

                return endpointResult;
            } catch (Exception exception) {
                log.error("Error during execute apigw: {}", Constant.ENDPOINT_SERVICE_GET_REDEEM_LOYALTY, exception);
                endpointResult = errorService.mapErrorException(exception,tv);
                tv.put(Constant.ENDPOINT_RESULT_RWD, errorService.convertMapResult(endpointResult));
                return endpointResult;

            }
        }
        return endpointResult;
    }

    private HttpHeaders createHeaders(Map<String, Object> tv) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tv.get(Constant.AUTHORIZATION).toString());
        return headers;
    }
}
