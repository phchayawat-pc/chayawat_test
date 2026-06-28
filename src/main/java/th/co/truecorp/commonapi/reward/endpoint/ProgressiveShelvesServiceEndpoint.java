package th.co.truecorp.commonapi.reward.endpoint;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import th.co.truecorp.commonapi.reward.model.ProgressiveShelvesRsp;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.service.APIGWService;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProgressiveShelvesServiceEndpoint {

    private static final Logger log = LoggerFactory.getLogger(ProgressiveShelvesServiceEndpoint.class);

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

    @EndpointLog(name = "APIGW.GetProgressiveShelves")
    public EndpointResult getProgressiveShelvesService(Map<String, Object> tv) throws Exception {
        try {
            log.info("call apigw " + Constant.ENDPOINT_SERVICE_GET_INQUIRY_SHELF_DETAIL);
            EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            ProgressiveShelvesRsp progressiveShelvesRsp = new ProgressiveShelvesRsp();
            EndpointResult endpointResult = null;
            EndpointResultRWD endpointResult2 = new EndpointResultRWD();

            HttpHeaders headers = createHeaders(tv);
            Map<String, Object> pathParams = createPathParams(tv);
            Map<String, Object> queryParams = createQueryParams(tv);

            ResponseEntity<ProgressiveShelvesRsp> gwResponse = apigwService.execute(logContext, HttpMethod.GET, Constant.ENDPOINT_SOURCE_SYSTEM_ID, Constant.ENDPOINT_SERVICE_GET_INQUIRY_SHELF_DETAIL, ProgressiveShelvesRsp.class, tv, headers, pathParams, queryParams);

            if (gwResponse != null && !gwResponse.getStatusCode().is2xxSuccessful()) {
                var body = gwResponse.getBody();

                String descriptionApigw = body != null ? body.getDescription() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                String code = (body != null && body.getCode() != null) ? body.getCode().replace("\"", "") : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                String messageApigw = body != null ? body.getMessage() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                String businessErrorApigw = body != null ? body.getBusinessError() : null;
                String errorApigw = body != null ? body.getError() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                String timestamp = body != null && body.getTimestamp() != null ? body.getTimestamp() : "";
                String errorMessage = rewardUtill.mapError(descriptionApigw, messageApigw, errorApigw, timestamp);

                String brand = tv.get("brand") != null ? tv.get("brand").toString().toUpperCase() : null;
                String language = tv.get("language") != null ? tv.get("language").toString().toUpperCase() : Constant.EN;

                endpointResult2 = errorService.mapErrorCode(
                        Constant.QUERY_DATA,
                        brand,
                        code,
                        language,
                        errorMessage,
                        null != businessErrorApigw ? businessErrorApigw : Constant.N_A,
                        Constant.MESSAGE
                );

                tv.put(Constant.PROGRESSIVE_SHELVES, null);
                tv.put(Constant.ENDPOINT_RESULT_RWD, endpointResult2);

                endpointResult = resultService.mapEndpointResultAPIGW(
                        tv,
                        Constant.ENDPOINT_SOURCE_SYSTEM_ID,
                        Constant.ENDPOINT_SERVICE_GET_SHELF_CONTENT,
                        code,
                        gwResponse.getStatusCode().value(),
                        endpointResult2.getEndpointStatusType(),
                        endpointResult2.getHttpStatus(),
                        endpointResult2.getEndpointResponseCode(),
                        code,
                        errorMessage
                );

            } else {
                progressiveShelvesRsp = gwResponse.getBody();
                log.info(Constant.ENDPOINT_SERVICE_GET_INQUIRY_SHELF_DETAIL + " success with data " + new Gson().toJson(progressiveShelvesRsp));
                tv.put(Constant.PROGRESSIVE_SHELVES, progressiveShelvesRsp);
                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                endpointResult2 = errorService.convertMapResult(endpointResult);
                tv.put(Constant.ENDPOINT_RESULT_RWD, endpointResult2);
            }

            return endpointResult;
        } catch (Exception exception) {
            log.error("Error during execute apigw: {}", Constant.ENDPOINT_SERVICE_GET_INQUIRY_SHELF_DETAIL, exception);
            EndpointResult endpointResult = errorService.mapErrorException(exception, tv);
            tv.put(Constant.ENDPOINT_RESULT_RWD, errorService.convertMapResult(endpointResult));
            return endpointResult;
        }
    }

    private HttpHeaders createHeaders(Map<String, Object> tv) {
        HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", tv.get(Constant.AUTHORIZATION).toString());
        return headers;
    }

    private Map<String, Object> createPathParams(Map<String, Object> tv) {

        return new HashMap<>();
    }

    private Map<String, Object> createQueryParams(Map<String, Object> tv) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("country", Constant.TH.toLowerCase());
        queryParams.put("lang", "T");
        queryParams.put("limit", 50);
        queryParams.put("fields", "how_redeem_button,thumb_list,card_type,campaign_type,sub_campaign_type,campaign_code,redeem_point,expire_date,detail,term_and_condition,info,allow_app,setting");
        queryParams.put("cms_id", tv.get("shelfIds"));
        queryParams.put("channel", Constant.TRUE);

        return queryParams;
    }

}
