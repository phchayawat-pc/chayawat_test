package th.co.truecorp.commonapi.reward.common.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import th.co.truecorp.commonapi.reward.common.model.GetDigitalByDigitalIdResponse;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;

import th.co.truecorp.commonlib.model.AccessTokenJWTPayload;
import th.co.truecorp.commonlib.service.APIGWService;

import th.co.truecorp.commonlib.service.CommonBEService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class CommonServiceProfileEndpoint {

    private static final Logger log = LoggerFactory.getLogger(CommonServiceProfileEndpoint.class);

    @Autowired
    private LogContextService logContextService;

    @Autowired
    private ResultService resultService;

    @Autowired
    private CommonBEService commonBEService;

    @EndpointLog(name = Constant.ENDPOINT_SOURCE_SYSTEM_ID_COMMONBE + ".GetProfileFromDigitalID")
    public EndpointResult getCommonService(Map<String, Object> tv) {
        EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        try {

            HttpHeaders headers = createHeaders(tv);
            Map<String, Object> pathParams = createPathParams(tv);
            Map<String, Object> queryParams = createQueryParams();
            ResponseEntity<GetDigitalByDigitalIdResponse> response = commonBEService.execute(
                    logContext,
                    HttpMethod.GET,
                    Constant.ENDPOINT_SOURCE_SYSTEM_ID,
                    "GetProfileFromDigitalID",
                    GetDigitalByDigitalIdResponse.class,
                    tv,
                    headers,
                    pathParams,
                    queryParams
            );

            GetDigitalByDigitalIdResponse getDigitalByDigitalIdResponse = response.getBody();
            log.info("Response from profile API: {}", getDigitalByDigitalIdResponse);

            logContext.putA("convert", Thread.currentThread().getName());
            if (getDigitalByDigitalIdResponse != null && getDigitalByDigitalIdResponse.getStatus() != null && !ComnConst.STTS_TYPE_SUCC.equals(getDigitalByDigitalIdResponse.getStatus().getStatusType())) {

                var status = getDigitalByDigitalIdResponse.getData() != null && getDigitalByDigitalIdResponse.getData().getProfile() != null ? getDigitalByDigitalIdResponse.getData().getProfile().getStatus() : null;

                String statusType = status != null ? status.getStatusType() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                String errorCode = status != null ? status.getErrorCode() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                String errorMessage = status != null ? status.getErrorMessage() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                String errorDescription = status != null ? status.getErrorDescription() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;

                EndpointResult endpointResult = resultService.mapEndpointResultCommonBE(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA, statusType);
                endpointResult.setEndpointStatusType(statusType);
                endpointResult.setEndpointStatusCode(errorCode);
                endpointResult.setEndpointErrorMessage(errorMessage);
                endpointResult.setEndpointErrorDescription(errorDescription);
                tv.put("serviceProfileRspEndpoint", null);
                tv.put("endpointResult", endpointResult);
                return endpointResult;
            } else {
                EndpointResult endpointResult = resultService.mapEndpointResultCommonBE(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA, getDigitalByDigitalIdResponse.getStatus().getStatusType());
                tv.put("serviceProfileRspEndpoint", getDigitalByDigitalIdResponse.getData().getProfile());
                tv.put("endpointResult", endpointResult);
                return endpointResult;
            }

        } catch (Exception exception) {
            log.error("Error during execute apigw: {}", "GetProfileFromDigitalID", exception);
            return resultService.getEndpointExceptionResult(tv, exception);
        }
    }

    private HttpHeaders createHeaders(Map<String, Object> tv) {
        AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get("x-customer-profile");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(Constant.CUSTOMER_PROFILE, String.valueOf(accessTokenJWTPayload));
        return headers;
    }

    private Map<String, Object> createPathParams(Map<String, Object> tv) {
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("digitalId", tv.get("digitalId"));
        return pathParams;
    }

    private Map<String, Object> createQueryParams() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("profileType", "profile");
        return queryParams;
    }

}
