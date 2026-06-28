package th.co.truecorp.commonapi.reward.endpoint;

import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.model.endpoint.GetProfileFromDigitalIdResponse;
import th.co.truecorp.commonlib.constant.LoggingKey;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.service.CommonBEService;
import th.co.truecorp.commonlib.util.LoggerParameterUtil;

import java.util.Map;

@Component
public class ProfileEndpoint {

    private static Logger log = LoggerFactory.getLogger(ProfileEndpoint.class);
    @Autowired
    private LogContextService logContextService;
    @Autowired
    private ResultService resultService;
    @Autowired
    private CommonBEService commonBEService;


    @EndpointLog(name = "PROFILE.getProfileFromDigitalId")
    public EndpointResult getProfileFromDigitalId(Map<String, Object> tv, String digitalId, Map<String, Object> pathParams, Multimap<String, Object> queryParams) throws Exception {
        log.debug("[getProfileFromDigitalId] start");
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            logContext.putA(LoggingKey.ENDPOINT_SRV_TX_ID, tv.get(LoggingKey.TRANSACTION_ID).toString());

            ResponseEntity<GetProfileFromDigitalIdResponse> response = commonBEService.executeMultiParam(
                    logContext,
                    HttpMethod.GET,
                    "TRUEAPP",
                    "GetProfileFromDigitalID",
                    GetProfileFromDigitalIdResponse.class,
                    tv,
                    headers,
                    pathParams,
                    queryParams
            );

            int httpCode = response.getStatusCode().value();

            var body = response.getBody();

            if (httpCode == HttpStatus.OK.value() && body != null) {
                tv.put("GetProfileFromDigitalID", body);
            }

            String errorCode = null;
            if (body != null && body.getStatus() != null) {
                errorCode = body.getStatus().getErrorCode() != null ? body.getStatus().getErrorCode() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;

//				if (errorCode != null && !errorCode.endsWith(ComnConst.STTS_CODE_SUCC_WITH_DATA) && response.getBody().getData() != null) {
//					if (response.getBody().getData().getProfile() != null && response.getBody().getData().getProfile().getStatus() != null) {
//						String errorCodeProfile = response.getBody().getData().getProfile().getStatus().getErrorCode();
//						if (errorCodeProfile != null) {
//							logContext.putA("subErrCodeProfile", errorCodeProfile);
//							tv.put("subErrCodeProfile", errorCodeProfile);
//						}
//					}
//				}
            }

            return resultService.mapEndpointResultCommonBE(tv, "PROFILE", "GetProfileFromDigitalID", errorCode, httpCode);
        } catch (Exception exception) {
            LoggerParameterUtil.error(log, exception);
            return resultService.getEndpointExceptionResult(tv, exception);
        }
    }

}
