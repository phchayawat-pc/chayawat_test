package th.co.truecorp.commonapi.reward.service;
//2025-03-11 แก้ไข thumbnail4x3 จาก promotionPattern/relatedParty/href เป็น promotionPattern/promotionCriteriaGroup/href
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSystemConfig;
import th.co.truecorp.commonapi.reward.cms.jpa.service.*;
import th.co.truecorp.commonapi.reward.common.endpoint.CommonServiceEndpoint;
import th.co.truecorp.commonapi.reward.common.endpoint.CommonServiceProfileEndpoint;
import th.co.truecorp.commonapi.reward.common.model.GetDigitalByDigitalIdResponse;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.dto.*;
import th.co.truecorp.commonapi.reward.endpoint.*;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonapi.reward.redis.RedisCacheService;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.model.EndpointResult;

import java.util.*;

@Service
public class ShelfSectionDetailService {

    private static Logger log = LoggerFactory.getLogger(ShelfSectionDetailService.class);

    Gson gson = new Gson();

    @Autowired
    private ResultService resultService;

    @Autowired
    private APIGWUtill apigwUtill;

    @Autowired
    private RewardUtill rewardUtill;

    @Autowired
    private RwdSystemConfigService rwdSystemConfigService;

    @Autowired
    private RwdSectionDetailService rwdSectionDetailService;

    @Autowired
    private ShelfContentEndpoint shelfContentEndpoint;

    @Autowired
    private ShelfTrueInquiryShelfDetailEndpoint shelfTrueInquiryShelfDetailEndpoint;

    @Autowired
    private ShelfSectionDetailDtacInquiryShelfDetailEndpoint shelfSectionDetailDtacInquiryShelfDetailEndpoint;

    @Autowired
    private ErrorService errorService;

    @Autowired
    private CommonServiceProfileEndpoint commonServiceProfileEndpoint;

    @Autowired
    private CommonServiceEndpoint commonServiceEndpoint;

    @Autowired
    private RedisCacheService redisCacheService;

    @EndpointLog (name = "ALL_DB.GetSectionDetail")
    public EndpointResult getSectionDetail(Map<String, Object> tv , String brand) throws Exception {
        log.info("get Shelf SectionDetail");
        EndpointResult endpointResult = null;
        tv.put(ComnConst.KEY_LANGUAGE, rewardUtill.handleLanguage(tv.get(ComnConst.KEY_LANGUAGE).toString()));
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();

        String sectionId = tv.get("sectionId") != null ? tv.get("sectionId").toString():"";
        String displayTypeCode = tv.get("displayTypeCode") != null ? tv.get("displayTypeCode").toString():"";
        String productType = tv.get("productType") != null ? tv.get("productType").toString():"";
        try {

            List<ShelfSectionDetailItemRsp> shelfSectionDetailItems = new ArrayList<ShelfSectionDetailItemRsp>();

            String value = "";
            List<ShelfSectionDetailDto> sectionDetailDtoList = new ArrayList<ShelfSectionDetailDto>();

            try {
                Optional<RwdSystemConfig> optional = rwdSystemConfigService.findConfigs("MAX_SLIDE", "FIX");
                if (!optional.isEmpty()) {
                    RwdSystemConfig rwdSystemConfig = optional.get();
                    value = rwdSystemConfig.getValue();
                    log.info("get SystemConfig where MAX_SLIDE and FIX : value : " + value);
                }
            }catch (Exception e){
                log.info("error get RwdSystemConfig : "+e.getMessage());
            }

            try {
                sectionDetailDtoList = rwdSectionDetailService.findShelfSectionDetailDtoBySectionId(sectionId);
                Integer seqNo = 0;

                if (sectionDetailDtoList == null) {
                    log.info("sectionDetailDtoList is null, initializing empty list.");
                    sectionDetailDtoList = Collections.emptyList();
                }
                log.info("get sectionDetail size : " + sectionDetailDtoList.size());

                if (!sectionDetailDtoList.isEmpty()) {
                    for (ShelfSectionDetailDto sectionDetail : sectionDetailDtoList) {
                        String itemTypeCode = sectionDetail.getitem_type_code();
                        String itemMapping = sectionDetail.getitem_mapping();

                        log.info("itemTypeCode is {}",itemTypeCode);
                        log.info("itemMapping is {}",itemMapping);

                        if ("MERCHANT".equals(itemTypeCode)) {
                            log.info("MERCHANT");
                            endpointResult = errorService.revertMapResult(fetchShelfContent(tv, itemMapping, lang.toLowerCase(), Integer.valueOf(value)));
                            List<ShelfSectionDetailItemRsp> shelfSectionDetailItem =
                                    mapContentToShelfSectionDetailItemRsp(tv, seqNo, safeGetContentData(tv, "GetContent"), itemMapping);

                            if (shelfSectionDetailItem != null) {
                                shelfSectionDetailItems.addAll(shelfSectionDetailItem);
                                log.info("shelfSectionDetailItem is {}", shelfSectionDetailItem);
                                if (!shelfSectionDetailItem.isEmpty()) {
                                    seqNo = shelfSectionDetailItem.get(shelfSectionDetailItem.size() - 1).getSeqNo();
                                }
                            }
                        } else if ("SHELF".equals(itemTypeCode)) {
                            log.info("SHELF");
                            String shelfTypeCode = sectionDetail.getshelf_type_code();
                            log.info("shelf type code is {}",shelfTypeCode);
                            if ("NORMAL_TRUE".equals(shelfTypeCode)) {
                                log.info("SHELF: NORMAL_TRUE");
                                endpointResult = errorService.revertMapResult(fetchTrueShelfInquiryShelfDetail(tv, itemMapping, lang, brand, Integer.valueOf(value)));
                                List<ShelfSectionDetailItemRsp> shelfSectionDetailItem =
                                        mapInquiryToShelfSectionDetailItemRsp(tv, seqNo, safeGetContentData(tv, "GetShelfInquiryShelfDetail"), itemMapping);

                                if (shelfSectionDetailItem != null) {
                                    shelfSectionDetailItems.addAll(shelfSectionDetailItem);
                                    if (!shelfSectionDetailItem.isEmpty()) {
                                        seqNo = shelfSectionDetailItem.get(shelfSectionDetailItem.size() - 1).getSeqNo();
                                    }
                                }
                            } else if ("SPECIAL".equals(shelfTypeCode)) {
                                log.info("SHELF: SPECIAL");
                                String customerGrade = null;
                                String customerNumber = null;

                                EndpointResult result = commonServiceProfileEndpoint.getCommonService(tv);
                                EndpointResult resultProfile = commonServiceEndpoint.getCommonService(tv);
                                CustomerProfileRsp commonProfileRsp = (CustomerProfileRsp) tv.get("commonProfileRspEndpoint");
                                GetDigitalByDigitalIdResponse.Profile serviceProfileRsp = (GetDigitalByDigitalIdResponse.Profile) tv.get("serviceProfileRspEndpoint");

                                if (commonProfileRsp != null && serviceProfileRsp != null) {
                                    log.info("commonProfileRsp data exist");
                                    customerGrade = commonProfileRsp.getCardType();
                                    log.info("customerGrade: " + customerGrade);
                                    customerNumber = serviceProfileRsp.getCustomerNumber();
                                    log.info("customerNumber: " + customerNumber);

                                    Map<String, Object> queryParams = mapQueryDtacSpecialShelfInquiryShelfDetail(tv, lang, brand, itemMapping, productType, customerNumber, customerGrade);
                                    endpointResult = errorService.revertMapResult(redisShelfDtacInquiryShelfDetail(tv, sectionId, lang, itemMapping, queryParams));
                                    ShelfSectionDetailDtacInquiryShelfDetailRsp detailRsp = (ShelfSectionDetailDtacInquiryShelfDetailRsp) tv.get("GetShelfDtacInquiryShelfDetail");

                                    if (detailRsp != null) {
                                        shelfSectionDetailItems.addAll(mapInquiryToShelfSPECIALSectionDetailItemRsp(tv, seqNo, itemMapping, detailRsp));
                                    }
                                }
                            } else if ("RECOMMENED".equals(shelfTypeCode)) {
                                log.info("SHELF: RECOMMENED");
                            }
                        }
                    }

                    if (endpointResult == null || endpointResult.getHttpStatus() == 200) {
                        if (shelfSectionDetailItems != null && !shelfSectionDetailItems.isEmpty()) {
                            log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + " is success with data");
                            endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                        } else {
                            log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + " is success without data");
                            endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC, ComnConst.STTS_CODE_SUCC);
                        }
                    }
                } else {
                    log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + " is not found shelf section detail");
                    endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC, ComnConst.STTS_CODE_SUCC);
                }

                ShelfSectionDetailRsp shelfSectionDetail = new ShelfSectionDetailRsp();
                shelfSectionDetail.setSectionId(sectionId);
                shelfSectionDetail.setLang(lang);
                shelfSectionDetail.setDisplayTypeCode(displayTypeCode);
                shelfSectionDetail.setSectionDetailItem(shelfSectionDetailItems);

                log.info("set ShelfSectionDetail : " + gson.toJson(shelfSectionDetail));
                tv.put(Constant.TRANSACTION_RESPONSE_KEY, shelfSectionDetail);
                log.info("endpointResult : " + endpointResult);

            } catch (Exception e) {
                log.info("error get RwdSectionDetail : " + e.getMessage(), e);
                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC, ComnConst.STTS_CODE_SUCC);
            }
        } catch (Exception e) {
            log.info("error is " + e.getMessage());
            endpointResult = errorService.mapErrorException(e,tv);//resultService.getEndpointExceptionResult(tv, e);
            return endpointResult;
        }

        return endpointResult;
    }

    // Utility method to safely get content data
    private List<ShelfContentDataApiRsp.ContentData.DataDetails> safeGetContentData(Map<String, Object> tv, String key) {
        Object contentData = tv.get(key);
        if (contentData instanceof List) {
            return (List<ShelfContentDataApiRsp.ContentData.DataDetails>) contentData;
        }
        log.info("{} is null or not a list", key);
        return Collections.emptyList();
    }

    private List<ShelfSectionDetailItemRsp> mapContentToShelfSectionDetailItemRsp(Map<String, Object> tv, Integer seqNo, List<ShelfContentDataApiRsp.ContentData.DataDetails> privilegeList, String itemMapping){
        log.info("map Content To ShelfSectionDetailItem");
        List<ShelfSectionDetailItemRsp> dealLists = new ArrayList<ShelfSectionDetailItemRsp>();
        log.info("privilegeList is {}",privilegeList);
        if(privilegeList != null && !privilegeList.isEmpty()){
            for(ShelfContentDataApiRsp.ContentData.DataDetails privilege:privilegeList){
                ShelfSectionDetailItemRsp detail = new ShelfSectionDetailItemRsp();
                seqNo++;
                String contentType = "";
                if ("privilege".equals(privilege.getContent_type())) {
                    contentType = "DEAL";
                } else if ("trueyoumerchant".equals(privilege.getContent_type())) {
                    contentType = "MERCHANT";
                } else if ("trueyouarticle".equals(privilege.getContent_type())) {
                    String thematicMainShelfIds = privilege.getSetting() != null ? privilege.getSetting().getThematic_main_shelf_ids() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                    if (thematicMainShelfIds != null && !thematicMainShelfIds.isEmpty()) {
                        contentType = "THEMATIC";
                    } else {
                        contentType = "ARTICLE";
                    }
                }

                detail.setSeqNo(seqNo);
                detail.setShelfId(itemMapping);
                detail.setContentType(contentType);
                detail.setCampaignId(privilege.getId());
                detail.setCampaignCode(privilege.getCampaign_code());
                detail.setTimeCounterFlag(privilege.getInfo() != null ? privilege.getInfo().getTime_counter_show() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                detail.setCampaignDescription(privilege.getTitle());
                detail.setCampaignExpireDate(privilege.getExpire_date());
                detail.setCampaignType(privilege.getCampaign_type());
                detail.setRegularPoint(privilege.getRedeem_point() != null && !privilege.getRedeem_point().isEmpty() ? privilege.getRedeem_point() : "0");
                detail.setOfferPoint("0");

                ThumbnailListRsp thumbnail = new ThumbnailListRsp();
                String highlight16x9 = "";
                if(privilege.getThumb_list() != null){
                    highlight16x9 = privilege.getThumb_list().getHighlight16x9();
                }
                thumbnail.setThumbnail16x9(highlight16x9);
                detail.setThumbnailList(thumbnail);

                String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
                if(lang.equalsIgnoreCase(Constant.TH)){
                    String budgetSaveCurrencyTH = "";
                    if(privilege.getInfo() != null){
                        budgetSaveCurrencyTH = privilege.getInfo().getBudget_save_currency_th();
                    }
                    detail.setCampaignName(budgetSaveCurrencyTH);
                }else{
                    String budgetSaveTextEn = "";
                    if(privilege.getInfo() != null){
                        budgetSaveTextEn = privilege.getInfo().getBudget_save_text_en();
                    }
                    detail.setCampaignName(budgetSaveTextEn);
                }

                List<String> resultCardType = privilege.getCard_type();
                detail.setCardType(resultCardType);

                dealLists.add(detail);
            }
        }else{
            log.info("privilegeList is null");
            dealLists = null;
        }

        return dealLists;
    }

    private List<ShelfSectionDetailItemRsp> mapInquiryToShelfSectionDetailItemRsp(Map<String, Object> tv, Integer seqNo, List<ShelfContentDataApiRsp.ContentData.DataDetails> shelfItemsList, String itemMapping){
        log.info("map Inquiry To ShelfSectionDetailItem");
        List<ShelfSectionDetailItemRsp> dealLists = new ArrayList<ShelfSectionDetailItemRsp>();

        if(!shelfItemsList.isEmpty()){
            for(ShelfContentDataApiRsp.ContentData.DataDetails shelfItems:shelfItemsList) {
                ShelfSectionDetailItemRsp detail = new ShelfSectionDetailItemRsp();
                seqNo++;
                String contentType = "";
                String contentTypeValue = shelfItems.getContent_type();
                if (contentTypeValue != null) {
                    switch (contentTypeValue) {
                        case "privilege" -> contentType = "DEAL";
                        case "trueyoumerchant" -> contentType = "MERCHANT";
                        case "trueyouarticle" -> {
                            String ids = Optional.ofNullable(shelfItems.getSetting())
                                    .map(setting -> setting.getThematic_main_shelf_ids())
                                    .orElse("");
                            log.info("get thematicMainShelfIds : " + ids);
                            contentType = !ids.isEmpty() ? "THEMATIC" : "ARTICLE";
                        }
                    }
                }

                detail.setSeqNo(seqNo);
                detail.setShelfId(itemMapping);
                detail.setContentType(contentType);
                detail.setCampaignId(shelfItems.getId());
                detail.setCampaignCode(shelfItems.getCampaign_code());
                String timeCounterShow = "";
                if(shelfItems.getInfo() != null){
                    timeCounterShow = shelfItems.getInfo().getTime_counter_show();
                }
                detail.setTimeCounterFlag(timeCounterShow);
                detail.setCampaignDescription(shelfItems.getTitle());
                detail.setCampaignExpireDate(shelfItems.getExpire_date());
                detail.setCampaignType(shelfItems.getCampaign_type());
                detail.setRegularPoint(
                        shelfItems.getRedeem_point() != null && !shelfItems.getRedeem_point().isEmpty()
                                ? shelfItems.getRedeem_point()
                                : "0"
                );
                detail.setOfferPoint("0");

                ThumbnailListRsp thumbnail = new ThumbnailListRsp();
                String highlight16x9 = "";
                if(shelfItems.getThumb_list() != null){
                    highlight16x9 = shelfItems.getThumb_list().getHighlight16x9();
                }
                thumbnail.setThumbnail16x9(highlight16x9);
                detail.setThumbnailList(thumbnail);

                String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
                if (shelfItems != null && shelfItems.getInfo() != null) {
                    if ("TH".equalsIgnoreCase(lang)) {
                        detail.setCampaignName(shelfItems.getInfo().getBudget_save_currency_th());
                    } else {
                        detail.setCampaignName(shelfItems.getInfo().getBudget_save_text_en());
                    }
                }

                List<String> resultCardType = shelfItems.getCard_type();
                detail.setCardType(resultCardType);

                dealLists.add(detail);
            }
        }else{
            dealLists = null;
        }

        return dealLists;
    }

    private List<ShelfSectionDetailItemRsp> mapInquiryToShelfSPECIALSectionDetailItemRsp(Map<String, Object> tv, Integer seqNo, String itemMapping, ShelfSectionDetailDtacInquiryShelfDetailRsp detailRsp){
        log.info("map inquiry to special dtac Section detail item : "+ gson.toJson(detailRsp));
        List<ShelfSectionDetailItemRsp> dealLists = new ArrayList<ShelfSectionDetailItemRsp>();

        if(detailRsp != null && detailRsp.getPromotionPattern() != null && !detailRsp.getPromotionPattern().isEmpty()){
            for(ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionPattern promotionPattern:detailRsp.getPromotionPattern()) {
                ShelfSectionDetailItemRsp detail = new ShelfSectionDetailItemRsp();
                seqNo++;

                String contentType = null;
                if (promotionPattern.getId() != null && !promotionPattern.getId().isEmpty()) {
                    log.info("Promotion ID is not null or empty. Setting contentType to DEAL.");
                    contentType = Constant.DEAL;
                } else {
                    if(promotionPattern.getBanner() != null){
                        log.info("Promotion ID is null or empty. Banner type: " + promotionPattern.getBanner().getType());
                        switch (promotionPattern.getBanner().getType()) {
                            case "2":
                                log.info("Banner type is '2'. Setting contentType to MAJOR.");
                                contentType = Constant.MAJOR;
                                break;
                            case "7":
                                log.info("Banner type is '7'. Setting contentType to GROUPING.");
                                contentType = Constant.GROUPING;
                                break;
                            case "8":
                                log.info("Banner type is '8'. Setting contentType to FESTIVE.");
                                contentType = Constant.FESTIVE;
                                break;
                        }
                    }
                }

                String campaignId = "";
                if(Constant.DEAL.equals(contentType)){
                    campaignId = promotionPattern.getId();
                } else if (Constant.MAJOR.equals(contentType)) {
                    if(promotionPattern.getBanner() != null){
                        campaignId = "cg_"+promotionPattern.getBanner().getId();
                    }
                } else if (Constant.GROUPING.equals(contentType)) {
                    if(promotionPattern.getBanner() != null){
                        campaignId = "g_"+promotionPattern.getBanner().getId();
                    }
                }else {
                    if(promotionPattern.getBanner() != null){
                        promotionPattern.getBanner().getId();
                    }
                }

                detail.setSeqNo(seqNo);
                detail.setShelfId(itemMapping);
                detail.setContentType(contentType);
                detail.setCampaignId(campaignId);
                String campaignCode = "";

                if (promotionPattern.getPromotionCriteriaGroup() != null &&
                        !promotionPattern.getPromotionCriteriaGroup().isEmpty() &&
                        promotionPattern.getPromotionCriteriaGroup().get(0) != null &&
                        promotionPattern.getPromotionCriteriaGroup().get(0).getId() != null) {

                    campaignCode = promotionPattern.getPromotionCriteriaGroup().get(0).getId();
                }
                detail.setCampaignCode(campaignCode);

                if (promotionPattern != null) {
                    String description = "";
                    if (Constant.DEAL.equals(contentType)) {
                        description = promotionPattern.getDescription();
                    } else if (promotionPattern.getBanner() != null && promotionPattern.getBanner().getDescription() != null) {
                        description = checkStrintNotPhoto(promotionPattern.getBanner().getDescription());
                    }
                    detail.setCampaignDescription(description);

                    if (promotionPattern.getValidFor() != null) {
                        detail.setCampaignExpireDate(promotionPattern.getValidFor().getEndDateTime());
                    }
                }

                String regularPoint = "0";
                String offerPoint = "0";

                if (promotionPattern.getPromotionCriteriaGroup() != null && !promotionPattern.getPromotionCriteriaGroup().isEmpty()) {
                    ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteriaGroup firstGroup = promotionPattern.getPromotionCriteriaGroup().get(0);
                    String relationType = firstGroup.getRelationTypeInGroup();
                    List<ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteria> criteriaList = firstGroup.getPromotionCriteria();

                    log.info("CampaignId : {}, Relation Type In Group : {}", promotionPattern.getId(), relationType);
                    log.info("PromotionCriteria ::: {}", gson.toJson(criteriaList));

                    if (criteriaList != null && !criteriaList.isEmpty()) {
                        for (ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteria pc : criteriaList) {
                            String para = pc.getCriteriaPara();
                            String value = pc.getCriteriaValue();

                            if ("On Going".equals(relationType)) {
                                if ("CoinsAmount".equals(para)) {
                                    regularPoint = value;
                                }
                            } else {
                                if ("OriginalCoinsAmount".equals(para)) {
                                    regularPoint = value;
                                } else if ("CoinsAmount".equals(para)) {
                                    offerPoint = value;
                                }
                            }
                        }
                    }
                }

                detail.setRegularPoint(regularPoint);
                detail.setOfferPoint(offerPoint);

                ThumbnailListRsp thumbnail = new ThumbnailListRsp();
                String thumbnail3x2 = null;

                if (Constant.DEAL.equals(contentType)) {
                    if (promotionPattern.getPromotionCriteriaGroup() != null &&
                            !promotionPattern.getPromotionCriteriaGroup().isEmpty() &&
                            promotionPattern.getPromotionCriteriaGroup().get(0) != null &&
                            promotionPattern.getPromotionCriteriaGroup().get(0).getHref() != null) {

                        thumbnail3x2 = promotionPattern.getPromotionCriteriaGroup().get(0).getHref();
                    }
                } else {
                    if (promotionPattern.getBanner() != null &&
                            promotionPattern.getBanner().getHref() != null) {

                        thumbnail3x2 = promotionPattern.getBanner().getHref();
                    }
                }

                thumbnail.setThumbnail3x2(thumbnail3x2);
                detail.setThumbnailList(thumbnail);

                String campaignName = null;

                if (Constant.DEAL.equals(contentType)) {
                    if (promotionPattern.getRelatedParty() != null &&
                            !promotionPattern.getRelatedParty().isEmpty() &&
                            promotionPattern.getRelatedParty().get(0) != null) {

                        campaignName = promotionPattern.getRelatedParty().get(0).getName();
                    }
                } else {
                    if (promotionPattern.getBanner() != null) {
                        campaignName = promotionPattern.getBanner().getName();
                    }
                }

                detail.setCampaignName(campaignName);

                String type = null;
                if ("1".equals(promotionPattern.getType())) {
                    type = "dtac reward";
                } else if ("2".equals(promotionPattern.getType())) {
                    type = "coin";
                }
                detail.setCampaignType(type);

                List<String> resultCardType = null;

                if (promotionPattern.getPromotionCriteriaGroup() != null && !promotionPattern.getPromotionCriteriaGroup().isEmpty()) {
                    String relationTypeInGroup = promotionPattern.getPromotionCriteriaGroup().get(0).getRelationTypeInGroup();

                    if (relationTypeInGroup != null) {
                        switch (relationTypeInGroup) {
                            case "Welcome" -> resultCardType = Arrays.asList("welcome", "silver", "gold", "platinum_blue");
                            case "Silver" -> resultCardType = Arrays.asList("silver", "gold", "platinum_blue");
                            case "Gold" -> resultCardType = Arrays.asList("gold", "platinum_blue");
                            case "Platinum Blue" -> resultCardType = Arrays.asList("platinum_blue");
                            case "On Going" -> resultCardType = Arrays.asList("no card");
                        }
                    }
                }
                detail.setCardType(resultCardType);

                dealLists.add(detail);
            }
            if (dealLists != null && !dealLists.isEmpty()) {
                seqNo = dealLists.get(dealLists.size() - 1).getSeqNo();
            }
        }else{
            dealLists = null;
        }

        return dealLists;
    }

    private String checkStrintNotPhoto(String text){
        String textString = text;
        if(text != null){
            String sub = text.substring(text.length()-4);
            if(".jpg".equals(sub)|| "jpeg".equals(sub) || ".png".equals(sub)){
                textString = "";
            }
        }
        return textString;
    }

    private EndpointResultRWD fetchShelfContent(Map<String, Object> tv, String itemMapping, String lang, Integer maxRow) throws Exception {
        log.info("fetchShelfContent");
        tv.put("country", "th");
        tv.put("lang", lang);
        tv.put("expand", "privilege_list");
        tv.put("expand_limit", maxRow);
        tv.put("fields", "show_redeem_button,privilege_list,card_type,campaign_type,sub_campaign_type,campaign_code,detail,redeem_point,thumb_list,expire_date,info,allow_app,setting,term_and_condition");
        tv.put("cms_id", itemMapping);
        shelfContentEndpoint.getShelfContentApi(tv);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

    private EndpointResultRWD fetchTrueShelfInquiryShelfDetail(Map<String, Object> tv, String itemMapping, String lang, String brand, Integer maxRow) throws Exception {
        log.info("fetchTrueShelfInquiryShelfDetail");
        tv.put("country", "th");
        tv.put("lang", lang);
        tv.put("limit", maxRow);
        tv.put("fields", "how_redeem_button,thumb_list,card_type,campaign_type,sub_campaign_type,campaign_code,redeem_point,expire_date,detail,term_and_condition,info,allow_app,setting");
        tv.put("cms_id", itemMapping);
        tv.put("channel", apigwUtill.isDtac(brand)? "dtac":"true");
        shelfTrueInquiryShelfDetailEndpoint.getShelfInquiryShelfDetail(tv);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

    private Map<String, Object> mapQueryDtacSpecialShelfInquiryShelfDetail(Map<String, Object> tv, String lang, String brand, String listMode, String productType, String customerNumber, String custGrade) throws Exception {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("lang", Objects.equals(lang, Constant.TH) ? "T" : "E");
        queryParams.put("id", apigwUtill.generateRewardBackendId());
        queryParams.put("phoneNumber", apigwUtill.encryptPhoneNoToApiGw(tv));
        queryParams.put("relatedParty.type", rewardUtill.getShortProductTypeForDtac(productType));
        queryParams.put("channel", apigwUtill.isDtac(brand)? "dtac":"true");
        queryParams.put("shelfType", 1);
        queryParams.put("listType", 3);
        queryParams.put("type", custGrade.replace("_", "%20"));//segment decrypt from FE
        queryParams.put("customerNumber", customerNumber);
        queryParams.put("listMode", listMode);

//        queryParams.put("lang", Objects.equals(lang, Constant.TH) ? "T" : "E");
//        queryParams.put("id", apigwUtill.generateRewardBackendId());
//        queryParams.put("phoneNumber", "WNiOoIsPThXEeZqoIqt02qPsgRRqc2voAMlOWXnanZQ%3D");
//        queryParams.put("relatedParty.type", "P");
//        queryParams.put("channel", apigwUtill.isDtac(brand)? "dtac":"true");
//        queryParams.put("shelfType", 1);
//        queryParams.put("listType", 3);
//        queryParams.put("type", "Gold");//segment decrypt from FE
//        queryParams.put("customerNumber", "626162838");
//        queryParams.put("listMode", "1");
//        System.out.println("::: "+gson.toJson(queryParams));

        return queryParams;
    }

    private Map<String, Object> mapQueryDtacRecommenedShelfInquiryShelfDetail(Map<String, Object> tv, String lang, String brand, String listMode, String productType, String customerNumber, String custGrade) throws Exception {
        Map<String, Object> queryParams = new HashMap<>();
//        queryParams.put("lang", Objects.equals(lang, Constant.TH) ? "T" : "E");
//        queryParams.put("channel", apigwUtill.isDtac(brand)? "dtac":"true");
//        queryParams.put("id", apigwUtill.generateRewardBackendId());
//        queryParams.put("phoneNumber", apigwUtill.encryptPhoneNoToApiGw(tv));
//        queryParams.put("customerNumber", customerNumber);
//        queryParams.put("type", custGrade.replace("_", "%20"));//segment decrypt from FE
//        queryParams.put("shelfType", 1);
//        queryParams.put("listType", 3);
//        queryParams.put("relatedParty.type", rewardUtill.getShortProductTypeForDtac(productType));
//        queryParams.put("relatedParty.description", "P");

        queryParams.put("lang", Objects.equals(lang, Constant.TH) ? "T" : "E");
        queryParams.put("channel", apigwUtill.isDtac(brand)? "dtac":"true");
        queryParams.put("id", apigwUtill.generateRewardRedeemBackendId("RWDNEWAPP_SHELFB2B_"));
        queryParams.put("phoneNumber", "WNiOoIsPThXEeZqoIqt02qPsgRRqc2voAMlOWXnanZQ%3D");
        queryParams.put("customerNumber", "626162838");
        queryParams.put("type", "Gold");
        queryParams.put("shelfType", 1);
        queryParams.put("listType", 3);
        queryParams.put("relatedParty.type", "P");
        queryParams.put("relatedParty.description", "1");
        System.out.println("::: "+gson.toJson(queryParams));

        return queryParams;
    }

    private EndpointResultRWD fetchDtacShelfInquiryShelfDetail(Map<String, Object> tv, Map<String, Object> queryParams) throws Exception {
        log.info("fetchDtacShelfInquiryShelfDetail");
        shelfSectionDetailDtacInquiryShelfDetailEndpoint.getShelfDtacInquiryShelfDetail(tv, queryParams);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

    private EndpointResultRWD redisShelfDtacInquiryShelfDetail(Map<String, Object> tv, String sectionId, String lang, String itemMapping, Map<String, Object> queryParams) throws Exception {
        log.info("redisShelfDtacInquiryShelfDetail");
        EndpointResult endpointResult = null;
        EndpointResultRWD endpointResult2 = new EndpointResultRWD();

        String redisKey = sectionId+":rawdata:getSectionDetail:"+itemMapping+":"+lang;

//        ShelfSectionDetailDtacInquiryShelfDetailRsp dataDetails = redisCacheService.get(redisKey,ShelfSectionDetailDtacInquiryShelfDetailRsp.class);

//        if(dataDetails == null){
            log.info("Get InquiryShelfDetail Service. : "+ sectionId);
            endpointResult2 = fetchDtacShelfInquiryShelfDetail(tv, queryParams);

            if(endpointResult2.getHttpStatus() == 200){
                log.info("put redis key: "+ redisKey +", rawdata: "+gson.toJson((ShelfSectionDetailDtacInquiryShelfDetailRsp) tv.get("GetShelfDtacInquiryShelfDetail")));
                redisCacheService.putExpireRedis(redisKey, (ShelfSectionDetailDtacInquiryShelfDetailRsp) tv.get("GetShelfDtacInquiryShelfDetail"), Long.valueOf("3600"));// ใช้จริง 3600s
            }

//        }else{
//            log.info("Get InquiryShelfDetail redis. : "+ sectionId);
//            endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
//            endpointResult2 = errorService.convertMapResult(endpointResult);
//            tv.put("GetShelfDtacInquiryShelfDetail", dataDetails);
//        }
        tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);

        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

    private String mapListMode(String itemMapping){

        String listMode = null;

        switch (itemMapping){
            case "1":
                listMode = "For You";
                break;
            case "3":
                listMode = "Free";
                break;
            case "4":
                listMode = "Discount";
                break;
            case "5":
                listMode = "Voice & Net";
                break;
            case "6":
                listMode = "Brand";
                break;
            case "7":
                listMode = "7Eleven";
                break;
            case "8":
                listMode = "Makro";
                break;
            case "9":
                listMode = "Lotus";
                break;
        }

        return listMode;
    }

}
