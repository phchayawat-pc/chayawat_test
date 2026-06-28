package th.co.truecorp.commonapi.reward.controller;

import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import th.co.truecorp.commonapi.reward.common.model.EmptyJsonResponse;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonapi.reward.model.jsonResponse.ShelfDefaultResponse;
import th.co.truecorp.commonapi.reward.model.jsonResponse.ShelfGetLayoutPageJsonResponse;
import th.co.truecorp.commonapi.reward.model.redeem.ShelfMajorRsp;
import th.co.truecorp.commonapi.reward.model.jsonResponse.ShelfPageDefaultResponse;
import th.co.truecorp.commonapi.reward.transaction.ShelfTransaction;
import th.co.truecorp.commonlib.annotations.CustomerProfile;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.log.model.TransactionResult;
import th.co.truecorp.commonlib.model.AccessTokenJWTPayload;
import th.co.truecorp.commonlib.service.NetworkService;
import th.co.truecorp.commonlib.util.TransactionValueUtil;
import th.co.truecorp.commonlib.ws.model.GenericJsonResponse;

import java.util.*;

@Validated
@RestController
@RequestMapping(value = "/v1/shelf")
public class ShelfController {

    private static Logger log = LoggerFactory.getLogger(ShelfController.class);

    @Autowired
    ShelfTransaction shelfTransaction;

    @Autowired
    private NetworkService networkService;

    String host;

    Gson gson = new Gson();

    @PostConstruct
    public void getHost() {
        host = networkService.getHostName();
    }

    @GetMapping(path = "/getlayout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericJsonResponse<ShelfLoyoutRsp>> getLayout(
            @RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
            @RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
            @RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
            @RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
            @RequestHeader(Constant.HEADER_PLATFORM) String platform,
            @RequestHeader(Constant.HEADER_VERSION) String version,
            @RequestHeader(ComnConst.KEY_LANGUAGE) String language,
            @CustomerProfile AccessTokenJWTPayload customerProfile,
            @RequestParam(value="brandCode") String brandCode,
            @ModelAttribute(value="productType") String productType,
            @ModelAttribute(value="chargeType") String chargeType,
            HttpServletRequest httpRequest
    ) throws Exception {
        HashMap<String, Object> tv = new HashMap<String, Object>();
        TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);

        String brand = brandCode.toUpperCase();

        tv.put(ComnConst.KEY_LANGUAGE, language!=null && !language.equals("") ? language : Constant.EN);
        tv.put("brand", brand);
        tv.put("productType", productType);
        tv.put("chargeType", chargeType);

        TransactionResult transactionResult = shelfTransaction.getlayouts(tv, httpRequest);

        ShelfDefaultResponse<ShelfLoyoutRsp> data = new ShelfDefaultResponse(tv, transactionResult);
        GenericJsonResponse<ShelfLoyoutRsp> getLayoutRspGenericJsonResponse = new GenericJsonResponse<>(tv, transactionResult,
                data.getData());
        log.info("End Get Layout : "+gson.toJson(getLayoutRspGenericJsonResponse));
        return ResponseEntity.status(transactionResult.getHttpStatus()).body(getLayoutRspGenericJsonResponse);
    }

    @GetMapping(path = "/getSectionHeader", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericJsonResponse<ShelfSectionHeaderRsp>> getSectionHeader(
            @RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
            @RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
            @RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
            @RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
            @RequestHeader(Constant.HEADER_PLATFORM) String platform,
            @RequestHeader(Constant.HEADER_VERSION) String version,
            @RequestHeader(ComnConst.KEY_LANGUAGE) String language,
            @CustomerProfile AccessTokenJWTPayload customerProfile,
            @ModelAttribute(value="layoutId") String layoutId,
            @ModelAttribute(value="sectionId") String sectionId,
            @ModelAttribute(value="displayTypeCode") String displayTypeCode,
            @ModelAttribute(value="useCmsContent") String useCmsContent,
            HttpServletRequest httpRequest
    ) throws Exception {
        HashMap<String, Object> tv = new HashMap<String, Object>();
        TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
        AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);

        language = language!=null && !language.equals("") ? language : Constant.EN;
        tv.put(ComnConst.KEY_LANGUAGE, language!=null && !language.equals("") ? language : Constant.EN);
        String brand = accessTokenJWTPayload.getCustomerInfo().getProductBrand().toLowerCase();
        tv.put("productId", accessTokenJWTPayload.getCustomerInfo().getProductId());
        tv.put("productType", accessTokenJWTPayload.getCustomerInfo().getProductType());
        tv.put("digitalId", accessTokenJWTPayload.getCustomerInfo().getDigitalId());
        tv.put("brand", accessTokenJWTPayload.getCustomerInfo().getProductBrand().toLowerCase());
        tv.put("productBrand", accessTokenJWTPayload.getCustomerInfo().getProductBrand());


        TransactionResult transactionResult = shelfTransaction.getSectionHeader(tv, httpRequest, language, brand, layoutId, sectionId, displayTypeCode, useCmsContent);

        ShelfDefaultResponse<ShelfSectionHeaderRsp> data = new ShelfDefaultResponse(tv, transactionResult);
        GenericJsonResponse<ShelfSectionHeaderRsp> getSectionHeaderRspGenericJsonResponse = new GenericJsonResponse<>(tv, transactionResult,
                data.getData());
        log.info("End Get SectionHeader : "+gson.toJson(getSectionHeaderRspGenericJsonResponse));
        return ResponseEntity.status(transactionResult.getHttpStatus()).body(getSectionHeaderRspGenericJsonResponse);
    }

    @GetMapping(path = "/getThematicHeader", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericJsonResponse<ShelfThematicHeaderRsp>> getThematicHeader(
            @RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
            @RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
            @RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
            @RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
            @RequestHeader(Constant.HEADER_PLATFORM) String platform,
            @RequestHeader(Constant.HEADER_VERSION) String version,
            @RequestHeader(ComnConst.KEY_LANGUAGE) String language,
            @CustomerProfile AccessTokenJWTPayload customerProfile,
            @RequestParam(value="campaignId") String campaignId,
            @RequestParam(value="templateCode") String templateCode,
            @RequestParam(value="sectionId" , required = false) String sectionId,
            HttpServletRequest httpRequest
    ) throws Exception {
        log.info("start  process get thematic header with campaignId is {} ,templateCode is {} ,sectionId is{}",campaignId,templateCode,sectionId);
        HashMap<String, Object> tv = new HashMap<String, Object>();
        TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
        tv.put("campaignId", campaignId);
        tv.put("templateCode",templateCode);
        tv.put("sectionId",sectionId);
        AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);
        tv.put("brand", accessTokenJWTPayload.getCustomerInfo().getProductBrand());
        tv.put(ComnConst.KEY_LANGUAGE, language!=null && !language.equals("") ? language : Constant.EN);
        TransactionResult transactionResult = shelfTransaction.getThematicHeader(tv, httpRequest);

        ShelfDefaultResponse<ShelfThematicHeaderRsp> data = new ShelfDefaultResponse<>(tv, transactionResult);

        GenericJsonResponse<ShelfThematicHeaderRsp> getLayoutRspGenericJsonResponse = new GenericJsonResponse<>(tv, transactionResult,
                data.getData());
        log.info("end process get thematic header");
        return ResponseEntity.status(transactionResult.getHttpStatus()).body(getLayoutRspGenericJsonResponse);
    }

    @GetMapping(path = "/getThematicFilter", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericJsonResponse<ShelfThematicFilterRsp>> getThematicFilter(
            @RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
            @RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
            @RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
            @RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
            @RequestHeader(Constant.HEADER_PLATFORM) String platform,
            @RequestHeader(Constant.HEADER_VERSION) String version,
            @RequestHeader(ComnConst.KEY_LANGUAGE) String language,
            @CustomerProfile AccessTokenJWTPayload customerProfile,
            @RequestParam(value="campaignId") String campaignId,
            @RequestParam(value="templateCode") String templateCode,
            @RequestParam(value="sectionId" , required = false) String sectionId,
            HttpServletRequest httpRequest
    ) throws Exception {
        log.info("start process get thematic filter with campaignId is {} ,templateCode is {} ,sectionId is{}",campaignId,templateCode,sectionId);
        HashMap<String, Object> tv = new HashMap<String, Object>();
        TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
        tv.put("campaignId", campaignId);
        tv.put("templateCode",templateCode);
        tv.put("sectionId",sectionId);
        AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);
        tv.put("brand", accessTokenJWTPayload.getCustomerInfo().getProductBrand());
        tv.put(ComnConst.KEY_LANGUAGE, language!=null && !language.equals("") ? language : Constant.EN);
        TransactionResult transactionResult = shelfTransaction.getThematicFilter(tv, httpRequest);

        ShelfDefaultResponse<ShelfThematicFilterRsp> data = new ShelfDefaultResponse<>(tv, transactionResult);

        GenericJsonResponse<ShelfThematicFilterRsp> getLayoutRspGenericJsonResponse = new GenericJsonResponse<>(tv, transactionResult,
                data.getData());
        log.info("end process get thematic filter");
        return ResponseEntity.status(transactionResult.getHttpStatus()).body(getLayoutRspGenericJsonResponse);
    }

    @GetMapping(path = "/getThematicDealList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericJsonResponse<ShelfThematicDealListRsp>> getThematicDealList(
            @RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
            @RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
            @RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
            @RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
            @RequestHeader(Constant.HEADER_PLATFORM) String platform,
            @RequestHeader(Constant.HEADER_VERSION) String version,
            @RequestHeader(ComnConst.KEY_LANGUAGE) String language,
            @CustomerProfile AccessTokenJWTPayload customerProfile,
            @RequestParam(value="campaignId") String campaignId,
            @RequestParam(value="templateCode") String templateCode,
            @RequestParam(value="sectionId" , required = false) String sectionId,
            HttpServletRequest httpRequest
    ) throws Exception {
        log.info("start process get thematic deal list with campaignId is {} ,templateCode is {} ,sectionId is{}",campaignId,templateCode,sectionId);
        HashMap<String, Object> tv = new HashMap<String, Object>();
        TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
        tv.put("campaignId", campaignId);
        tv.put("templateCode",templateCode);
        tv.put("sectionId",sectionId);
        AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);
        tv.put("brand", accessTokenJWTPayload.getCustomerInfo().getProductBrand());
        tv.put(ComnConst.KEY_LANGUAGE, language!=null && !language.equals("") ? language : Constant.EN);
        TransactionResult transactionResult = shelfTransaction.getThematicDealList(tv, httpRequest);

        ShelfDefaultResponse<ShelfThematicDealListRsp> data = new ShelfDefaultResponse<>(tv, transactionResult);

        GenericJsonResponse<ShelfThematicDealListRsp> getLayoutRspGenericJsonResponse = new GenericJsonResponse<>(tv, transactionResult,
                data.getData());
        log.info("end process get thematic deal list");
        return ResponseEntity.status(transactionResult.getHttpStatus()).body(getLayoutRspGenericJsonResponse);
    }

    @GetMapping(path = "/getSectionNoneRelated", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericJsonResponse<ShelfSectionNoneRelatedRsp>> getSectionNoneRelated(
            @RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
            @RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
            @RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
            @RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
            @RequestHeader(Constant.HEADER_PLATFORM) String platform,
            @RequestHeader(Constant.HEADER_VERSION) String version,
            @RequestHeader(ComnConst.KEY_LANGUAGE) String language,
            @CustomerProfile AccessTokenJWTPayload customerProfile,
            @RequestParam(value="layoutId", required = false) String layoutId,
            @RequestParam(value="sectionId", required = false) String sectionId,
            @RequestParam(value="displayTypeCode", required = false) String displayTypeCode,
            @RequestParam(value="useCmsContent", required = false) String useCmsContent,
            HttpServletRequest httpRequest
    ) throws Exception {
        HashMap<String, Object> tv = new HashMap<String, Object>();
        TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
        AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);

        String brand = accessTokenJWTPayload.getCustomerInfo().getProductBrand().toUpperCase();

        tv.put("cvgList", accessTokenJWTPayload.getCustomerInfo().getCvgList());
        tv.put("productSegment", accessTokenJWTPayload.getCustomerInfo().getProductSegment());
        tv.put("brand",brand);
        tv.put("digitalId", accessTokenJWTPayload.getCustomerInfo().getDigitalId());
        tv.put("productId", accessTokenJWTPayload.getCustomerInfo().getProductId());
        tv.put("productType", accessTokenJWTPayload.getCustomerInfo().getProductType());
        tv.put(ComnConst.KEY_LANGUAGE, language!=null && !language.equals("") ? language : Constant.EN);
        tv.put("layoutId", layoutId);
        tv.put("sectionId", sectionId);
        tv.put("displayTypeCode", displayTypeCode);
        tv.put("useCmsContent", useCmsContent);

        TransactionResult transactionResult = shelfTransaction.getSectionNoneRelated(tv, httpRequest);

        ShelfDefaultResponse<ShelfSectionNoneRelatedRsp> data = new ShelfDefaultResponse(tv, transactionResult);
        GenericJsonResponse<ShelfSectionNoneRelatedRsp> getGenericJsonResponse = new GenericJsonResponse<>(tv, transactionResult,
                data.getData());
        log.info("End Get SectionNoneRelated : "+gson.toJson(getGenericJsonResponse));
        return ResponseEntity.status(transactionResult.getHttpStatus()).body(getGenericJsonResponse);
    }

    @GetMapping(path = "/gettemplatealldeallist", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericJsonResponse<TemplateAllRsp>> getTemplatealldeallist(
            @RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
            @RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
            @RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
            @RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
            @RequestHeader(Constant.HEADER_PLATFORM) String platform,
            @RequestHeader(Constant.HEADER_VERSION) String version,
            @RequestHeader(ComnConst.KEY_LANGUAGE) String language,
            @CustomerProfile AccessTokenJWTPayload customerProfile,
            @RequestParam(value="sectionId") String sectionId,
            @RequestParam(value="templateCode", required = false) String templateCode,
            @RequestParam(value="page") Integer page,
            @RequestParam(value="limit") Integer limit,
            HttpServletRequest httpRequest
    ) throws Exception {
        HashMap<String, Object> tv = new HashMap<String, Object>();
        TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
        AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);

        String brand = accessTokenJWTPayload.getCustomerInfo().getProductBrand().toUpperCase();

        tv.put("digitalId", accessTokenJWTPayload.getCustomerInfo().getDigitalId());
        tv.put("productId", accessTokenJWTPayload.getCustomerInfo().getProductId());
        tv.put(ComnConst.KEY_LANGUAGE, language!=null && !language.equals("") ? language : Constant.EN);
        tv.put("templateCode", templateCode);
        tv.put("sectionId", sectionId);
        tv.put("page", page);
        tv.put("limit", limit);
        tv.put("brand",brand);

        TransactionResult transactionResult = shelfTransaction.getTemplatealldeallist(tv, httpRequest, brand);

        ShelfPageDefaultResponse<TemplateAllRsp> data = new ShelfPageDefaultResponse(tv, transactionResult);
        ShelfGetLayoutPageJsonResponse<TemplateAllRsp> getGenericJsonResponse = new ShelfGetLayoutPageJsonResponse<>(tv, transactionResult,
                data.getData(), new EmptyJsonResponse(), data.getPaging());
        return ResponseEntity.status(transactionResult.getHttpStatus()).body(getGenericJsonResponse);

    }

    @GetMapping(path = "/getSectionDetail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericJsonResponse<ShelfSectionDetailRsp>> getSectionDetail(
            @RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
            @RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
            @RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
            @RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
            @RequestHeader(Constant.HEADER_PLATFORM) String platform,
            @RequestHeader(Constant.HEADER_VERSION) String version,
            @RequestHeader(ComnConst.KEY_LANGUAGE) String language,
            @CustomerProfile AccessTokenJWTPayload customerProfile,
            @RequestParam(value="layoutId", required = false) String layoutId,
            @RequestParam(value="sectionId", required = false) String sectionId,
            @RequestParam(value="displayTypeCode", required = false) String displayTypeCode,
            HttpServletRequest httpRequest
    ) throws Exception {
        HashMap<String, Object> tv = new HashMap<String, Object>();
        TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
        AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);

        String brand = accessTokenJWTPayload.getCustomerInfo().getProductBrand().toUpperCase();

        tv.put("brand", brand);
        tv.put("productBrand", accessTokenJWTPayload.getCustomerInfo().getProductBrand());
        tv.put("digitalId", accessTokenJWTPayload.getCustomerInfo().getDigitalId());
        tv.put("productId", accessTokenJWTPayload.getCustomerInfo().getProductId());
        tv.put("productType", accessTokenJWTPayload.getCustomerInfo().getProductType());
        tv.put(ComnConst.KEY_LANGUAGE, language!=null && !language.equals("") ? language : Constant.EN);
        tv.put("layoutId", layoutId);
        tv.put("sectionId", sectionId);
        tv.put("displayTypeCode", displayTypeCode);

        TransactionResult transactionResult = shelfTransaction.getSectionDetail(tv, httpRequest, brand);

        ShelfPageDefaultResponse<ShelfSectionDetailRsp> data = new ShelfPageDefaultResponse(tv, transactionResult);
        ShelfGetLayoutPageJsonResponse<ShelfSectionDetailRsp> getGenericJsonResponse = new ShelfGetLayoutPageJsonResponse<>(tv, transactionResult,
                data.getData(), new EmptyJsonResponse(), data.getPaging());
        log.info("End Get SectionDetail : "+gson.toJson(getGenericJsonResponse));
        return ResponseEntity.status(transactionResult.getHttpStatus()).body(getGenericJsonResponse);

    }

    @GetMapping(path = "/getSectionContentDetail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericJsonResponse<ShelfSectionContentDetailRsp>> getSectionContentDetail(
            @RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
            @RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
            @RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
            @RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
            @RequestHeader(Constant.HEADER_PLATFORM) String platform,
            @RequestHeader(Constant.HEADER_VERSION) String version,
            @RequestHeader(ComnConst.KEY_LANGUAGE) String language,
            @CustomerProfile AccessTokenJWTPayload customerProfile,
            @RequestParam(value="layoutId", required = false) String layoutId,
            @RequestParam(value="sectionId") String sectionId,
            @RequestParam(value="displayTypeCode", required = false) String displayTypeCode,
            @RequestParam(value="useCmsContent", required = false) String useCmsContent,
            HttpServletRequest httpRequest
    ) throws Exception {
        HashMap<String, Object> tv = new HashMap<String, Object>();
        TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
        tv.put(ComnConst.KEY_LANGUAGE, language!=null && !language.equals("") ? language : Constant.EN);
        tv.put("layoutId", layoutId);
        tv.put("sectionId", sectionId);
        tv.put("displayTypeCode", displayTypeCode);
        tv.put("useCmsContent", useCmsContent);

        TransactionResult transactionResult = shelfTransaction.getSectionContentDetail(tv, httpRequest);

        ShelfDefaultResponse<ShelfSectionContentDetailRsp> data = new ShelfDefaultResponse(tv, transactionResult);
        GenericJsonResponse<ShelfSectionContentDetailRsp> getGenericJsonResponse = new GenericJsonResponse<>(tv, transactionResult,
                data.getData());
        log.info("End Get SectionContentDetail : "+gson.toJson(getGenericJsonResponse));
        return ResponseEntity.status(transactionResult.getHttpStatus()).body(getGenericJsonResponse);

    }

    @GetMapping(path = "/gettemplatemerchantdeallist", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericJsonResponse<ShelfTemplateMerchantDealRsp>> getTemplateMerchantDealList(
            @RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
            @RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
            @RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
            @RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
            @RequestHeader(Constant.HEADER_PLATFORM) String platform,
            @RequestHeader(Constant.HEADER_VERSION) String version,
            @RequestHeader(ComnConst.KEY_LANGUAGE) String language,
            @CustomerProfile AccessTokenJWTPayload customerProfile,
            @RequestParam(value="merchantId") String merchantId,
            @RequestParam(value="templateCode", required = false) String templateCode,
            HttpServletRequest httpRequest
    ) throws Exception {
        HashMap<String, Object> tv = new HashMap<String, Object>();
        TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
        AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);

        String brand = accessTokenJWTPayload.getCustomerInfo().getProductBrand().toUpperCase();

        tv.put("brand", brand);
        tv.put("digitalId", accessTokenJWTPayload.getCustomerInfo().getDigitalId());
        tv.put("productId", accessTokenJWTPayload.getCustomerInfo().getProductId());
        tv.put(ComnConst.KEY_LANGUAGE, language!=null && !language.equals("") ? language : Constant.EN);
        tv.put("merchantId", merchantId);
        tv.put("templateCode", templateCode);

        TransactionResult transactionResult = shelfTransaction.getTemplateMerchantDealList(tv, httpRequest, brand);

        ShelfDefaultResponse<ShelfTemplateMerchantDealRsp> data = new ShelfDefaultResponse(tv, transactionResult);
        GenericJsonResponse<ShelfTemplateMerchantDealRsp> getGenericJsonResponse = new GenericJsonResponse<>(tv, transactionResult,
                data.getData());
        return ResponseEntity.status(transactionResult.getHttpStatus()).body(getGenericJsonResponse);

    }

    @GetMapping(path = "/getsectionalldata", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericJsonResponse<ShelfSectionAllDataRsp>> getSectionAllData(
            @RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
            @RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
            @RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
            @RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
            @RequestHeader(Constant.HEADER_PLATFORM) String platform,
            @RequestHeader(Constant.HEADER_VERSION) String version,
            @RequestHeader(ComnConst.KEY_LANGUAGE) String language,
            @CustomerProfile AccessTokenJWTPayload customerProfile,
            @RequestParam(value="layoutId", required = false) String layoutId,
            @RequestParam(value="sectionId", required = false) String sectionId,
            @RequestParam(value="displayTypeCode", required = false) String displayTypeCode,
            @RequestParam(value="useCmsContent", required = false) String useCmsContent,
            HttpServletRequest httpRequest
    ) throws Exception {
        HashMap<String, Object> tv = new HashMap<String, Object>();
        TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
        AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);

        String brand = accessTokenJWTPayload.getCustomerInfo().getProductBrand().toUpperCase();

        tv.put("brand", brand);
        tv.put("digitalId", accessTokenJWTPayload.getCustomerInfo().getDigitalId());
        tv.put("productId", accessTokenJWTPayload.getCustomerInfo().getProductId());
        tv.put(ComnConst.KEY_LANGUAGE, language!=null && !language.equals("") ? language : Constant.EN);
        tv.put("layoutId", layoutId);
        tv.put("sectionId", sectionId);
        tv.put("displayTypeCode", displayTypeCode);
        tv.put("useCmsContent", useCmsContent);

        TransactionResult transactionResult = shelfTransaction.getSectionAllData(tv, httpRequest, brand);

        ShelfDefaultResponse<ShelfSectionAllDataRsp> data = new ShelfDefaultResponse(tv, transactionResult);
        GenericJsonResponse<ShelfSectionAllDataRsp> getGenericJsonResponse = new GenericJsonResponse<>(tv, transactionResult,
                data.getData());
        return ResponseEntity.status(transactionResult.getHttpStatus()).body(getGenericJsonResponse);

    }

    @GetMapping(path = "/gettemplatedetail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericJsonResponse<ShelfTemplateDetailRsp>> getTemplateDetailList(
            @RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
            @RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
            @RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
            @RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
            @RequestHeader(Constant.HEADER_PLATFORM) String platform,
            @RequestHeader(Constant.HEADER_VERSION) String version,
            @RequestHeader(ComnConst.KEY_LANGUAGE) String language,
            @CustomerProfile AccessTokenJWTPayload customerProfile,
            @RequestParam(value="sectionId", required = false) String sectionId,
            @RequestParam(value="templateCode", required = false) String templateCode,
            @RequestParam(value="shelfId", required = false) String shelfId,
            @RequestParam(value="page", required = false) Integer page,
            @RequestParam(value="pageSize", required = false) Integer pageSize,
            HttpServletRequest httpRequest
    ) throws Exception {
        HashMap<String, Object> tv = new HashMap<String, Object>();
        TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
        AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);

        String brand = accessTokenJWTPayload.getCustomerInfo().getProductBrand().toUpperCase();

        tv.put("cvgList", accessTokenJWTPayload.getCustomerInfo().getCvgList());
        tv.put("productSegment", accessTokenJWTPayload.getCustomerInfo().getProductSegment());
        tv.put("brand", brand);
        tv.put("digitalId", accessTokenJWTPayload.getCustomerInfo().getDigitalId());
        tv.put("productId", accessTokenJWTPayload.getCustomerInfo().getProductId());
        tv.put("productType", accessTokenJWTPayload.getCustomerInfo().getProductType());
        tv.put(ComnConst.KEY_LANGUAGE, language!=null && !language.equals("") ? language : Constant.EN);
        tv.put("sectionId", sectionId);
        tv.put("templateCode", templateCode);
        tv.put("shelfId", shelfId);
        tv.put("page", page);
        tv.put("pageSize", pageSize);

        TransactionResult transactionResult = shelfTransaction.getTemplateDetailList(tv, httpRequest, brand);

        ShelfPageDefaultResponse<ShelfTemplateDetailRsp> data = new ShelfPageDefaultResponse(tv, transactionResult);
        ShelfGetLayoutPageJsonResponse<ShelfTemplateDetailRsp> getGenericJsonResponse = new ShelfGetLayoutPageJsonResponse<>(tv, transactionResult,
                data.getData(), new EmptyJsonResponse(), data.getPaging());
        return ResponseEntity.status(transactionResult.getHttpStatus()).body(getGenericJsonResponse);

    }

    @GetMapping(path = "/gettemplatehighlight", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericJsonResponse<ShelfTemplateHighlightRsp>> getTemplateHighlight(
            @RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
            @RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
            @RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
            @RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
            @RequestHeader(Constant.HEADER_PLATFORM) String platform,
            @RequestHeader(Constant.HEADER_VERSION) String version,
            @RequestHeader(ComnConst.KEY_LANGUAGE) String language,
            @CustomerProfile AccessTokenJWTPayload customerProfile,
            @RequestParam(value="sectionId", required = false) String sectionId,
            @RequestParam(value="templateCode", required = false) String templateCode,
            @RequestParam(value="useCmsContent", required = false) String useCmsContent,
            @RequestParam(value="shelfId", required = false) String shelfId,
            HttpServletRequest httpRequest
    ) throws Exception {
        HashMap<String, Object> tv = new HashMap<String, Object>();
        TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
        AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);

        String brand = accessTokenJWTPayload.getCustomerInfo().getProductBrand().toUpperCase();

        tv.put("cvgList", accessTokenJWTPayload.getCustomerInfo().getCvgList());
        tv.put("productSegment", accessTokenJWTPayload.getCustomerInfo().getProductSegment());
        tv.put("brand", brand);
        tv.put("digitalId", accessTokenJWTPayload.getCustomerInfo().getDigitalId());
        tv.put("productId", accessTokenJWTPayload.getCustomerInfo().getProductId());
        tv.put("productType", accessTokenJWTPayload.getCustomerInfo().getProductType());
        tv.put(ComnConst.KEY_LANGUAGE, language!=null && !language.equals("") ? language : Constant.EN);
        tv.put("sectionId", sectionId);
        tv.put("templateCode", templateCode);
        tv.put("useCmsContent", useCmsContent);
        tv.put("shelfId", shelfId);

        TransactionResult transactionResult = shelfTransaction.getTemplateHighlight(tv, httpRequest);

        ShelfPageDefaultResponse<ShelfTemplateHighlightRsp> data = new ShelfPageDefaultResponse(tv, transactionResult);
        GenericJsonResponse<ShelfTemplateHighlightRsp> getGenericJsonResponse = new GenericJsonResponse<>(tv, transactionResult,
                data.getData());
        log.info("End getTemplateHighlight : "+gson.toJson(getGenericJsonResponse));
        return ResponseEntity.status(transactionResult.getHttpStatus()).body(getGenericJsonResponse);

    }

    @GetMapping(path = "/getGrouping", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericJsonResponse<ShelfGroupingRsp>> getGrouping(
            @RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
            @RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
            @RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
            @RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
            @RequestHeader(Constant.HEADER_PLATFORM) String platform,
            @RequestHeader(Constant.HEADER_VERSION) String version,
            @RequestHeader(ComnConst.KEY_LANGUAGE) String language,
            @CustomerProfile AccessTokenJWTPayload customerProfile,
            @RequestParam(value="itemType") String itemType,
            @RequestParam(value="itemMapping") String itemMapping,
            @RequestParam(value="useCMS") String useCMS,
            @RequestParam(value="templateCode", required = false) String templateCode,
            @RequestParam(value="sectionId", required = false) String sectionId,
            @RequestParam(value="sourceApiName", required = false) String sourceApiName,
            @RequestParam(value="shelfId", required = false) String shelfId,
            HttpServletRequest httpRequest
    ) throws Exception {
        HashMap<String, Object> tv = new HashMap<String, Object>();
        TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
        AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);

        String brand = accessTokenJWTPayload.getCustomerInfo().getProductBrand().toUpperCase();

        tv.put("cvgList", accessTokenJWTPayload.getCustomerInfo().getCvgList());
        tv.put("productSegment", accessTokenJWTPayload.getCustomerInfo().getProductSegment());
        tv.put("brand", brand);
        tv.put("digitalId", accessTokenJWTPayload.getCustomerInfo().getDigitalId());
        tv.put("productId", accessTokenJWTPayload.getCustomerInfo().getProductId());
        tv.put(ComnConst.KEY_LANGUAGE, language!=null && !language.equals("") ? language : Constant.EN);
        tv.put("itemType", itemType);
        tv.put("itemMapping", itemMapping);
        tv.put("useCMS", useCMS);
        tv.put("templateCode", templateCode);
        tv.put("sectionId", sectionId);
        tv.put("sourceApiName", sourceApiName);
        tv.put("shelfId", shelfId);
        tv.put("productType", accessTokenJWTPayload.getCustomerInfo().getProductType());

        TransactionResult transactionResult = shelfTransaction.getGrouping(tv, httpRequest);

        ShelfPageDefaultResponse<ShelfGroupingRsp> data = new ShelfPageDefaultResponse(tv, transactionResult);
        GenericJsonResponse<ShelfGroupingRsp> getGenericJsonResponse = new GenericJsonResponse<>(tv, transactionResult,
                data.getData());
        log.info("End getGrouping : "+gson.toJson(getGenericJsonResponse));
        return ResponseEntity.status(transactionResult.getHttpStatus()).body(getGenericJsonResponse);

    }

    @GetMapping(path = "/getMajor", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericJsonResponse<ShelfMajorRsp>> getMajor(
            @RequestHeader(ComnConst.HEADER_AUTHORIZATION) String token,
            @RequestHeader(ComnConst.HEADER_SESSION_ID) String sessionId,
            @RequestHeader(ComnConst.HEADER_SOURCE_SYSTEM_ID) String sourceSystemId,
            @RequestHeader(Constant.HEADER_DEVICE_ID) String deviceId,
            @RequestHeader(Constant.HEADER_PLATFORM) String platform,
            @RequestHeader(Constant.HEADER_VERSION) String version,
            @RequestHeader(ComnConst.KEY_LANGUAGE) String language,
            @CustomerProfile AccessTokenJWTPayload customerProfile,
            @RequestParam(value="itemType") String itemType,
            @RequestParam(value="itemMapping") String itemMapping,
            @RequestParam(value="useCMS") String useCMS,
            @RequestParam(value="templateCode", required = false) String templateCode,
            @RequestParam(value="sectionId", required = false) String sectionId,
            @RequestParam(value="sourceApiName", required = false) String sourceApiName,
            @RequestParam(value="shelfId", required = false) String shelfId,
            HttpServletRequest httpRequest
    ) throws Exception {
        HashMap<String, Object> tv = new HashMap<String, Object>();
        TransactionValueUtil.initTransactionValue(tv, host, httpRequest, customerProfile);
        AccessTokenJWTPayload accessTokenJWTPayload = (AccessTokenJWTPayload) tv.get(ComnConst.HEADER_JWT_DECODE_KEY);

        String brand = accessTokenJWTPayload.getCustomerInfo().getProductBrand().toUpperCase();

        tv.put("cvgList", accessTokenJWTPayload.getCustomerInfo().getCvgList());
        tv.put("productSegment", accessTokenJWTPayload.getCustomerInfo().getProductSegment());
        tv.put("brand", brand);
        tv.put("digitalId", accessTokenJWTPayload.getCustomerInfo().getDigitalId());
        tv.put("productId", accessTokenJWTPayload.getCustomerInfo().getProductId());
        tv.put(ComnConst.KEY_LANGUAGE, language!=null && !language.equals("") ? language : Constant.EN);
        tv.put("itemType", itemType);
        tv.put("itemMapping", itemMapping);
        tv.put("useCMS", useCMS);
        tv.put("templateCode", templateCode);
        tv.put("sectionId", sectionId);
        tv.put("sourceApiName", sourceApiName);
        tv.put("shelfId", shelfId);
        tv.put("productType", accessTokenJWTPayload.getCustomerInfo().getProductType());

        TransactionResult transactionResult = shelfTransaction.getMajor(tv, httpRequest);

        ShelfPageDefaultResponse<ShelfMajorRsp> data = new ShelfPageDefaultResponse(tv, transactionResult);
        GenericJsonResponse<ShelfMajorRsp> getGenericJsonResponse = new GenericJsonResponse<>(tv, transactionResult,
                data.getData());
        log.info("End getMajor : "+gson.toJson(getGenericJsonResponse));
        return ResponseEntity.status(transactionResult.getHttpStatus()).body(getGenericJsonResponse);

    }
}
