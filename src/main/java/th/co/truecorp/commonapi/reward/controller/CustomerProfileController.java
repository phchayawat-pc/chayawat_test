package th.co.truecorp.commonapi.reward.controller;

import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonapi.reward.model.endpoint.EncryptModel;
import th.co.truecorp.commonapi.reward.model.jsonResponse.ProfileDefaultResponse;
import th.co.truecorp.commonapi.reward.transaction.CustomerProfileTransaction;
import th.co.truecorp.commonlib.annotations.PullToRefresh;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.model.TransactionResult;
import th.co.truecorp.commonlib.model.AccessTokenJWTPayload;
import th.co.truecorp.commonlib.service.NetworkService;
import th.co.truecorp.commonlib.util.SecurityUtil;
import th.co.truecorp.commonlib.util.TransactionValueUtil;
import th.co.truecorp.commonlib.ws.model.GenericJsonResponse;
import th.co.truecorp.commonlib.annotations.CustomerProfile;

import java.util.*;

@Validated
@RestController
@RequestMapping("/v1")
public class CustomerProfileController {

    private static Logger log = LoggerFactory.getLogger(CustomerProfileTransaction.class);

    @Autowired
    private CustomerProfileTransaction customerProfileTransaction;

    @Autowired
    private NetworkService networkService;

    @Autowired
    private ResultService resultService;

    String host;
    Gson gson = new Gson();
    @PostConstruct
    public void getHost() {
        host = networkService.getHostName();
    }

    @GetMapping(value = "/profile/point-grade", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericJsonResponse<CustomerProfileRsp>> getProfile(
            @RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
            @RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
            @RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
            @RequestHeader(Constant.HEADER_PLATFORM) String platform,
            @RequestHeader(Constant.HEADER_VERSION) String version,
            @RequestHeader(ComnConst.KEY_LANGUAGE) String language,
            @RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token, //jwt token
            @CustomerProfile AccessTokenJWTPayload customerProfile,
            @PullToRefresh boolean isPullToRefresh,
            HttpServletRequest httpRequest) {

        try {

            log.info("----------- In process point-grade request with headers: sessionId={}, sourceSystemId={}, deviceId={}, platform={}, version={}, language={}, authorization={}, isPullToRefresh={}",
                    sessionId, sourceSystemId, deviceId, platform, version, language, token,isPullToRefresh);

            HashMap<String, Object> tv = initializeTransactionValue(httpRequest,customerProfile);
            log.info("Header pull-refresh: {}", httpRequest.getHeader("pull-refresh"));
            log.info("Resolved PullToRefresh: {}", isPullToRefresh);
            tv.put(Constant.IS_PULL_TO_REFRESH,isPullToRefresh);
            AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);

            String brand = accessTokenJWTPayload.getCustomerInfo().getProductBrand().toLowerCase();
            tv.put("brand", brand);
            TransactionResult transactionResult = customerProfileTransaction.getProfile(tv, httpRequest);
            CustomerProfileResponse<CustomerProfileRsp> data = new CustomerProfileResponse(tv, transactionResult);
            GenericJsonResponse<CustomerProfileRsp> jsonResponse = new GenericJsonResponse<>(tv, transactionResult,
                    data.getData());

            transactionResult.setHttpStatus(Objects.equals(transactionResult.getStatusType(), "T") ? 500 : Objects.equals(transactionResult.getStatusType(), "B") ? 400 : 200);

          return  ResponseEntity.status(transactionResult.getHttpStatus()).body(jsonResponse);

        } catch (Exception e) {
            // Handle exceptions
            return handleException(e);
        }
    }

    private HashMap<String, Object> initializeTransactionValue(HttpServletRequest httpRequest,AccessTokenJWTPayload customerProfile) {
        HashMap<String, Object> tv = new HashMap<>();
        TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
        return tv;
    }

    private GenericJsonResponse<CustomerProfileRsp> createGenericJsonResponse(HashMap<String, Object> tv, CustomerProfileRsp customerProfileRsp, TransactionResult transactionResult) {
        return new GenericJsonResponse<>(tv, transactionResult, customerProfileRsp);
    }

    private ResponseEntity<GenericJsonResponse<CustomerProfileRsp>> handleException(Exception e) {

        HashMap<String, Object> tv = new HashMap<>();
        TransactionResult transactionResult = new TransactionResult();
        transactionResult = resultService.getTransactionExceptionResult(tv,e);
        GenericJsonResponse<CustomerProfileRsp> jsonResponse = createGenericJsonResponse(tv, null, transactionResult);

        // Return error response
        return ResponseEntity.status(transactionResult.getHttpStatus()).body(jsonResponse);
    }


    @GetMapping(value = "/profile/how-to-use-rewards", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericJsonResponse<ProfileHowToUseRsp>> getHowToUse(
            @RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
            @RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
            @RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
            @RequestHeader(Constant.HEADER_PLATFORM) String platform,
            @RequestHeader(Constant.HEADER_VERSION) String version,
            @RequestHeader(ComnConst.KEY_LANGUAGE) @Pattern(regexp = "^(TH|EN|MY|KM)$", message = "Invalid language. Supported languages") @Size(min = 2, max = 2) String language,
            @RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token, //jwt token
            @CustomerProfile AccessTokenJWTPayload customerProfile,
            HttpServletRequest httpRequest) throws Exception {

        HashMap<String, Object> tv = new HashMap<String, Object>();
        TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
        AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);

        String brand = accessTokenJWTPayload.getCustomerInfo().getProductBrand().toLowerCase();
        // do transaction
        tv.put("brand", brand);
        tv.put("lang", language!=null && !language.equals("") ? language : Constant.EN);

        TransactionResult transactionResult = customerProfileTransaction.getProfileHowToUseTransaction(tv, httpRequest);

        ProfileDefaultResponse<ProfileHowToUseRsp> data = new ProfileDefaultResponse(tv, transactionResult);
        GenericJsonResponse<ProfileHowToUseRsp> getCustomerProfileRsp = new GenericJsonResponse<ProfileHowToUseRsp>(tv, transactionResult,
                data.getData());
        log.info("End how-to-use-rewards : "+ gson.toJson(getCustomerProfileRsp));
        return ResponseEntity.status(transactionResult.getHttpStatus()).body(getCustomerProfileRsp);

    }

    @GetMapping(value = "/profile/tier-detail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericJsonResponse<List<ProfileTierDetailRsp>>> getTierDetail(
            @RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
            @RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
            @RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
            @RequestHeader(Constant.HEADER_PLATFORM) String platform,
            @RequestHeader(Constant.HEADER_VERSION) String version,
            @RequestHeader(ComnConst.KEY_LANGUAGE) String language,
            @RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token, //jwt token
            @CustomerProfile AccessTokenJWTPayload customerProfile,
            HttpServletRequest httpRequest) throws Exception {

        HashMap<String, Object> tv = new HashMap<String, Object>();
        TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
        AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);

        String brand = accessTokenJWTPayload.getCustomerInfo().getProductBrand().toLowerCase();
        // do transaction
        tv.put("brand", brand);
        tv.put("lang", language!=null && !language.equals("") ? language : Constant.EN);
        TransactionResult transactionResult = customerProfileTransaction.getTierDetailTransaction(tv, httpRequest);

        ProfileDefaultResponse<List<ProfileTierDetailRsp>> data = new ProfileDefaultResponse(tv, transactionResult);
        GenericJsonResponse<List<ProfileTierDetailRsp>> getCustomerProfileRsp = new GenericJsonResponse<List<ProfileTierDetailRsp>>(tv, transactionResult,
                data.getData());
        log.info("End tier-detail : "+ gson.toJson(getCustomerProfileRsp));
        return ResponseEntity.status(transactionResult.getHttpStatus()).body(getCustomerProfileRsp);

    }

    @PostMapping(path = "/encryptForAPIGWDev", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getTestRewardProfile(
            @RequestBody EncryptModel jsonNode) throws Exception {

        log.info("json is "+jsonNode);

        String encrypt = jsonNode.getEncrypt();
        String decrypt = jsonNode.getDecrypt();
        String decryptFE = jsonNode.getDecryptFE();
        String encryptFE = jsonNode.getEncryptFE();
        String decryptRandom = jsonNode.getDecryptRandom();
        String env = jsonNode.getEnv();

        String result = "";
        try {
            String key = Objects.equals(env, "uat") ? "NEWappSq7CpRFh8jar3n5PgyuGLDmbQ2" : "rkZsg36XQz7SAHD4yap9CeK5vfFLqcYT" ;
            String keyFE = Objects.equals(env, "uat") ? "B374A26A71490437AA024E4FADD5B497" : "" ;
            String ivFE = Objects.equals(env, "uat") ? "7E892875A52C59A3" : "" ;
            String keyRandomIv = Objects.equals(env, "uat") ? "RTStQ76Y1bmTIiyZCor+NQPeJgWfIieu" : "PG6HthcJVA2hMjXUihuqCor+NgInI5I7" ;

            if(encrypt != null && encrypt != ""){
                result += ("encrypt: " + SecurityUtil.aes256CBCEncryptRandomIV(key, encrypt));
            }
            if(decrypt != null && decrypt != ""){
                result += ("\n decrypt: " + SecurityUtil.aes256CBCDecryptRandomIV(key, decrypt));
            }

            if(decryptFE != null && decryptFE != ""){
                result += ("\n decryptFE: " + SecurityUtil.aes256CBCDecrypt(keyFE,ivFE,decryptFE));
            }

            if(encryptFE != null && encryptFE != ""){
                result += ("\n encryptFE: " + SecurityUtil.aes256CBCEncrypt(keyFE,ivFE,encryptFE));
            }

            if(decryptRandom != null && decryptRandom != ""){
                result += ("\n decryptRandom: " + SecurityUtil.aes256CBCDecryptRandomIV(keyRandomIv,decryptRandom));
            }
        }catch (Exception e){
            result += "\n error: "+e.getMessage();
        }
        return result;
    }

}
