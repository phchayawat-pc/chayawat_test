package th.co.truecorp.commonapi.reward.endpoint;

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
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemEligibleAndRedeem.req.EligibleAndRedeemBody;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemEligibleAndRedeem.res.EligibleAndRedeemResponse;
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
public class RedeemTrueBodyEligibleAndRedeemEndpoint {

    private static final Logger log = LoggerFactory.getLogger(RedeemTrueBodyEligibleAndRedeemEndpoint.class);

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

    @EndpointLog(name = Constant.ENDPOINT_SOURCE_SYSTEM_ID_APIGW+"."+Constant.ENDPOINT_SERVICE_GET_REDEEM_ELIGIBLE_AND_REDEEM)
    public EndpointResult PostEligibleAndRedeem(Map<String, Object> tv, Map<String, Object> pathParams, Map<String,Object> queryParams, EligibleAndRedeemBody req) throws Exception {

        EndpointLogContext logContext = logContextService.getEndpointLoggingContext();

        try {
            EndpointResult endpointResult = null;
            EndpointResultRWD endpointResult2 = new EndpointResultRWD();

            log.info("Call RedeemTrueEndpoint");
            HttpHeaders headers = createHeaders(tv);

            log.info("Calling API PostEligibleAndRedeem with endpoint");
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
                String errorCode = Objects.requireNonNull(gwResponse.getBody().getCode());
                String message = Objects.toString(gwResponse.getBody().getMessage(), "");
                String description = Objects.toString(gwResponse.getBody().getDescription(), "");
                String businessError = Objects.toString(gwResponse.getBody().getBusinessError(), "");
                String error = Objects.toString(gwResponse.getBody().getError(), "");
                String errorMessage = rewardUtill.mapError(description,message,error, gwResponse.getBody().getTimestamp());

                endpointResult2 = errorService.mapErrorCode(Constant.ELIGIBLE,
                        tv.get("brand").toString().toUpperCase(),
                        Objects.requireNonNull(errorCode),
                        tv.get(ComnConst.KEY_LANGUAGE).toString(),
                        errorMessage,
                        !businessError.isEmpty() ? businessError : Constant.N_A,
                        Constant.BUTTON);
                tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
                endpointResult = resultService.mapEndpointResultAPIGW(tv,Constant.ENDPOINT_SOURCE_SYSTEM_ID,Constant.ENDPOINT_SERVICE_GET_SHELF_CONTENT,errorCode, gwResponse.getStatusCode().value(),
                        endpointResult2.getEndpointStatusType(),
                        endpointResult2.getHttpStatus(),
                        endpointResult2.getEndpointResponseCode(),
                        errorCode,
                        errorMessage);
                tv.put("endpointResult",endpointResult);
            } else {
                log.info("api success!");
                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                endpointResult2 = errorService.convertMapResult(endpointResult);
                tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
                tv.put("endpointResult",endpointResult);
            }

            tv.put(Constant.ENDPOINT_SERVICE_GET_REDEEM_ELIGIBLE_AND_REDEEM, gwResponse.getBody());

            return endpointResult;
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


}
