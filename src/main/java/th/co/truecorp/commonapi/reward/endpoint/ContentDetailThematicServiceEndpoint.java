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
import th.co.truecorp.commonapi.reward.model.endpoint.ApiGwContentDetailThematicRsp;
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
public class ContentDetailThematicServiceEndpoint {

    private static final Logger log = LoggerFactory.getLogger(ContentDetailThematicServiceEndpoint.class);

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

    @EndpointLog(name = "APIGW.GetContentDetail")
    public EndpointResult getContentDetailService(Map<String, Object> tv) throws Exception {
        EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        try {
            log.info("call apigw " + Constant.ENDPOINT_SERVICE_GET_CONTENT);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            ApiGwContentDetailThematicRsp contentDetailThematicRsp = new ApiGwContentDetailThematicRsp();
            EndpointResult endpointResult = null;
            EndpointResultRWD endpointResult2 = new EndpointResultRWD();

            HttpHeaders headers = createHeaders(tv);
            Map<String, Object> pathParams = createPathParams(tv);
            Map<String, Object> queryParams = createQueryParams(tv);

            ResponseEntity<ApiGwContentDetailThematicRsp> gwResponse = apigwService.execute(logContext,
                    HttpMethod.GET,
                    Constant.ENDPOINT_SOURCE_SYSTEM_ID,
                    Constant.ENDPOINT_SERVICE_GET_CONTENT,
                    ApiGwContentDetailThematicRsp.class,
                    tv,
                    headers,
                    pathParams,
                    queryParams
            );

            if (gwResponse != null && !gwResponse.getStatusCode().is2xxSuccessful()) {
                var body = gwResponse.getBody();
                String descriptionApigw = body != null ? body.getDescription() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                String code = body != null && body.getCode() != null ? body.getCode().replace("\"", "") : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                String messageApigw = body != null ? body.getMessage() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                String businessErrorApigw = body != null ? body.getBusinessError() : null;
                String errorApigw = body != null ? body.getError() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                String timestamp = body != null && body.getTimestamp() != null ? body.getTimestamp() : "";
                log.info(Constant.ENDPOINT_SERVICE_GET_CONTENT + " fail with message " + descriptionApigw);
                String errorMessage = rewardUtill.mapError(descriptionApigw, messageApigw, errorApigw, timestamp);

                endpointResult2 = errorService.mapErrorCode(Constant.QUERY_DATA,
                        tv.get("brand").toString().toUpperCase(),
                        code,
                        tv.get("language").toString().toUpperCase(),
                        errorMessage,
                        null != businessErrorApigw ? businessErrorApigw : Constant.N_A,
                        Constant.MESSAGE);
                tv.put(Constant.CONTENT_DETAIL_THEMATIC, null);
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
                        errorMessage);
            } else {
                contentDetailThematicRsp = gwResponse.getBody();
                log.info(Constant.ENDPOINT_SERVICE_GET_CONTENT + " success with data " + new Gson().toJson(contentDetailThematicRsp));
                tv.put(Constant.CONTENT_DETAIL_THEMATIC, contentDetailThematicRsp);
                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                endpointResult2 = errorService.convertMapResult(endpointResult);
                tv.put(Constant.ENDPOINT_RESULT_RWD, endpointResult2);
            }
            return endpointResult;
        } catch (Exception exception) {
            log.error("Error during execute apigw: {}", "GetContent", exception);
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
        return new HashMap<>();
    }

    private Map<String, Object> createQueryParams(Map<String, Object> tv) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("country", Constant.TH.toLowerCase());
        queryParams.put("lang", Constant.TH.toLowerCase());
        queryParams.put("expand", Constant.PRIVILEGE_LIST);
        queryParams.put("expand_limit", 1);
        queryParams.put("fields", "show_redeem_button,privilege_list,card_type,campaign_type,sub_campaign_type,campaign_code,detail,redeem_point,thumb_list,expire_date,info,allow_app,setting,term_and_condition");
        queryParams.put("cms_id", tv.get("campaignId"));

        return queryParams;
    }

}
