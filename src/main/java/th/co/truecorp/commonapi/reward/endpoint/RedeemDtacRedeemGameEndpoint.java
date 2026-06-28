package th.co.truecorp.commonapi.reward.endpoint;

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
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemGame.req.RedeemGameDataApiReq;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemGame.res.RedeemGameResponse;
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
public class RedeemDtacRedeemGameEndpoint {

    private static final Logger log = LoggerFactory.getLogger(RedeemDtacRedeemGameEndpoint.class);

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

    @EndpointLog(name = Constant.ENDPOINT_SOURCE_SYSTEM_ID_APIGW + ".getRedeemGameEndpoint")
    public EndpointResult getRedeemGameEndpoint(Map<String, Object> tv, Map<String, Object> pathParams, Map<String, Object> queryParams, RedeemGameDataApiReq req) throws Exception {

        EndpointLogContext logContext = logContextService.getEndpointLoggingContext();

        try {
            EndpointResult endpointResult = null;
            EndpointResultRWD endpointResult2 = new EndpointResultRWD();

            log.info("Call RedeemGameEndpoint");
            HttpHeaders headers = createHeaders(tv);

            log.debug("Calling API RedeemGame with endpoint");
            ResponseEntity<RedeemGameResponse> gwResponse = apigwService.execute(
                    logContext,
                    HttpMethod.POST,
                    Constant.ENDPOINT_SOURCE_SYSTEM_ID,
                    Constant.ENDPOINT_SERVICE_GET_REDEEM_GAME,
                    RedeemGameResponse.class,
                    tv,
                    headers,
                    pathParams,
                    queryParams,
                    req
            );

            log.info("Response from RedeemGameEndpoint API: {}", gwResponse);

            tv.put(Constant.ENDPOINT_SERVICE_GET_REDEEM_GAME, gwResponse.getBody());
            logContext.putA("srvTxId", req.getRefId());

            if (gwResponse != null && gwResponse.getStatusCode() != null && !gwResponse.getStatusCode().is2xxSuccessful()) {
                log.error("api failed!");
                String errorCode = Objects.requireNonNull(gwResponse.getBody()).getCode();
                String description = Objects.requireNonNull(gwResponse.getBody()).getDescription();
                String message = Objects.requireNonNull(gwResponse.getBody()).getMessage();
                String error = Objects.requireNonNull(gwResponse.getBody()).getError();
                String errorMessage = rewardUtill.mapError(description, message, error, gwResponse.getBody().getTimestamp());

                endpointResult2 = errorService.mapErrorCode(Constant.REDEEM,
                        tv.get("brand").toString().toUpperCase(),
                        Objects.requireNonNull(gwResponse.getBody()).getCode(),
                        tv.get(ComnConst.KEY_LANGUAGE).toString().toUpperCase(),
                        errorMessage,
                        null != gwResponse.getBody().getBusinessError() ? Objects.requireNonNull(gwResponse.getBody()).getBusinessError() : Constant.N_A,
                        Constant.MESSAGE);
                tv.put(Constant.ENDPOINT_RESULT_RWD, endpointResult2);
                endpointResult = resultService.mapEndpointResultAPIGW(tv, Constant.ENDPOINT_SOURCE_SYSTEM_ID, Constant.ENDPOINT_SERVICE_GET_SHELF_CONTENT, gwResponse.getBody().getCode(), gwResponse.getStatusCode().value(),
                        endpointResult2.getEndpointStatusType(),
                        endpointResult2.getHttpStatus(),
                        endpointResult2.getEndpointResponseCode(),
                        errorCode,
                        errorMessage);
            } else {
                log.debug("api success!");
                tv.put("refId", req.getRefId());
                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                endpointResult2 = errorService.convertMapResult(endpointResult);
                tv.put(Constant.ENDPOINT_RESULT_RWD, endpointResult2);
            }

            return endpointResult;
        } catch (Exception exception) {
            log.error("Error during execute apigw: {}", Constant.ENDPOINT_SERVICE_GET_REDEEM_GAME, exception);
            EndpointResult endpointResult = errorService.mapErrorException(exception, tv);
            tv.put(Constant.ENDPOINT_RESULT_RWD, errorService.convertMapResult(endpointResult));
            return endpointResult;
        }
    }

    private HttpHeaders createHeaders(Map<String, Object> tv) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tv.get(Constant.AUTHORIZATION).toString());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
