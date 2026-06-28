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
import th.co.truecorp.commonapi.reward.model.ContentCategoryResponse;
import th.co.truecorp.commonapi.reward.model.EndpointResultRWD;
import th.co.truecorp.commonapi.reward.model.ShelfSectionDetailDtacInquiryShelfDetailRsp;
import th.co.truecorp.commonapi.reward.model.endpoint.ApiGwContentCategoryRsp;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.service.APIGWService;

import java.util.*;

@Component
public class ShelfContentCategoryEndpoint {

    private static final Logger log = LoggerFactory.getLogger(ShelfContentCategoryEndpoint.class);

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

    @EndpointLog(name = "APIGW."+Constant.ENDPOINT_SERVICE_CONTENT_CATEGORY)
    public EndpointResult getContentCategoryEndpoint(Map<String, Object> tv, Map<String, Object> queryParams) throws Exception {

        EndpointLogContext logContext = logContextService.getEndpointLoggingContext();

        try {
            ContentCategoryResponse contentCategory = new ContentCategoryResponse ();

            EndpointResult endpointResult = null;
            EndpointResultRWD endpointResult2 = new EndpointResultRWD();

            log.info("Call GetShelfDtacInquiryShelfDetail");
            HttpHeaders headers = createHeaders(tv);
            Map<String, Object> pathParams = createPathParams(tv);
            log.info("Calling API GetShelfInquiryShelfDetail with endpoint");
            ResponseEntity<ApiGwContentCategoryRsp> gwResponse = apigwService.execute(
                    logContext,
                    HttpMethod.GET,
                    Constant.ENDPOINT_SOURCE_SYSTEM_ID,
                    Constant.ENDPOINT_SERVICE_CONTENT_CATEGORY,
                    ApiGwContentCategoryRsp.class,
                    tv,
                    headers,
                    pathParams,
                    queryParams
            );
            logContext.putA("srvTxId",queryParams.get("id"));

            if (gwResponse != null && !gwResponse.getStatusCode().is2xxSuccessful()) {
                log.info("api failed!");
                String errorCode = gwResponse.getBody().getCode();
                String messageApigw = gwResponse.getBody().getMessage();
                String descriptionApigw = gwResponse.getBody().getDescription();
                String businessErrorApigw = gwResponse.getBody().getBusinessError();
                String errorApigw = gwResponse.getBody().getError();
                String timestamp = gwResponse.getBody().getTimestamp() != null ? gwResponse.getBody().getTimestamp() : "";
                String errorMessage = rewardUtill.mapError(descriptionApigw,messageApigw,errorApigw,timestamp);

                endpointResult2 = errorService.mapErrorCode(Constant.QUERY_DATA,
                        tv.get("brand").toString().toUpperCase(),
                        errorCode,
                        tv.get(ComnConst.KEY_LANGUAGE).toString().toUpperCase(),
                        errorMessage,
                        null != businessErrorApigw && !businessErrorApigw.isEmpty() ? businessErrorApigw : Constant.N_A,
                        Constant.MESSAGE);
                contentCategory = null;
                tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
                endpointResult = resultService.mapEndpointResultAPIGW(tv,Constant.ENDPOINT_SOURCE_SYSTEM_ID,Constant.ENDPOINT_SERVICE_GET_SHELF_CONTENT,errorCode,gwResponse.getStatusCode().value(),
                        endpointResult2.getEndpointStatusType(),
                        endpointResult2.getHttpStatus(),
                        endpointResult2.getEndpointResponseCode(),
                        errorCode,
                        errorMessage);
            } else {
                log.info("api success!");
                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                endpointResult2 = errorService.convertMapResult(endpointResult);
                tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
                contentCategory = parseContentCategoryResponse(gwResponse.getBody());
            }

            tv.put(Constant.ENDPOINT_SERVICE_CONTENT_CATEGORY, contentCategory);

            return endpointResult;
        } catch (Exception exception) {
            log.error("Error during execute apigw: {}", Constant.ENDPOINT_SERVICE_CONTENT_CATEGORY, exception);
            EndpointResult endpointResult = errorService.mapErrorException(exception,tv);
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

//    ----------------------------------------------------------
private ContentCategoryResponse parseContentCategoryResponse(ApiGwContentCategoryRsp jsonNode) throws Exception {
    ContentCategoryResponse contentCategoryResponse = new ContentCategoryResponse();

    log.debug("map ContentCategoryResponse value from api...");
    if(jsonNode != null){
        contentCategoryResponse.setCode(jsonNode.getCode() != null ? rewardUtill.parseStringNULL(jsonNode.getCode()) : null);
        contentCategoryResponse.setDescription(jsonNode.getDescription() != null ? rewardUtill.parseStringNULL(jsonNode.getDescription()) : null);
        contentCategoryResponse.setTimestamp(jsonNode.getTimestamp() != null ? rewardUtill.parseStringNULL(jsonNode.getTimestamp()) : null);
        contentCategoryResponse.setResource(parseResource(jsonNode.getResource()));
        contentCategoryResponse.setCharacteristic(parseCharacteristic(jsonNode.getCharacteristic()));
        contentCategoryResponse.setContent(parseContent(jsonNode.getContent()));
    }else{
        contentCategoryResponse = null;
    }

    log.debug("map ContentCategoryResponse value from api success");
    return contentCategoryResponse;
}

    private ContentCategoryResponse.Resource parseResource(ApiGwContentCategoryRsp.Resource jsonResourceNode) throws Exception {
        ContentCategoryResponse.Resource resource = new ContentCategoryResponse.Resource();

        log.debug("map Resource value from api...");
        if(jsonResourceNode != null){
            resource.setLimit(jsonResourceNode.getLimit() != null ? rewardUtill.parseStringNULL(jsonResourceNode.getLimit()) : null);
            resource.setNext(jsonResourceNode.getNext() != null ? rewardUtill.parseStringNULL(jsonResourceNode.getNext()) : null);
            resource.setTotalItem(jsonResourceNode.getTotalItem() != null ? rewardUtill.parseStringNULL(jsonResourceNode.getTotalItem()) : null);
            resource.setTotalPage(jsonResourceNode.getTotalPage() != null ? rewardUtill.parseStringNULL(jsonResourceNode.getTotalPage()) : null);
        }else{
            log.debug("Resource is null");
            resource = null;
        }

        log.debug("map Resource value from api success");
        return resource;
    }

    private List<ContentCategoryResponse.Characteristic> parseCharacteristic(List<ApiGwContentCategoryRsp.Characteristic> jsonCharacteristicNode) throws Exception {
        List<ContentCategoryResponse.Characteristic> characteristicList = new ArrayList<ContentCategoryResponse.Characteristic>();

        log.debug("map Characteristic value from api...");
        if(jsonCharacteristicNode != null){
            for(ApiGwContentCategoryRsp.Characteristic JsonNode : jsonCharacteristicNode){
                ContentCategoryResponse.Characteristic characteristic = new ContentCategoryResponse.Characteristic();
                characteristic.setName(JsonNode.getName() != null ? rewardUtill.parseStringNULL(JsonNode.getName()) : null);
                characteristic.setValue(JsonNode.getValue() != null ? rewardUtill.parseStringNULL(JsonNode.getValue()) : null);
                characteristicList.add(characteristic);
            }
        }else{
            log.debug("Characteristic is null");
            characteristicList = null;
        }

        log.debug("map Characteristic value from api success");
        return characteristicList;
    }

    private List<ContentCategoryResponse.Content> parseContent(List<ApiGwContentCategoryRsp.Content> jsonContentNode) throws Exception {
        List<ContentCategoryResponse.Content> contentList = new ArrayList<ContentCategoryResponse.Content>();

        log.debug("map Content value from api...");
        if(jsonContentNode != null){
            for(ApiGwContentCategoryRsp.Content JsonNode : jsonContentNode){
                ContentCategoryResponse.Content content = new ContentCategoryResponse.Content();
                content.setId(JsonNode.getId() != null ? rewardUtill.parseStringNULL(JsonNode.getId()) : null);
                content.setType(JsonNode.getType() != null ? rewardUtill.parseStringNULL(JsonNode.getType()) : null);
                content.setOriginalID(JsonNode.getOriginalID() != null ? rewardUtill.parseStringNULL(JsonNode.getOriginalID()) : null);
                content.setTitle(JsonNode.getTitle() != null ? rewardUtill.parseStringNULL(JsonNode.getTitle()) : null);
                content.setArticleCategory(JsonNode.getArticleCategory() != null ? JsonNode.getArticleCategory() : null);
                content.setTag(JsonNode.getTag() != null ? JsonNode.getTag() : null);
                content.setStatus(JsonNode.getStatus() != null ? rewardUtill.parseStringNULL(JsonNode.getStatus()) : null);
                content.setValidFor(parseValidFor(JsonNode.getValidFor()));
                content.setStatistics(parseStatistics(JsonNode.getStatistics()));
                content.setCharacteristic(parseCharacteristic(JsonNode.getCharacteristic()));
                content.setCreateBy(JsonNode.getCreateBy() != null ? rewardUtill.parseStringNULL(JsonNode.getCreateBy()) : null);
                content.setCreateDate(JsonNode.getCreateDate() != null ? rewardUtill.parseStringNULL(JsonNode.getCreateDate()) : null);
                content.setCreateBySsoid(JsonNode.getCreateBySsoid() != null ? rewardUtill.parseStringNULL(JsonNode.getCreateBySsoid()) : null);
                content.setUpdateBy(JsonNode.getUpdateBy() != null ? rewardUtill.parseStringNULL(JsonNode.getUpdateBy()) : null);
                content.setUpdateDate(JsonNode.getUpdateDate() != null ? rewardUtill.parseStringNULL(JsonNode.getUpdateDate()) : null);
                content.setUpdateBySsoId(JsonNode.getUpdateBySsoId() != null ? rewardUtill.parseStringNULL(JsonNode.getUpdateBySsoId()) : null);
                content.setDetail(JsonNode.getDetail() != null ? rewardUtill.parseStringNULL(JsonNode.getDetail()) : null);
                content.setThumbList(parseThumbList(JsonNode.getThumbList()));
                content.setCampaign(parseCampaign(JsonNode.getCampaign()));
                content.setRedeemPoint(JsonNode.getRedeemPoint() != null ? rewardUtill.parseStringNULL(JsonNode.getRedeemPoint()) : null);
                content.setCardType(mapListString(JsonNode.getCardType()));
                System.out.println("::: "+ content.getCardType());
                content.setContentSpecification(parseContentSpecification(JsonNode.getContentSpecification()));
                content.setAddtionalInfo(parseAddtionalInfo(JsonNode.getAddtionalInfo()));
                content.setAllowApp(JsonNode.getAllowApp() != null ? JsonNode.getAllowApp() : null);
                content.setSetting(JsonNode.getSetting() != null ? rewardUtill.parseStringNULL(JsonNode.getSetting()) : null);
                content.setSubContent(JsonNode.getSubContent() != null ? parseContent(JsonNode.getSubContent()) : null);
                content.setArticleCategoryDetail(parseArticleCategoryDetail(JsonNode.getArticleCategoryDetail()));
                contentList.add(content);
            }
        }else{
            log.debug("Content is null");
            contentList = null;
        }

        log.debug("map Content value from api success");
        return contentList;
    }

    private ContentCategoryResponse.ValidFor parseValidFor(ApiGwContentCategoryRsp.ValidFor jsonValidForNode) throws Exception {
        ContentCategoryResponse.ValidFor validFor = new ContentCategoryResponse.ValidFor();

        log.debug("map ValidFor value from api...");
        if(jsonValidForNode != null){
            validFor.setStartDate(jsonValidForNode.getStartDate() != null ? rewardUtill.parseStringNULL(jsonValidForNode.getStartDate()) : null);
            validFor.setEndDate(jsonValidForNode.getEndDate() != null ? rewardUtill.parseStringNULL(jsonValidForNode.getEndDate()) : null);
        }else{
            log.debug("ValidFor is null");
            validFor = null;
        }

        log.debug("map ValidFor value from api success");
        return validFor;
    }

    private ContentCategoryResponse.Statistics parseStatistics(ApiGwContentCategoryRsp.Statistics jsonValidForNode) throws Exception {
        ContentCategoryResponse.Statistics validFor = new ContentCategoryResponse.Statistics();

        log.debug("map Statistics value from api...");
        if(jsonValidForNode != null){
            validFor.setViews(jsonValidForNode.getViews() != null ? rewardUtill.parseStringNULL(jsonValidForNode.getViews()) : null);
            validFor.setLikes(jsonValidForNode.getLikes() != null ? rewardUtill.parseStringNULL(jsonValidForNode.getLikes()) : null);
            validFor.setWatchLater(jsonValidForNode.getWatchLater() != null ? rewardUtill.parseStringNULL(jsonValidForNode.getWatchLater()) : null);
            validFor.setFavorite(jsonValidForNode.getFavorite() != null ? rewardUtill.parseStringNULL(jsonValidForNode.getFavorite()) : null);
            validFor.setRating(parseRating(jsonValidForNode.getRating()));
        }else{
            log.debug("Statistics is null");
            validFor = null;
        }

        log.debug("map Statistics value from api success");
        return validFor;
    }

    private ContentCategoryResponse.Rating parseRating(ApiGwContentCategoryRsp.Rating jsonResourceNode) throws Exception {
        ContentCategoryResponse.Rating rating = new ContentCategoryResponse.Rating();

        log.debug("map Rating value from api...");
        if(jsonResourceNode != null){
            rating.setAverage(jsonResourceNode.getAverage() != null ? rewardUtill.parseStringNULL(jsonResourceNode.getAverage()) : null);
            rating.setTotal(jsonResourceNode.getTotal() != null ? rewardUtill.parseStringNULL(jsonResourceNode.getTotal()) : null);
        }else{
            log.debug("Rating is null");
            rating = null;
        }

        log.debug("map Rating value from api success");
        return rating;
    }

    private ContentCategoryResponse.ThumbList parseThumbList(ApiGwContentCategoryRsp.ThumbList jsonThumbListNode) throws Exception {
        ContentCategoryResponse.ThumbList thumbList = new ContentCategoryResponse.ThumbList();

        log.debug("map ThumbList value from api...");
        if(jsonThumbListNode != null){
            thumbList.setBanner(jsonThumbListNode.getBanner() != null ? rewardUtill.parseStringNULL(jsonThumbListNode.getBanner()) : null);
            thumbList.setHighlight(parseHighlight(jsonThumbListNode.getHighlight()));
            thumbList.setLogo(parselogo(jsonThumbListNode.getLogo()));
            thumbList.setThumbnail(jsonThumbListNode.getThumbnail() != null ? rewardUtill.parseStringNULL(jsonThumbListNode.getThumbnail()) : null);
        }else{
            log.debug("ThumbList is null");
            thumbList = null;
        }

        log.debug("map ThumbList value from api success");
        return thumbList;
    }

    private ContentCategoryResponse.Highlight parseHighlight(ApiGwContentCategoryRsp.Highlight jsonHighlightNode) throws Exception {
        ContentCategoryResponse.Highlight highlight = new ContentCategoryResponse.Highlight();

        log.debug("map Highlight value from api...");
        if(jsonHighlightNode != null){
            highlight.setStandard(jsonHighlightNode.getStandard() != null ? rewardUtill.parseStringNULL(jsonHighlightNode.getStandard()) : null);
            highlight.setHighlight16x9(jsonHighlightNode.getHighlight16x9() != null ? rewardUtill.parseStringNULL(jsonHighlightNode.getHighlight16x9()) : null);
        }else{
            log.debug("Highlight is null");
            highlight = null;
        }

        log.debug("map Highlight value from api success");
        return highlight;
    }

    private ContentCategoryResponse.Logo parselogo(ApiGwContentCategoryRsp.Logo jsonLogoNode) throws Exception {
        ContentCategoryResponse.Logo logo = new ContentCategoryResponse.Logo();

        log.debug("map Logo value from api...");
        if(jsonLogoNode != null){
            logo.setSizeS(jsonLogoNode.getSizeS() != null ? rewardUtill.parseStringNULL(jsonLogoNode.getSizeS()) : null);
            logo.setSizeM(jsonLogoNode.getSizeM() != null ? rewardUtill.parseStringNULL(jsonLogoNode.getSizeM()) : null);
        }else{
            log.debug("Logo is null");
            logo = null;
        }

        log.debug("map Logo value from api success");
        return logo;
    }

    private ContentCategoryResponse.Campaign parseCampaign(ApiGwContentCategoryRsp.Campaign jsonCampaignNode) throws Exception {
        ContentCategoryResponse.Campaign campaign = new ContentCategoryResponse.Campaign();

        log.debug("map Campaign value from api...");
        if(jsonCampaignNode != null){
            campaign.setType(jsonCampaignNode.getType() != null ? rewardUtill.parseStringNULL(jsonCampaignNode.getType()) : null);
            campaign.setSubType(jsonCampaignNode.getSubType() != null ? rewardUtill.parseStringNULL(jsonCampaignNode.getSubType()) : null);
            campaign.setId(jsonCampaignNode.getId() != null ? rewardUtill.parseStringNULL(jsonCampaignNode.getId()) : null);
        }else{
            log.debug("Campaign is null");
            campaign = null;
        }

        log.debug("map Campaign value from api success");
        return campaign;
    }

    private ContentCategoryResponse.ContentSpecification parseContentSpecification(ApiGwContentCategoryRsp.ContentSpecification jsonContentSpecificationNode) throws Exception {
        ContentCategoryResponse.ContentSpecification contentSpecification = new ContentCategoryResponse.ContentSpecification();

        log.debug("map ContentSpecification value from api...");
        if(jsonContentSpecificationNode != null){
            contentSpecification.setCharacteristic(parseCharacteristic(jsonContentSpecificationNode.getCharacteristic()));
        }else{
            log.debug("ContentSpecification is null");
            contentSpecification = null;
        }

        log.debug("map ContentSpecification value from api success");
        return contentSpecification;
    }

    private ContentCategoryResponse.AddtionalInfo parseAddtionalInfo(ApiGwContentCategoryRsp.AddtionalInfo jsonAddtionalInfoNode) throws Exception {
        ContentCategoryResponse.AddtionalInfo addtionalInfo = new ContentCategoryResponse.AddtionalInfo();

        log.debug("map AddtionalInfo value from api...");
        if(jsonAddtionalInfoNode != null){
            addtionalInfo.setEnableReview(jsonAddtionalInfoNode.getEnableReview() != null ? rewardUtill.parseStringNULL(jsonAddtionalInfoNode.getEnableReview()) : null);
            addtionalInfo.setTelNo(jsonAddtionalInfoNode.getTelNo() != null ? rewardUtill.parseStringNULL(jsonAddtionalInfoNode.getTelNo()) : null);
            addtionalInfo.setBudgetSaveAmount(jsonAddtionalInfoNode.getBudgetSaveAmount() != null ? rewardUtill.parseStringNULL(jsonAddtionalInfoNode.getBudgetSaveAmount()) : null);
            addtionalInfo.setBudgetSaveShow(jsonAddtionalInfoNode.getBudgetSaveShow() != null ? rewardUtill.parseStringNULL(jsonAddtionalInfoNode.getBudgetSaveShow()) : null);
            addtionalInfo.setDefaultCodeFormat(jsonAddtionalInfoNode.getDefaultCodeFormat() != null ? rewardUtill.parseStringNULL(jsonAddtionalInfoNode.getDefaultCodeFormat()) : null);
            addtionalInfo.setExLink(jsonAddtionalInfoNode.getExLink() != null ? rewardUtill.parseStringNULL(jsonAddtionalInfoNode.getExLink()) : null);
            addtionalInfo.setMerchant(parseMerchant(jsonAddtionalInfoNode.getMerchant()));
            addtionalInfo.setPrivilegeVersion(jsonAddtionalInfoNode.getPrivilegeVersion() != null ? rewardUtill.parseStringNULL(jsonAddtionalInfoNode.getPrivilegeVersion()) : null);
            addtionalInfo.setRequireLocation(jsonAddtionalInfoNode.getRequireLocation() != null ? rewardUtill.parseStringNULL(jsonAddtionalInfoNode.getRequireLocation()) : null);
            addtionalInfo.setTextRedeemButton(jsonAddtionalInfoNode.getTextRedeemButton() != null ? rewardUtill.parseStringNULL(jsonAddtionalInfoNode.getTextRedeemButton()) : null);
            addtionalInfo.setTimeCounterShow(jsonAddtionalInfoNode.getTimeCounterShow() != null ? rewardUtill.parseStringNULL(jsonAddtionalInfoNode.getTimeCounterShow()) : null);
        }else{
            log.debug("AddtionalInfo is null");
            addtionalInfo = null;
        }

        log.debug("map AddtionalInfo value from api success");
        return addtionalInfo;
    }

    private ContentCategoryResponse.Merchant parseMerchant(ApiGwContentCategoryRsp.Merchant jsonMerchantNode) throws Exception {
        ContentCategoryResponse.Merchant merchant = new ContentCategoryResponse.Merchant();

        log.debug("map AddtionalInfo value from api...");
        if(jsonMerchantNode != null){
            merchant.setId(jsonMerchantNode.getId() != null ? rewardUtill.parseStringNULL(jsonMerchantNode.getId()) : null);
            merchant.setNameEn(jsonMerchantNode.getNameEn() != null ? rewardUtill.parseStringNULL(jsonMerchantNode.getNameEn()) : null);
            merchant.setNameTh(jsonMerchantNode.getNameTh() != null ? rewardUtill.parseStringNULL(jsonMerchantNode.getNameTh()) : null);
        }else{
            log.debug("AddtionalInfo is null");
            merchant = null;
        }

        log.debug("map AddtionalInfo value from api success");
        return merchant;
    }

    private ContentCategoryResponse.ArticleCategoryDetail parseArticleCategoryDetail(ApiGwContentCategoryRsp.ArticleCategoryDetail jsonArticleCategoryDetailNode) throws Exception {
        ContentCategoryResponse.ArticleCategoryDetail articleCategoryDetail = new ContentCategoryResponse.ArticleCategoryDetail();

        log.debug("map ArticleCategoryDetail value from api...");
        if(jsonArticleCategoryDetailNode != null){
            articleCategoryDetail.setSlug(jsonArticleCategoryDetailNode.getSlug() != null ? rewardUtill.parseStringNULL(jsonArticleCategoryDetailNode.getSlug()) : null);
            articleCategoryDetail.setName(jsonArticleCategoryDetailNode.getName() != null ? rewardUtill.parseStringNULL(jsonArticleCategoryDetailNode.getName()) : null);
            articleCategoryDetail.setParent(jsonArticleCategoryDetailNode.getParent() != null ? rewardUtill.parseStringNULL(jsonArticleCategoryDetailNode.getParent()) : null);
            articleCategoryDetail.setSubCategory(parseSubCategory(jsonArticleCategoryDetailNode.getSubCategory()));
        }else{
            log.debug("ArticleCategoryDetail is null");
            articleCategoryDetail = null;
        }

        log.debug("map ArticleCategoryDetail value from api success");
        return articleCategoryDetail;
    }

    private ContentCategoryResponse.SubCategory parseSubCategory(ApiGwContentCategoryRsp.SubCategory jsonSubCategoryNode) throws Exception {
        ContentCategoryResponse.SubCategory subCategory = new ContentCategoryResponse.SubCategory();

        log.debug("map SubCategory value from api...");
        if(jsonSubCategoryNode != null){
            subCategory.setSlug(jsonSubCategoryNode.getSlug() != null ? rewardUtill.parseStringNULL(jsonSubCategoryNode.getSlug()) : null);
            subCategory.setName(jsonSubCategoryNode.getName() != null ? rewardUtill.parseStringNULL(jsonSubCategoryNode.getName()) : null);
            subCategory.setParent(jsonSubCategoryNode.getParent() != null ? rewardUtill.parseStringNULL(jsonSubCategoryNode.getParent()) : null);
        }else{
            log.debug("SubCategory is null");
            subCategory = null;
        }

        log.debug("map SubCategory value from api success");
        return subCategory;
    }

    private List<String> mapListString(Optional jn) {
        List<String> listVal = new ArrayList<>();

        if(!"".equals(jn.get().toString())){
            listVal.addAll((Collection<? extends String>) jn.get());
        }else{
            listVal = null;
        }

        return listVal;
    }

//    private List<String> mapListString(JsonNode jn) {
//        List<String> listVal = new ArrayList<>();
//        for (JsonNode j:jn) {
//            listVal.add(j.toString().replace("\"", ""));
//        }
//        return listVal;
//    }
//    ---------------------------------------------------------

}
