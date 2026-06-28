package th.co.truecorp.commonapi.reward.common.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdRedeemHistory;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSystemConfig;
import th.co.truecorp.commonapi.reward.cms.jpa.repository.RwdRedeemHistoryRepository;
import th.co.truecorp.commonapi.reward.cms.jpa.service.RwdSystemConfigService;
import th.co.truecorp.commonapi.reward.common.endpoint.CommonGetConvergenceListByProductIdEndpoint;
import th.co.truecorp.commonapi.reward.common.endpoint.CommonServiceEndpoint;
import th.co.truecorp.commonapi.reward.common.endpoint.CommonServiceProfileEndpoint;
import th.co.truecorp.commonapi.reward.common.model.GetConvergenceListResp;
import th.co.truecorp.commonapi.reward.common.model.GetDigitalByDigitalIdResponse;
import th.co.truecorp.commonapi.reward.common.model.IdAndType;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.model.CustomerProfileRsp;
import th.co.truecorp.commonapi.reward.model.ServiceProfileRsp;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemLoyaltyBurn.res.LoyaltyBurnResponse;
import th.co.truecorp.commonapi.reward.model.redis.Appconfig;
import th.co.truecorp.commonapi.reward.redis.TokenCacheService;
import th.co.truecorp.commonapi.reward.service.CommonRequestorService;
import th.co.truecorp.commonapi.reward.service.RetryService1;
import th.co.truecorp.commonlib.jpa.entity.CommonRequestor;
import th.co.truecorp.commonlib.log.exception.CustomerProfileException;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.model.AccessTokenJWTPayload;
import th.co.truecorp.commonlib.model.CustomerInfoResponse;
import th.co.truecorp.commonlib.util.ObjectUtil;
import th.co.truecorp.commonlib.util.SecurityUtil;

@Component
public class RewardUtill {

    private static final Logger log = LoggerFactory.getLogger(RewardUtill.class);

    @Value("${app.private.key.aes256cbc}")
    private String appPrivateKeyAES256CBC;
    @Value("${app.iv}")
    private String iv;
    @Value("${app.key}")
    private String key;
    @Value("${app.fe.aes256}")
    private String appFeAES256;

    @Autowired
    private RwdRedeemHistoryRepository rwdRedeemHistoryRepository;

    @Autowired
    private RetryService1 retryService;

    @Autowired
    private RwdSystemConfigService rwdSystemConfigService;

    @Autowired
    private CommonServiceProfileEndpoint commonServiceProfileEndpoint;
    
    private Appconfig appconfig = null;
    
    @Autowired
    private TokenCacheService tokenCacheService;
    
    @Autowired
    private CommonRequestorService commonRequestorService;

    @Autowired
    private CommonServiceEndpoint commonServiceEndpoint;

    @Autowired
    private CommonGetConvergenceListByProductIdEndpoint commonGetConvergenceListByProductIdEndpoint;
    
    private static final int DEF_EXPIRE_TOKEN = 3600; // 1 hour
    private static final int DEF_EXPIRE_REFRESH_TOKEN = 86400; // 1 day

    /*
        T=postpaid
        P=prepaid
    * */
    public String getShortProductTypeForDtac(String productType) {
        String shortType = null;
        if(productType.equals("PREPAID")){
            shortType =  "P";
        } else if (productType.equals("POSTPAID")) {
            shortType =  "T";
        }
        return shortType;
    }

    public IdAndType getThaiIdOrPhoneNoForTrue(Map<String, Object> tv)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        log.info("Start process getThaiIdOrPhoneNoForTrue");

        IdAndType idAndType = new IdAndType();
        AtomicBoolean isEncryptedId = new AtomicBoolean(false);
        String id = "", idNoEnc = "", idnType = "";

        String productSegment = ((String) tv.getOrDefault("productSegment", "")).toUpperCase();
        String brand = ((String) tv.getOrDefault("brand", "")).toUpperCase();
        String productId = (String) tv.getOrDefault("productId", "");
        String autoFlg = (String) tv.getOrDefault("autoFlg", "");
        String thaiId = (String) tv.getOrDefault("thaiId", "");
        @SuppressWarnings("unchecked")
        List<CustomerInfoResponse.CvgList> cvgList = (List<CustomerInfoResponse.CvgList>) tv.get("cvgList");

        boolean isCore = !thaiId.isEmpty();
        log.info("Extracted data: productSegment={}, brand={}, productId={}, thaiId={}, autoFlg={}, isCore={}",
                productSegment, brand, productId, thaiId, autoFlg, isCore);

        if ("Y".equalsIgnoreCase(autoFlg)) {
            log.info("Processing Earn auto Mode case");
            id = handleEarnAutoMode(tv, brand, productSegment, productId, isCore, isEncryptedId);
        } else {
            log.info("Processing Normal Mode case");
            id = handleNormalMode(tv, brand, productSegment, productId, isCore, isEncryptedId, cvgList);
        }

        idnType = isEncryptedId.get() ? Constant.TMH : Constant.THAIID;

        log.info("Final result: id={}, idnType={}", id, idnType);

        idAndType.setId(id);
        idAndType.setIdnType(idnType);
        return idAndType;
    }

    private String handleEarnAutoMode(Map<String, Object> tv, String brand, String productSegment, String productId, boolean isCore, AtomicBoolean isEncryptedId)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        if (Constant.TMH.equalsIgnoreCase(brand)) {
            log.info("Earn auto Mode with TMH brand, encrypting productId");
            isEncryptedId.set(true);
            return encryptProductId(productId, isCore);
        } else if (Constant.CVG.equalsIgnoreCase(productSegment)) {
            log.info("Earn auto Mode with CVG productSegment, processing Convergence List");
            Optional<String> result = processConvergenceList(tv, isCore, isEncryptedId);

            if (result.isEmpty()) {
                return null;
            }else{
                encryptThaiId(tv, isCore);
            }
        }else {
            return encryptThaiId(tv, isCore);
        }

        log.info("Earn auto Mode did not match any specific conditions, encrypting Thai ID");
        return null;
    }

    private String handleNormalMode(Map<String, Object> tv, String brand, String productSegment, String productId, boolean isCore, AtomicBoolean isEncryptedId, List<CustomerInfoResponse.CvgList> cvgList)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        log.info("Handling Normal Mode: brand={}, productSegment={}", brand, productSegment);

         if (Constant.TMH.equalsIgnoreCase(brand)) {
            log.info("Normal Mode with TMH brand, encrypting productId");
            isEncryptedId.set(true);
            return encryptProductId(productId, isCore);
        }else if (Constant.CVG.equalsIgnoreCase(productSegment)) {
             log.info("Processing Convergence List for Normal Mode");

             long tmhCount = cvgList.stream()
                     .filter(cvg -> Constant.TMH.equalsIgnoreCase(cvg.getProductBrand()))
                     .count();

             if (tmhCount > 1) {
                 log.info("Found multiple TMH products in Convergence List, encrypting Thai ID");
                 return encryptThaiId(tv, isCore);
             }

             return cvgList.stream()
                     .filter(cvg -> Constant.TMH.equalsIgnoreCase(cvg.getProductBrand()))
                     .map(cvg -> {
                         log.info("Found TMH product in Convergence List, encrypting productId: {}", cvg.getProductId());
                         isEncryptedId.set(true);
                         return encryptProductIdSafe(cvg.getProductId(), isCore);
                     })
                     .findFirst()
                     .orElseGet(() -> {
                         log.info("No valid TMH product found in Convergence List, encrypting Thai ID");
                         return encryptThaiId(tv, isCore);
                     });

        }

        log.info("Normal Mode did not match any specific conditions, encrypting Thai ID");
        return encryptThaiId(tv, isCore);
    }

    private Optional<String> processConvergenceList(Map<String, Object> tv, boolean isCore, AtomicBoolean isEncryptedId) {
        log.info("Processing Convergence List from profile service");

        EndpointResult endpointResult = commonGetConvergenceListByProductIdEndpoint.getCommonService(tv);
        if (endpointResult.getHttpStatus() == 200) {
            log.info("Successfully retrieved Convergence List data");
            GetConvergenceListResp convergenceListResp = (GetConvergenceListResp) tv.get("getConvergenceListByProductId");
            List<GetConvergenceListResp.DataObj> filteredList = convergenceListResp.getData().stream()
                    .filter(cvg -> Constant.TMH.equalsIgnoreCase(cvg.getProductBrand()))
                    .limit(2) // จำกัดจำนวนที่ต้องตรวจสอบ
                    .collect(Collectors.toList()); // เก็บเป็น List ชั่วคราว

            if (filteredList.size() > 1) {
                return Optional.empty(); // ถ้ามีมากกว่า 1 ตัว ให้คืนค่า Optional.empty()
            }

            return filteredList.stream().findFirst()
                    .map(cvg -> {
                        log.info("Found TMH product in Convergence List, encrypting productId: {}", cvg.getProductId());
                        isEncryptedId.set(true);
                        return encryptProductIdSafe(cvg.getProductId(), isCore);
                    });

        }else{
            log.warn("Failed to retrieve Convergence List data, HTTP Status: {}, error message: {}", endpointResult.getHttpStatus(), endpointResult.getEndpointErrorMessage());
        }
        return Optional.empty();
    }

    private String encryptThaiId(Map<String, Object> tv, boolean isCore) {
        try {
            log.info("Encrypting Thai ID, isCore={}", isCore);
            if (!isCore) {
                EndpointResult endpointResult = commonServiceProfileEndpoint.getCommonService(tv);
                if (endpointResult.getHttpStatus() == 200) {
                    log.info("Successfully retrieved customer identity from service profile");
                    GetDigitalByDigitalIdResponse.Profile serviceProfileRsp = (GetDigitalByDigitalIdResponse.Profile) tv.get("serviceProfileRspEndpoint");
                    return SecurityUtil.aes256CBCEncryptRandomIV(
                            appPrivateKeyAES256CBC,
                            SecurityUtil.aes256CBCDecrypt(key, iv, serviceProfileRsp.getCustomerIdentity())
                    );
                }else{
                    log.warn("Failed to retrieve service profile, HTTP Status: {}, error message: {}", endpointResult.getHttpStatus(),endpointResult.getEndpointErrorMessage());
                }
            } else {
                log.info("Using Thai ID from input data");
                return SecurityUtil.aes256CBCEncryptRandomIV(
                        appPrivateKeyAES256CBC,
                        SecurityUtil.aes256CBCDecrypt(key, iv, (String) tv.getOrDefault("thaiId", ""))
                );
            }
        } catch (Exception e) {
            log.info("Error encrypting Thai ID: {}", e.getMessage(), e);
        }
        return null;
    }

    private String encryptProductId(String productId, boolean isCore)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        log.info("Encrypting productId: {}, isCore={}", productId, isCore);
        String decryptedProductId = isCore ?
                SecurityUtil.aes256CBCDecrypt(key, iv, productId) :
                SecurityUtil.aes256CBCDecryptRandomIV(appFeAES256, productId);

        if (decryptedProductId.startsWith("66")) {
            log.info("Replacing country code 66 with 0 in decrypted productId");
            decryptedProductId = decryptedProductId.replaceFirst("66", "0");
        }

        return SecurityUtil.aes256CBCEncryptRandomIV(appPrivateKeyAES256CBC, decryptedProductId);
    }

    private String encryptProductIdSafe(String productId, boolean isCore) {
        try {
            return encryptProductId(productId, isCore);
        } catch (Exception e) {
            log.info("Error encrypting product ID: {}", e.getMessage(), e);
            return "";
        }
    }


    public void saveRWDHistory(Map<String, Object> tv, String endpoint) throws InterruptedException {

        RwdRedeemHistory rwdRedeemHistory = new RwdRedeemHistory();
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));

        rwdRedeemHistory.setBrandCode(Objects.equals(tv.get("brand").toString(), Constant.DTAC) ? Constant.DTAC.toUpperCase() : Constant.TRUE.toUpperCase());
        rwdRedeemHistory.setDigitalId(tv.get("digitalId").toString());
        rwdRedeemHistory.setActionDate(timestamp);
        rwdRedeemHistory.setCampaignId(tv.getOrDefault("cmpgId", "") != null ? tv.get("cmpgId").toString() : "");
        rwdRedeemHistory.setCampaignCode(tv.getOrDefault("cmpgCode", "") != null ? tv.get("cmpgCode").toString() : "");
        rwdRedeemHistory.setCampaignType(tv.getOrDefault("cmpgType", "") != null ? tv.get("cmpgType").toString() : "");
        rwdRedeemHistory.setCouponCode(null);
        rwdRedeemHistory.setCouponExpireDate(null);
        rwdRedeemHistory.setPackageName(null);
        rwdRedeemHistory.setDescription(null);
        rwdRedeemHistory.setSource(tv.get("source")!=null ? tv.get("source").toString():null);

        if (Objects.equals(endpoint, Constant.ENDPOINT_SERVICE_GET_LOYALTYBURN)) {
            LoyaltyBurnResponse responseLoyaltyBurn = (LoyaltyBurnResponse) tv.get(Constant.ENDPOINT_SERVICE_GET_LOYALTYBURN);
            System.out.println("responseLoyaltyBurn is "+responseLoyaltyBurn);
            if (responseLoyaltyBurn.getProductNumber() != null) {
                rwdRedeemHistory.setPackageName(responseLoyaltyBurn.getProductNumber());
                rwdRedeemHistory.setDescription(responseLoyaltyBurn.getDescription());
                rwdRedeemHistory.setCouponCode(responseLoyaltyBurn.getProductNumber());
                rwdRedeemHistory.setCouponExpireDate(timestamp);
                rwdRedeemHistory.setRedeemStatus(Constant.SUCCESS);
                rwdRedeemHistory.setRedeemMessage(new Gson().toJson(responseLoyaltyBurn));
            }else{
                rwdRedeemHistory.setRedeemStatus(Constant.FAIL);
                rwdRedeemHistory.setRedeemMessage(new Gson().toJson(responseLoyaltyBurn));
            }
        }

        try{
             rwdRedeemHistoryRepository.save(rwdRedeemHistory);
        }catch (Exception exception){
            retryService.retrySaveData(rwdRedeemHistory);
        }
    }

//ใหม่
    public String handleLanguage(String langFront){
        log.info("start process handle language with and langFront is {}",langFront);
        String lang = Constant.EN;
        try{
            Optional<RwdSystemConfig> rwdSystemConfig = rwdSystemConfigService.findConfigs(Constant.DEFAULT_LANG,Constant.FIX);
            RwdSystemConfig systemConfig = rwdSystemConfig.get();
            log.info("system config is {}",new Gson().toJson(systemConfig));
            String value = systemConfig.getValue();
            if(Objects.equals(value, Constant.FRONT)){
                lang = langFront!=null && !langFront.equals("") ? langFront : Constant.EN;
            }else {
                if(value != null && !value.equals("")){
                    lang = value;
                }
            }
        }catch (Exception e){
            log.info("Error is get rwdSystemConfig : {} | {} , {}",Constant.DEFAULT_LANG,Constant.FIX,e.getMessage());
            if(langFront!=null && !langFront.equals("")){
                lang = langFront;
            }
        }

        log.info("end process handle language with response is {}",lang);
        return lang;
    }
    
    public String beDecrypt(String encrypted) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
    	return SecurityUtil.aes256CBCDecrypt(key, iv, encrypted);
    }
    
    public String beEncrypt(String plainText) throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
    	return SecurityUtil.aes256CBCEncrypt(key, iv, plainText);
    }
    
	public String encryptApiGw(String value) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		return SecurityUtil.aes256CBCEncryptRandomIV(appPrivateKeyAES256CBC, value);
	}

	public String decryptApiGw(String encrypted) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		return SecurityUtil.aes256CBCDecryptRandomIV(appPrivateKeyAES256CBC, encrypted);
	}
	
	public String encryptForFE(String cientId, String value) throws InvalidAlgorithmParameterException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Appconfig appconfig = getAppConfig(cientId);
        String secretKey = appconfig.getStaticPassphrase();
        return SecurityUtil.aes256CBCEncryptRandomIV(secretKey, value);
    }

    public String decryptForFE(String cientId, String encrypted) throws InvalidAlgorithmParameterException, UnsupportedEncodingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Appconfig appconfig = getAppConfig(cientId);
        String secretKey = appconfig.getStaticPassphrase();
        return SecurityUtil.aes256CBCDecryptRandomIV(secretKey, encrypted);
    }
    
    private Appconfig getAppConfig(String clientId) {
        if (Objects.isNull(appconfig)) {
            String key = "appConfig:" + clientId;
            appconfig = tokenCacheService.get(key, Appconfig.class);
            if (Objects.isNull(appconfig)) {
                saveAppConfig(
                        commonRequestorService.findRequestorByClientId(clientId));
                appconfig = tokenCacheService.get(key, Appconfig.class);

            }
        }
        return appconfig;
    }
    
    public void saveAppConfig(CommonRequestor commonRequestor) {
        Integer reqExprTokenSec = DEF_EXPIRE_TOKEN;
        try {
            reqExprTokenSec = commonRequestor.getReqExprTokenSec();
        } catch (Exception e) {
            // ignore
        }
        Integer reqExprRefreshSec = DEF_EXPIRE_REFRESH_TOKEN;
        try {
            reqExprRefreshSec = commonRequestor.getReqExprRefreshSec();
        } catch (Exception e) {
            // ignore
        }
        Appconfig appconfig = new Appconfig();
        appconfig.setStaticPassphrase(commonRequestor.getStaticPassphrase());
        appconfig.setReqExprTokenSec(reqExprTokenSec);
        appconfig.setReqExprRefreshSec(reqExprRefreshSec);
        // save redis
        String key = "appConfig:" + commonRequestor.getReqUsername();
        tokenCacheService.putExpireToken(key, appconfig, reqExprRefreshSec.longValue());
    }
    
    public static AccessTokenJWTPayload getAccessTokenFromXCustomerProfile(String auth, String xCustomerProfile) {
        if (StringUtils.isNotBlank(auth) && null != xCustomerProfile) {
            try {
                return ObjectUtil.objectMapper().readValue(xCustomerProfile, AccessTokenJWTPayload.class);
            } catch (Exception e) {
                throw new CustomerProfileException("cannot parse to AccessTokenJWTPayload: " + e.getMessage());
            }
        }
        return null;
    }

    public String mapUserLevel(Map<String, Object> tv) {
        String userLevel = "";
        int typeCardValue = 0;
        int productTypeValue = 0;

        log.info("Starting mapUserLevel with input: {}", tv);

        try {
            commonServiceEndpoint.getCommonService(tv);

            CustomerProfileRsp commonProfileRsp = (tv.get("commonProfileRspEndpoint") instanceof CustomerProfileRsp)
                    ? (CustomerProfileRsp) tv.get("commonProfileRspEndpoint")
                    : null;

            if (commonProfileRsp != null) {
                log.info("Retrieved CustomerProfileRsp: {}", commonProfileRsp);

                Optional<RwdSystemConfig> rwdSystemConfig = rwdSystemConfigService.findConfigs(Constant.LEVEL_SEGMENT, commonProfileRsp.getTypeCard());

                if (rwdSystemConfig.isPresent()) {
                    typeCardValue = parseConfigValue(rwdSystemConfig.get(), "typeCardValue");
                } else {
                    log.warn("No configuration found for typeCard: {}", commonProfileRsp.getTypeCard());
                }

                if (tv.containsKey("productType")) {
                    rwdSystemConfig = rwdSystemConfigService.findConfigs(Constant.LEVEL_SEGMENT, tv.get("productType").toString());

                    if (rwdSystemConfig.isPresent()) {
                        productTypeValue = parseConfigValue(rwdSystemConfig.get(), "productTypeValue");
                    } else {
                        log.warn("No configuration found for productType: {}", tv.get("productType"));
                    }
                } else {
                    log.warn("Missing productType in input map");
                }

                userLevel = String.valueOf(typeCardValue + productTypeValue);
            } else {
                log.warn("CustomerProfileRsp is null");
            }
        } catch (Exception e) {
            log.info("Error while mapping user level", e);
        }

        log.info("Final userLevel: {}", userLevel);
        return userLevel;
    }

    private int parseConfigValue(RwdSystemConfig config, String key) {
        try {
            return Integer.parseInt(config.getValue());
        } catch (NumberFormatException e) {
            log.info("Invalid numeric value for {}: {}", key, config.getValue(), e);
            return 0;
        }
    }

    public List<String> levelCardType(String userLevel){

        List<String> cardType = new ArrayList<>();

        Map<String, List<String>> userLevelMap = new HashMap<>();
        userLevelMap.put("15", List.of("welcome", "silver", "gold", "platinum_blue"));
        userLevelMap.put("14", List.of("silver", "gold", "platinum_blue"));
        userLevelMap.put("12", List.of("gold", "platinum_blue"));
        userLevelMap.put("8", List.of("platinum_blue"));
        userLevelMap.put("47", List.of("welcome", "silver", "gold", "platinum_blue"));
        userLevelMap.put("46", List.of("silver", "gold", "platinum_blue"));
        userLevelMap.put("44", List.of("gold", "platinum_blue"));
        userLevelMap.put("40", List.of("platinum_blue"));
        userLevelMap.put("79", List.of("welcome", "silver", "gold", "platinum_blue"));
        userLevelMap.put("78", List.of("silver", "gold", "platinum_blue"));
        userLevelMap.put("76", List.of("gold", "platinum_blue"));
        userLevelMap.put("72", List.of("platinum_blue"));
        userLevelMap.put("111", List.of("welcome", "silver", "gold", "platinum_blue"));
        userLevelMap.put("110", List.of("silver", "gold", "platinum_blue"));
        userLevelMap.put("108", List.of("gold", "platinum_blue"));
        userLevelMap.put("104", List.of("platinum_blue"));

        cardType = userLevelMap.getOrDefault(userLevel, List.of("no card"));

        return cardType;
    }
    
    public String mapError(String description, String message, String error, String timeStamp){

        return (description != null && !description.isEmpty()) ? description :
                (message != null && !message.isEmpty()) ? message :
                        (error != null && !error.isEmpty()) ? error :
                                Constant.NO_ERROR_MESSAGE_FROM_API_GW +
                                        Optional.ofNullable(timeStamp).orElse("");
    }

    public String  parseStringNULL(String key) {
        if(key != null && !"null".equals(key)){
            return key;
        }else{
            return null;
        }
    }
}