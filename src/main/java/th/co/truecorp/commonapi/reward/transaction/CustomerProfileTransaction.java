package th.co.truecorp.commonapi.reward.transaction;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.common.endpoint.CommonServiceEndpoint;
import th.co.truecorp.commonapi.reward.model.CustomerProfileRsp;
import th.co.truecorp.commonapi.reward.service.CustomerProfileService;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ConfigService;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.TransactionLog;
import th.co.truecorp.commonlib.log.context.LogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.exception.EndpointServiceException;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.log.model.TransactionResult;
import th.co.truecorp.commonlib.model.AccessTokenJWTPayload;
import java.util.Map;

@Service
public class CustomerProfileTransaction {

    private static Logger log = LoggerFactory.getLogger(CustomerProfileTransaction.class);

    @Autowired
    private ResultService resultService;

    @Autowired
    private LogContextService logContextService;

    @Autowired
    private CommonServiceEndpoint commonServiceEndpoint;

    @Autowired
    private CustomerProfileService customerProfileService;

    @TransactionLog(name = "getProfile")
    public TransactionResult getProfile(Map<String, Object> tv, HttpServletRequest httpRequest) throws Exception{
        final LogContext logContext = logContextService.getCurrentContext();
        try {
            logContext.loadHttpRequest(tv, httpRequest);

            AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);
            tv.put("digitalId", accessTokenJWTPayload.getCustomerInfo().getDigitalId());
            tv.put("productBrand", accessTokenJWTPayload.getCustomerInfo().getProductBrand());
            tv.put("productId", accessTokenJWTPayload.getCustomerInfo().getProductId());
            EndpointResult resultProfile = commonServiceEndpoint.getCommonService(tv);

            CustomerProfileRsp commonProfileRsp = (CustomerProfileRsp) tv.get("commonProfileRspEndpoint");
            if (commonProfileRsp != null) {
                CustomerProfileRsp customerProfileResponse = customerProfileService.getCustProfileService(commonProfileRsp);
                if (customerProfileResponse != null) {
                    tv.put("data", customerProfileResponse);
                }
            }

            return new TransactionResult(resultProfile);
        } catch (Exception exception) {
            return handleException(tv, exception);
        }
    }

    private TransactionResult handleException(Map<String, Object> tv, Exception exception) {
        log.error("Exception occurred: {}", exception.getMessage(), exception);

        return resultService.getTransactionExceptionResult(tv, exception);
    }


    @TransactionLog(name = "getProfileHowToUseTransaction")
    public TransactionResult getProfileHowToUseTransaction(Map<String, Object> tv, HttpServletRequest httpRequest) throws EndpointServiceException {

        final LogContext logContext = logContextService.getCurrentContext();

        try {
            logContext.loadHttpRequest(tv, httpRequest);

            EndpointResult resultProfile = customerProfileService.getProfileHowToUseService(tv);

            return new TransactionResult(resultProfile);

        } catch (EndpointServiceException endpointServiceException) {
            return resultService.getTransactionExceptionResult(endpointServiceException);
        } catch (Exception exception) {
            return resultService.getTransactionExceptionResult(tv, exception);
        }

    }

    @TransactionLog(name = "getTierDetailTransaction")
    public TransactionResult getTierDetailTransaction(Map<String, Object> tv, HttpServletRequest httpRequest) {

        final LogContext logContext = logContextService.getCurrentContext();

        try {
            logContext.loadHttpRequest(tv, httpRequest);

            EndpointResult resultProfile = customerProfileService.getProfileTierDetailService(tv);

            return new TransactionResult(resultProfile);
        } catch (EndpointServiceException endpointServiceException) {
            return resultService.getTransactionExceptionResult(endpointServiceException);
        }  catch (Exception exception) {
            return resultService.getTransactionExceptionResult(tv, exception);
        }
    }
}
