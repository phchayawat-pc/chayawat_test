package th.co.truecorp.commonapi.reward.cms.jpa.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.dto.RwdErrMappingDTO;
import th.co.truecorp.commonapi.reward.cms.jpa.repository.CustomQueryRepository;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.model.EndpointResultRWD;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.model.EndpointResult;

import java.util.*;

@Service
public class ErrorService {

    @Autowired
    private CustomQueryRepository customQueryRepository;

    @Autowired
    private CacheService cacheService;

    @Value("${app.zone}")
    private String environment;

    @Autowired
    private ResultService resultService;

    @Autowired
    private RewardUtill rewardUtill;

    @Autowired
    private APIGWUtill apigwUtill;

    private static Logger log = LoggerFactory.getLogger(ErrorService.class);

    public Optional<RwdErrMappingDTO> findByParams(String action, String brand, String errCode, String lang, String businessErr, String displayType) {
        String cacheKey = "RWD-ERR:" + environment;
        ObjectMapper objectMapper = new ObjectMapper();

        // Fetch from cache and deserialize
        List<RwdErrMappingDTO> cachedDataList = null;

        // Attempt to cast and deserialize cache data
        Object cachedData = cacheService.getCache(cacheKey);

        if (cachedData != null && cachedData instanceof String) {
            try {
                // Deserialize the cached JSON string into a List<RwdErrMappingDTO>
                cachedDataList = objectMapper.readValue((String) cachedData, new TypeReference<List<RwdErrMappingDTO>>() {});
                log.info("Data fetched from cache.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Cache is empty, fetch from DB
        if (cachedDataList == null || cachedDataList.isEmpty()) {
            log.info("Cache is empty, fetching from DB...");
            List<RwdErrMappingDTO> allData = customQueryRepository.findAllMappings();

            if (allData.isEmpty()) {
                // No data in DB, return default DTO with errCode = 99999
                RwdErrMappingDTO defaultDto = new RwdErrMappingDTO(
                        action,
                        Constant.ERR_CODE_UNEXPECTED,
                        null,
                        null,
                        null,
                        brand,
                        lang,
                        null,
                        null,
                        displayType
                );
                return Optional.of(defaultDto);
            } else {
                // Serialize and cache data
                try {
                    String allDataJson = objectMapper.writeValueAsString(allData);
                    cacheService.setCache(cacheKey, allDataJson, 1296000); // Cache for 15 days
                    cachedDataList = allData;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Filter the results
        Optional<RwdErrMappingDTO> result = Optional.empty();
        try{
            List<RwdErrMappingDTO> finalCachedDataList = cachedDataList;
            result = cachedDataList.stream()
                    .filter(data -> action.equals(data.getAction()) &&
                            brand.equals(data.getBrandCode()) &&
                            errCode.equals(data.getErrCode()) &&
                            lang.equals(data.getLang()) &&
                            businessErr != null && businessErr.equals(data.getSystemErrorCode()) &&
                            displayType.equals(data.getDisplayType()))
                    .findFirst()
                    .or(() -> finalCachedDataList.stream()
                            .filter(data -> action.equals(data.getAction()) &&
                                    brand.equals(data.getBrandCode()) &&
                                    errCode.equals(data.getErrCode()) &&
                                    "EN".equals(data.getLang()) &&
                                    businessErr != null && businessErr.equals(data.getSystemErrorCode()) &&
                                    displayType.equals(data.getDisplayType()))
                            .findFirst());

            // If not found in cache, search in DB
            if (!result.isPresent()) {
                log.info("Data not found in cache, searching in DB...");
                try {
                    result = customQueryRepository.findFirstByParams(action, brand, errCode, lang, businessErr, displayType);
                }catch (Exception e){
                    e.printStackTrace();
                }

                if (result.isPresent()) {
                    this.clearAndUpdateCache(cacheKey);
                } else {
                    // Set errCode = 99999 if not found in both cache and DB
                    log.info("Data not found in DB, setting errCode = 99999 for technical error or 88888 for business error");
                    RwdErrMappingDTO defaultDto = new RwdErrMappingDTO(
                            action,
                            Objects.equals(errCode.split("\\.")[0].substring(0,1), "4") ? String.valueOf(Constant.ERR_CODE_BUSS_UNEXPECTED) : Constant.ERR_CODE_UNEXPECTED,
                            Objects.equals(errCode.split("\\.")[0].substring(0,1), "4") ? String.valueOf(Constant.ERR_CODE_BUSS_UNEXPECTED) : Constant.ERR_CODE_UNEXPECTED,
                            null,
                            Objects.equals(errCode.split("\\.")[0].substring(0,1), "4") ? String.valueOf(Constant.BUSS_HTTP_STTS) : String.valueOf(Constant.DEFT_HTTP_STTS),
                            brand,
                            lang,
                            null,
                            null,
                            displayType
                    );
//                    System.out.println("defaultDto is "+Optional.of(defaultDto));
                    return Optional.of(defaultDto);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    public EndpointResultRWD mapErrorCode(String action, String brand, String errCode, String lang, String errorDesc, String businessErr, String displayType){
        brand = apigwUtill.isDtac(brand.toLowerCase()) ? brand.toUpperCase() : Constant.TRUE.toUpperCase();

        log.info("start map error apigw with action is {} and brand is {} and errCode is '{}' and lang is {} and errorDesc is {} and businessErr is {} and displayType is {}",action, brand, errCode, lang, errorDesc, businessErr,displayType);
        EndpointResultRWD endpointResult = new EndpointResultRWD();
        Optional<RwdErrMappingDTO> result = this.findByParams(action, brand, errCode, rewardUtill.handleLanguage(lang),businessErr,displayType);
        RwdErrMappingDTO rwdErrMappingDTO = result.get();

        String statusType = rwdErrMappingDTO.getHttpCode().startsWith("2") ? "S" :
                rwdErrMappingDTO.getHttpCode().startsWith("4") ? "B" : "T";
        String statusCode = rwdErrMappingDTO.getCustomCode() != null ? statusType+"-RWD-"+rwdErrMappingDTO.getCustomCode() : statusType+"-RWD-"+Constant.ERR_CODE_UNEXPECTED;
        String desc = !Objects.equals(rwdErrMappingDTO.getCustomCode(), Constant.ERR_CODE_UNEXPECTED) &&
                !Objects.equals(rwdErrMappingDTO.getCustomCode(), Constant.ERR_CODE_BUSS_UNEXPECTED) ? rwdErrMappingDTO.getDescription() : errorDesc;
        String message = Objects.equals(rwdErrMappingDTO.getCustomCode(), Constant.ERR_CODE_UNEXPECTED) ? errorDesc :
                Objects.equals(rwdErrMappingDTO.getCustomCode(), Constant.ERR_CODE_BUSS_UNEXPECTED) ? errorDesc : rwdErrMappingDTO.getMessage();
        String responseCode = rwdErrMappingDTO.getCustomCode();

        endpointResult.setEndpointErrorCode(statusCode);
        endpointResult.setEndpointStatusType(statusType);
        endpointResult.setEndpointErrorMessage(message);
        endpointResult.setEndpointErrorDescription(desc);
        endpointResult.setHttpStatus(Integer.parseInt(rwdErrMappingDTO.getHttpCode()));
        endpointResult.setEndpointStatusCode(statusCode);
        endpointResult.setEndpointResponseCode(responseCode);

        log.info("result error is {}",new Gson().toJson(endpointResult));
        return endpointResult;
    }

    public EndpointResult mapErrorException(Exception exception, Map<String, Object> tv){
        log.info("start map error exception with exception is {} ",exception.getMessage());

        EndpointResult endpointResult = resultService.getEndpointExceptionResult(tv,exception);
        log.info("result from lib framework is {}",new Gson().toJson(endpointResult));
        String desc = "", message = "";
        if(endpointResult.getEndpointResponseCode()==null || endpointResult.getEndpointResponseCode().contains(Constant.ERR_CODE_UNEXPECTED)) {
            desc = exception.getMessage();
            message = exception.getMessage();
        }else {
            desc = endpointResult.getEndpointErrorDescription()==null && endpointResult.getEndpointErrorDescription().isEmpty() ? null : endpointResult.getEndpointErrorDescription();
            message = endpointResult.getEndpointErrorMessage();
        }
        endpointResult.setEndpointErrorMessage(message);
        endpointResult.setEndpointErrorDescription(desc);
        log.info("end map error exception");
        return endpointResult;
    }

    public void clearAndUpdateCache(String cacheKey){
        // Clear and update cache if found in DB
        log.info("Data found in DB, updating cache...");
        ObjectMapper objectMapper = new ObjectMapper();
        boolean isDeleted = cacheService.evictCache(cacheKey);
        List<RwdErrMappingDTO> allData = customQueryRepository.findAllMappings();
        try {
            String allDataJson = objectMapper.writeValueAsString(allData);
            cacheService.setCache(cacheKey, allDataJson, 3600); // Cache for 1 hour
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public EndpointResultRWD convertMapResult(EndpointResult endpointResult) {
        String json = new Gson().toJson(endpointResult);
        return new Gson().fromJson(json, EndpointResultRWD.class);
    }

    public EndpointResult revertMapResult(EndpointResultRWD endpointResultRwd) {
        String json = new Gson().toJson(endpointResultRwd);
        return new Gson().fromJson(json, EndpointResult.class);
    }
}
