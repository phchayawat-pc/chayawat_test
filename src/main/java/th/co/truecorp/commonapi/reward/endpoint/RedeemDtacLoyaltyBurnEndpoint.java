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
import th.co.truecorp.commonapi.reward.model.endpoint.ApiGwLoyaltyBurnRsp;
import th.co.truecorp.commonapi.reward.model.endpoint.LoyaltyBurnRsp;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.service.APIGWService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class RedeemDtacLoyaltyBurnEndpoint {

    private static final Logger log = LoggerFactory.getLogger(RedeemDtacLoyaltyBurnEndpoint.class);

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

    @EndpointLog(name = Constant.ENDPOINT_SOURCE_SYSTEM_ID_APIGW + ".getRedeemDtacLoyaltyBurn")
    public EndpointResult getRedeemDtacLoyaltyBurn(Map<String, Object> tv) throws Exception {

        EndpointLogContext logContext = logContextService.getEndpointLoggingContext();

        try {
            LoyaltyBurnRsp loyaltyBurnRsp = new LoyaltyBurnRsp();
            EndpointResult endpointResult = null;
            EndpointResultRWD endpointResult2 = new EndpointResultRWD();

            log.info("Call GetLoyaltyBurn");
            HttpHeaders headers = createHeaders(tv);
            Map<String, Object> pathParams = createPathParams(tv);
            Map<String, Object> queryParams = createQueryParams(tv);

            log.info("Calling API GetLoyaltyBurn with endpoint");
            ResponseEntity<ApiGwLoyaltyBurnRsp> gwResponse = apigwService.execute(
                    logContext,
                    HttpMethod.GET,
                    Constant.ENDPOINT_SOURCE_SYSTEM_ID,
                    Constant.ENDPOINT_SERVICE_GET_LOYALTYBURN,
                    ApiGwLoyaltyBurnRsp.class,
                    tv,
                    headers,
                    pathParams,
                    queryParams
            );

            if (gwResponse != null && gwResponse.getStatusCode() != null && !gwResponse.getStatusCode().is2xxSuccessful()) {
                log.error("api failed!");
                var body = gwResponse.getBody();
                String errorCode = body != null ? body.getCode() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                String messageApigw = body != null ? body.getMessage() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                String descriptionApigw = body != null ? body.getDescription() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                String businessErrorApigw = body != null ? body.getBusinessError() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                String errorApigw = body != null ? body.getError() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                String timestamp = body != null && body.getTimestamp() != null ? body.getTimestamp() : "";

                String errorMessage = rewardUtill.mapError(descriptionApigw, messageApigw, errorApigw, timestamp);

                endpointResult2 = errorService.mapErrorCode(Constant.ELIGIBLE,
                        tv.get("brand").toString().toUpperCase(),
                        Objects.requireNonNull(errorCode),
                        tv.get(ComnConst.KEY_LANGUAGE).toString(),
                        errorMessage,
                        null != businessErrorApigw ? businessErrorApigw : Constant.N_A,
                        Constant.BUTTON);
                loyaltyBurnRsp = null;
                tv.put(Constant.ENDPOINT_RESULT_RWD, endpointResult2);
                endpointResult = resultService.mapEndpointResultAPIGW(tv, Constant.ENDPOINT_SOURCE_SYSTEM_ID, Constant.ENDPOINT_SERVICE_GET_SHELF_CONTENT, errorCode, gwResponse.getStatusCode().value(),
                        endpointResult2.getEndpointStatusType(),
                        endpointResult2.getHttpStatus(),
                        endpointResult2.getEndpointResponseCode(),
                        errorCode,
                        errorMessage);
            } else {
                log.info("api success!");
                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                endpointResult2 = errorService.convertMapResult(endpointResult);
                tv.put(Constant.ENDPOINT_RESULT_RWD, endpointResult2);
                loyaltyBurnRsp = parseLoyaltyBurnRsp(gwResponse != null ? gwResponse.getBody() : null, tv);
            }

            tv.put("getRedeemDtacLoyaltyBurn", loyaltyBurnRsp);

            return endpointResult;
        } catch (Exception exception) {
            log.error("Error during execute apigw: {}", Constant.ENDPOINT_SERVICE_GET_LOYALTYBURN, exception);
            EndpointResult endpointResult = errorService.mapErrorException(exception, tv);
            tv.put(Constant.ENDPOINT_RESULT_RWD, errorService.convertMapResult(endpointResult));
            return endpointResult;
        }
    }

    private HttpHeaders createHeaders(Map<String, Object> tv) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tv.get(Constant.AUTHORIZATION).toString());
        return headers;
    }

    private Map<String, Object> createPathParams(Map<String, Object> tv) {
        Map<String, Object> pathParams = new HashMap<>();
        return pathParams;
    }

    private Map<String, Object> createQueryParams(Map<String, Object> tv) {
        Map<String, Object> queryParams = new HashMap<>();

        queryParams.put("id", tv.get("id"));
        queryParams.put("href", tv.get("href"));
        queryParams.put("user.id", tv.get("user.id"));
        queryParams.put("user.href", tv.get("user.href"));//segment decrypt from FE
        queryParams.put("privilege.function", tv.get("privilege.function"));
        queryParams.put("requestDateTime", tv.get("requestDateTime"));
        queryParams.put("lang", tv.get("lang"));
        queryParams.put("loyaltyProgramProduct.id", tv.get("loyaltyProgramProduct.id"));
        queryParams.put("loyaltyProgramProduct.productSerialNumber", tv.get("loyaltyProgramProduct.productSerialNumber"));
        queryParams.put("relatedParty.id", tv.get("relatedParty.id"));
        queryParams.put("relatedParty.name", tv.get("relatedParty.name"));
        return queryParams;
    }

    private LoyaltyBurnRsp parseLoyaltyBurnRsp(ApiGwLoyaltyBurnRsp rootNode, Map<String, Object> tv) throws Exception {
        LoyaltyBurnRsp loyaltyBurnRsp = new LoyaltyBurnRsp();
        log.debug("map response value from api...");

        if (rootNode != null) {
            loyaltyBurnRsp.setStatus(rootNode.getStatus());
            loyaltyBurnRsp.setName(rootNode.getName());
            loyaltyBurnRsp.setDescription(rootNode.getDescription());
            loyaltyBurnRsp.setQuantity(rootNode.getQuantity());
            loyaltyBurnRsp.setRemark(rootNode.getRemark());
        } else {
            loyaltyBurnRsp = null;
        }

        log.debug("map response value from api success");
        return loyaltyBurnRsp;
    }

}
