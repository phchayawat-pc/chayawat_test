package th.co.truecorp.commonapi.reward.service;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSectionHighlight;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSystemConfig;
import th.co.truecorp.commonapi.reward.cms.jpa.service.*;
import th.co.truecorp.commonapi.reward.common.endpoint.CommonServiceEndpoint;
import th.co.truecorp.commonapi.reward.common.endpoint.CommonServiceProfileEndpoint;
import th.co.truecorp.commonapi.reward.common.model.GetDigitalByDigitalIdResponse;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.endpoint.*;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import java.util.*;

@Service
public class ShelfSectionNoneRelatedService {

    private static Logger log = LoggerFactory.getLogger(ShelfSectionNoneRelatedService.class);

    @Autowired
    private ResultService resultService;

    @Autowired
    private APIGWUtill apigwUtill;

    @Autowired
    private RewardUtill rewardUtill;

    @Autowired
    private RwdSystemConfigService rwdSystemConfigService;

    @Autowired
    private RwdSectionHighlightService rwdSectionHighlightService;

    @Autowired
    private ShelfContentEndpoint shelfContentEndpoint;

    @Autowired
    private ShelfTrueInquiryShelfDetailEndpoint shelfTrueInquiryShelfDetailEndpoint;

    @Autowired
    private ErrorService errorService;

    @Autowired
    private CommonServiceProfileEndpoint commonServiceProfileEndpoint;

    @Autowired
    private CommonServiceEndpoint commonServiceEndpoint;

    @Autowired
    private ShelfSectionDetailDtacInquiryShelfDetailEndpoint shelfSectionDetailDtacInquiryShelfDetailEndpoint;


    @EndpointLog (name = "TRUEAPP.GetSectionNoneRelated")
    public EndpointResult getSectionNoneRelated(Map<String, Object> tv) throws Exception {

        EndpointResult endpointResult = null;
        ShelfSectionNoneRelatedRsp shelfSectionNoneRelatedRsp = new ShelfSectionNoneRelatedRsp();
        tv.put(ComnConst.KEY_LANGUAGE, rewardUtill.handleLanguage(tv.get(ComnConst.KEY_LANGUAGE).toString()));

        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
        String brand = tv.get("brand").toString();
        String sectionId = tv.get("sectionId") != null ? tv.get("sectionId").toString().toUpperCase() : "";
        String useCmsContent = tv.get("useCmsContent") != null ? tv.get("useCmsContent").toString().toUpperCase() : "";
        String productType = tv.get("productType") != null ? tv.get("productType").toString():"";

        Integer seqNo = 0;
        try {
            log.info("RwdSystemConfig findConfigs : MAX_SLIDE : FIX");
            Optional<RwdSystemConfig> optional = rwdSystemConfigService.findConfigs("MAX_SLIDE","FIX");
            RwdSystemConfig systemConfig = optional.get();
            Integer maxRow = Integer.valueOf(systemConfig.getValue());
            if(!useCmsContent.equals("Y")){
                log.info("useCmsContent : Y : " + sectionId);
                List<RwdSectionHighlight> sectionHighlights = rwdSectionHighlightService.findSectionIdDefault(sectionId);
                shelfSectionNoneRelatedRsp = mapShelfSectionNoneRelatedRsp(tv, sectionId, sectionHighlights);

                if(shelfSectionNoneRelatedRsp.getSectionItem() != null && shelfSectionNoneRelatedRsp.getSectionItem().size() > 0){
                    log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + "is success");
                    endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                }else{
                    log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + "is not success");
                    EndpointResultRWD endpointResultRwd = new EndpointResultRWD();
                    endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
                            apigwUtill.isDtac(brand) ? "DTAC" : "TRUE",
                            Objects.requireNonNull(Constant.ERROR_DATA_NOT_FOUND),
                            tv.get(ComnConst.KEY_LANGUAGE).toString(),
                            "data not found",
                            Constant.N_A,
                            Constant.MESSAGE);
                    endpointResult = errorService.revertMapResult(endpointResultRwd);
                }

            }else{
                log.info("RwdSystemConfig findConfigCode : TEMPLATE_DEFAULT");

                List<RwdSystemConfig> rwdSystemConfig = rwdSystemConfigService.findConfigTemplateDefault("TEMPLATE_DEFAULT");
                log.info("RwdSectionHighlight findSectionIdDefault : "+sectionId);
                List<RwdSectionHighlight> sectionHighlights2 = rwdSectionHighlightService.findSectionIdDefault(sectionId);
                for(RwdSectionHighlight shl : sectionHighlights2){
                    String itemMapping = shl.getItemMapping();
                    String itemTypeCode = shl.getItemTypeCode();

                    if(itemTypeCode.equals("MERCHANT")){
                        log.info("Shelf MERCHANT");
                        endpointResult = errorService.revertMapResult(fetchShelfContent(tv, itemMapping, lang.toLowerCase(), maxRow));
                        List<ShelfContentDataApiRsp.ContentData.DataDetails> privilegeList = (List<ShelfContentDataApiRsp.ContentData.DataDetails>) tv.get("GetContent");
                        shelfSectionNoneRelatedRsp = mapContentToShelfSectionNoneRelatedRsp(tv, seqNo, rwdSystemConfig, privilegeList);
                    }else if(itemTypeCode.equals("SHELF")){
                        log.info("Shelf SHELF");
                        String shelfTypeCode = shl.getShelfTypeCode();
                        if(shelfTypeCode.equals("NORMAL_TRUE")){
                            log.info("Shelf NORMAL_TRUE");
                            endpointResult = errorService.revertMapResult(fetchTrueShelfInquiryShelfDetail(tv, itemMapping, lang, "true", maxRow));
                            List<ShelfContentDataApiRsp.ContentData.DataDetails> shelfItemsList = (List<ShelfContentDataApiRsp.ContentData.DataDetails>) tv.get("GetShelfInquiryShelfDetail");
                            shelfSectionNoneRelatedRsp = mapInquiryShelfToShelfSectionNoneRelatedRsp(tv, seqNo, rwdSystemConfig, shelfItemsList);
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
                                endpointResult = errorService.revertMapResult(fetchDtacShelfInquiryShelfDetail(tv, queryParams));
                                ShelfSectionDetailDtacInquiryShelfDetailRsp detailRsp = (ShelfSectionDetailDtacInquiryShelfDetailRsp) tv.get("GetShelfDtacInquiryShelfDetail");

                              if (detailRsp != null) {
                                  shelfSectionNoneRelatedRsp = mapInquiryToShelfSPECIALSectionNoneRelatedRsp(tv, seqNo, rwdSystemConfig, detailRsp);
                              }
                            }

                        }else if ("RECOMMENED".equals(shelfTypeCode)) {
                            log.info("SHELF: RECOMMENED");
                        }
                    }
                }

                if(endpointResult.getHttpStatus() ==200){
                    if(shelfSectionNoneRelatedRsp.getSectionItem() != null && shelfSectionNoneRelatedRsp.getSectionItem().size() > 0){
                        log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + "is success");
                        endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                    }else{
                        log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + "is not success");
                        EndpointResultRWD endpointResultRwd = new EndpointResultRWD();
                        endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
                                apigwUtill.isDtac(brand) ? "DTAC" : "TRUE",
                                Objects.requireNonNull(Constant.ERROR_DATA_NOT_FOUND),
                                tv.get(ComnConst.KEY_LANGUAGE).toString(),
                                "data not found",
                                Constant.N_A,
                                Constant.MESSAGE);
                        endpointResult = errorService.revertMapResult(endpointResultRwd);
                    }
                }
            }
            tv.put(Constant.TRANSACTION_RESPONSE_KEY,shelfSectionNoneRelatedRsp);

            log.info("endpointResult : "+endpointResult);
        } catch (Exception e) {
            log.info("error is " + e.getMessage());
            endpointResult = resultService.getEndpointExceptionResult(tv, e);
            return endpointResult;
        }

        return endpointResult;
    }

    private ShelfSectionNoneRelatedRsp mapShelfSectionNoneRelatedRsp(Map<String, Object> tv, String sectionId, List<RwdSectionHighlight> results){
        ShelfSectionNoneRelatedRsp rsp = new ShelfSectionNoneRelatedRsp();
        List<ShelfSectionNoneRelatedRsp.SectionItemDetailRsp> details = new ArrayList<ShelfSectionNoneRelatedRsp.SectionItemDetailRsp>();
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();

        rsp.setSectionId(sectionId);
        rsp.setLang(lang);
        rsp.setDisplayTypeCode(tv.get("displayTypeCode") != null ? tv.get("displayTypeCode").toString().toUpperCase() : "");
        if(!results.isEmpty()){
            rsp.setSectionId(results.get(0).getSectionId());
            for(RwdSectionHighlight result : results){
                details.add(mapShelfLoyoutDetailRsp(result));
            }
        }
        rsp.setSectionItem(details);

        return rsp;
    }

    private ShelfSectionNoneRelatedRsp.SectionItemDetailRsp mapShelfLoyoutDetailRsp(RwdSectionHighlight result){
        ShelfSectionNoneRelatedRsp.SectionItemDetailRsp detail = new ShelfSectionNoneRelatedRsp.SectionItemDetailRsp();
        if(result != null){
            detail.setItemNo(result.getSeqNo());
            detail.setItemName((result.getItemMapping() != null) ? result.getItemMapping() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
            detail.setItemDisplayName(Constant.DEFAULT_NULL_EXCEPTION_VALUE);
            detail.setItemType((result.getItemTypeCode() != null) ? result.getItemTypeCode() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
            detail.setItemSubtype((result.getItemSubtype() != null) ? result.getItemSubtype() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
            detail.setShelfType((result.getShelfTypeCode() != null) ? result.getShelfTypeCode() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
            detail.setItemMapping((result.getItemMapping() != null) ? result.getItemMapping() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
            detail.setItemMapping2((result.getItemMapping2() != null) ? result.getItemMapping2() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
            ShelfSectionNoneRelatedRsp.SectionItemDetailRsp.SectionItemImageDetailRsp itemImage = new ShelfSectionNoneRelatedRsp.SectionItemDetailRsp.SectionItemImageDetailRsp();
            itemImage.setImage1x1((result.getItemImage1x1() != null) ? result.getItemImage1x1() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
            itemImage.setImage4x3((result.getItemImage4x3() != null) ? result.getItemImage4x3() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
            itemImage.setImage9x16((result.getItemImage9x16() != null) ? result.getItemImage9x16() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
            itemImage.setImage16x9((result.getItemImage16x9() != null) ? result.getItemImage16x9() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
            detail.setItemImageList(itemImage);
        }
        return detail;
    }

    private ShelfSectionNoneRelatedRsp mapContentToShelfSectionNoneRelatedRsp(Map<String, Object> tv, Integer seqNo, List<RwdSystemConfig> rwdSystemConfig, List<ShelfContentDataApiRsp.ContentData.DataDetails> privilegeList){
        log.info("map Content To ShelfSectionNoneRelatedRsp");
        ShelfSectionNoneRelatedRsp rsp = new ShelfSectionNoneRelatedRsp();
        List<ShelfSectionNoneRelatedRsp.SectionItemDetailRsp> details = new ArrayList<ShelfSectionNoneRelatedRsp.SectionItemDetailRsp>();
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
        String sectionId = tv.get("sectionId") != null ? tv.get("sectionId").toString().toUpperCase() : "";

        rsp.setSectionId(sectionId);
        rsp.setLang(lang);
        rsp.setDisplayTypeCode(tv.get("displayTypeCode").toString());

        if(privilegeList != null && !privilegeList.isEmpty()){
            for(ShelfContentDataApiRsp.ContentData.DataDetails privilege:privilegeList){
                seqNo++;
                ShelfSectionNoneRelatedRsp.SectionItemDetailRsp detail = new ShelfSectionNoneRelatedRsp.SectionItemDetailRsp();

                ShelfSectionNoneRelatedRsp.SectionItemDetailRsp.SectionItemImageDetailRsp itemImageList = new ShelfSectionNoneRelatedRsp.SectionItemDetailRsp.SectionItemImageDetailRsp();
                itemImageList.setImage16x9(privilege.getThumb_list() != null ? privilege.getThumb_list().getHighlight16x9() != null ? privilege.getThumb_list().getHighlight16x9() : Constant.DEFAULT_NULL_EXCEPTION_VALUE : Constant.DEFAULT_NULL_EXCEPTION_VALUE);

                detail.setItemNo(seqNo);
                detail.setItemName(privilege.getId());
                detail.setItemDisplayName(lang.toUpperCase().equals(Constant.TH) ? privilege.getInfo().getMerchant_name_th() : privilege.getInfo().getMerchant_name_en());
                detail.setItemImageList(itemImageList);
                detail.setItemType(mapItemType(privilege));
                detail.setItemSubtype(mapItemSubtype(detail.getItemType(), rwdSystemConfig));
                detail.setItemMapping(privilege.getId());

                details.add(detail);
            }
        }else{
            details = null;
        }
        rsp.setSectionItem(details);

        return rsp;
    }

    private ShelfSectionNoneRelatedRsp mapInquiryShelfToShelfSectionNoneRelatedRsp(Map<String, Object> tv, Integer seqNo, List<RwdSystemConfig> rwdSystemConfig, List<ShelfContentDataApiRsp.ContentData.DataDetails> shelfItemsList){
        log.info("map InquiryShelf To ShelfSectionNoneRelatedRsp");
        ShelfSectionNoneRelatedRsp rsp = new ShelfSectionNoneRelatedRsp();
        List<ShelfSectionNoneRelatedRsp.SectionItemDetailRsp> details = new ArrayList<ShelfSectionNoneRelatedRsp.SectionItemDetailRsp>();
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
        String sectionId = tv.get("sectionId") != null ? tv.get("sectionId").toString().toUpperCase() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;

        rsp.setSectionId(sectionId);
        rsp.setLang(tv.get(ComnConst.KEY_LANGUAGE).toString());
        rsp.setDisplayTypeCode(tv.get("displayTypeCode").toString());
        if(shelfItemsList != null && shelfItemsList.size() > 0){
            for(ShelfContentDataApiRsp.ContentData.DataDetails shelfItems:shelfItemsList){
                seqNo++;
                ShelfSectionNoneRelatedRsp.SectionItemDetailRsp detail = new ShelfSectionNoneRelatedRsp.SectionItemDetailRsp();

                ShelfSectionNoneRelatedRsp.SectionItemDetailRsp.SectionItemImageDetailRsp itemImageList = new ShelfSectionNoneRelatedRsp.SectionItemDetailRsp.SectionItemImageDetailRsp();
                itemImageList.setImage16x9(shelfItems.getThumb_list() != null ? shelfItems.getThumb_list().getHighlight16x9() != null ? shelfItems.getThumb_list().getHighlight16x9() : Constant.DEFAULT_NULL_EXCEPTION_VALUE : Constant.DEFAULT_NULL_EXCEPTION_VALUE);

                detail.setItemNo(seqNo);
                detail.setItemName(shelfItems.getId());
                String displayName = Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                if(shelfItems.getInfo() != null){
                    displayName = lang.equalsIgnoreCase(Constant.TH) ? shelfItems.getInfo().getMerchant_name_th() : shelfItems.getInfo().getMerchant_name_en();
                }
                detail.setItemDisplayName(displayName);
                detail.setItemImageList(itemImageList);
                detail.setItemType(mapItemType(shelfItems));
                detail.setItemSubtype(mapItemSubtype(detail.getItemType(), rwdSystemConfig));
                detail.setItemMapping(shelfItems.getId());

                details.add(detail);
            }
        }else{
            details = null;
        }
        rsp.setSectionItem(details);

        return rsp;
    }

    private String mapItemType(ShelfContentDataApiRsp.ContentData.DataDetails shelfItems){
        String itemType = Constant.DEFAULT_NULL_EXCEPTION_VALUE;

        if(shelfItems.getContent_type() != null){
            String Content_type = shelfItems.getContent_type().toLowerCase();
            if(Content_type.equals("privilege")){
                itemType = "DEAL";
            }else if(Content_type.equals("trueyoumerchant")){
                itemType = "MERCHANT";
            }else if(Content_type.equals("trueyouarticle")){
                if(shelfItems.getSetting() != null && shelfItems.getSetting().getThematic_main_shelf_ids() != null && !shelfItems.getSetting().getThematic_main_shelf_ids().isEmpty()){
                    itemType = "THEMATIC";
                }else{
                    itemType = "ARTICLE";
                }
            }
        }

        return itemType;
    }

    private String mapItemSubtype(String itemType , List<RwdSystemConfig> systemConfigList){

        String itemSubtype = Constant.DEFAULT_NULL_EXCEPTION_VALUE;
        if(itemType != null){
            for(RwdSystemConfig systemConfig:systemConfigList){
                if(itemType.equals(systemConfig.getConfigGroup())){
                    itemSubtype = systemConfig.getValue();
                }
            }
        }

        return itemSubtype;
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

        return queryParams;
    }

    private EndpointResultRWD fetchDtacShelfInquiryShelfDetail(Map<String, Object> tv, Map<String, Object> queryParams) throws Exception {
        log.info("fetchDtacShelfInquiryShelfDetail");
        shelfSectionDetailDtacInquiryShelfDetailEndpoint.getShelfDtacInquiryShelfDetail(tv, queryParams);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

    private ShelfSectionNoneRelatedRsp mapInquiryToShelfSPECIALSectionNoneRelatedRsp(Map<String, Object> tv, Integer seqNo, List<RwdSystemConfig> rwdSystemConfig, ShelfSectionDetailDtacInquiryShelfDetailRsp detailRsp){
        ShelfSectionNoneRelatedRsp rsp = new ShelfSectionNoneRelatedRsp();
        List<ShelfSectionNoneRelatedRsp.SectionItemDetailRsp> details = new ArrayList<ShelfSectionNoneRelatedRsp.SectionItemDetailRsp>();
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
        String sectionId = tv.get("sectionId") != null ? tv.get("sectionId").toString().toUpperCase() : "";

        rsp.setSectionId(sectionId);
        rsp.setLang(tv.get(ComnConst.KEY_LANGUAGE).toString());
        rsp.setDisplayTypeCode(tv.get("displayTypeCode").toString());

        if(detailRsp != null){
            for(ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionPattern promotionPattern:detailRsp.getPromotionPattern()){
                seqNo++;
                ShelfSectionNoneRelatedRsp.SectionItemDetailRsp detail = new ShelfSectionNoneRelatedRsp.SectionItemDetailRsp();

                String itemType = mapItemTypeSPECIAL(promotionPattern);

                String itemName = mapItemNameSPECIAL(promotionPattern,itemType);

                ShelfSectionNoneRelatedRsp.SectionItemDetailRsp.SectionItemImageDetailRsp itemImageList = new ShelfSectionNoneRelatedRsp.SectionItemDetailRsp.SectionItemImageDetailRsp();
                itemImageList.setImage3x2(mapImage3x2SPECIAL(promotionPattern,itemType));

                detail.setItemNo(seqNo);
                detail.setItemName(itemName);
                detail.setItemDisplayName(mapItemDisplayNameSPECIAL(promotionPattern,itemType));
                detail.setItemImageList(itemImageList);
                detail.setItemType(itemType);
                detail.setItemSubtype(mapItemSubtype(detail.getItemType(), rwdSystemConfig));
                detail.setItemMapping(itemName);

                details.add(detail);
            }
        }else{
            details = null;
        }
        rsp.setSectionItem(details);

        return rsp;
    }



    private String mapItemTypeSPECIAL(ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionPattern promotionPattern){
        String itemType = Constant.DEFAULT_NULL_EXCEPTION_VALUE;

        if (promotionPattern.getId() != null && !promotionPattern.getId().equals("")) {
            log.info("Promotion ID is not null or empty. Setting contentType to DEAL.");
            itemType = Constant.DEAL;
        } else if(promotionPattern.getBanner() != null){
            log.info("Promotion ID is null or empty. Banner type: " + promotionPattern.getBanner().getType());
            switch (promotionPattern.getBanner().getType()) {
                case "2":
                    log.info("Banner type is '2'. Setting contentType to MAJOR.");
                    itemType = Constant.MAJOR;
                    break;
                case "7":
                    log.info("Banner type is '7'. Setting contentType to GROUPING.");
                    itemType = Constant.GROUPING;
                    break;
                case "8":
                    log.info("Banner type is '8'. Setting contentType to FESTIVE.");
                    itemType = Constant.FESTIVE;
                    break;
            }
        }

        return itemType;
    }

    private String mapItemNameSPECIAL(ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionPattern promotionPattern , String itemType){
        String itemName = Constant.DEFAULT_NULL_EXCEPTION_VALUE;

        if(promotionPattern.getId() != null && !promotionPattern.getId().equals("")){
            itemName = promotionPattern.getId();
        }else if(itemType.equals(Constant.GROUPING) && promotionPattern.getBanner() != null){
            itemName = "g_"+promotionPattern.getBanner().getId();
        }else if(itemType.equals(Constant.MAJOR) && promotionPattern.getBanner() != null){
            itemName = "cg_"+promotionPattern.getBanner().getId();
        }

        return itemName;
    }

    private String mapImage3x2SPECIAL(ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionPattern promotionPattern , String itemType){
        String image3x2 = Constant.DEFAULT_NULL_EXCEPTION_VALUE;

        if(promotionPattern.getId() != null && !promotionPattern.getId().equals("")){
            image3x2 = promotionPattern.getPromotionCriteriaGroup()!=null ? promotionPattern.getPromotionCriteriaGroup().get(0).getHref() : null;
        }else if(itemType.equals(Constant.GROUPING) && promotionPattern.getBanner() != null){
            image3x2 = promotionPattern.getBanner().getHref();
        }else if(itemType.equals(Constant.MAJOR) && promotionPattern.getBanner() != null){
            image3x2 = promotionPattern.getBanner().getHref();
        }

        return image3x2;
    }

    private String mapItemDisplayNameSPECIAL(ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionPattern promotionPattern , String itemType){
        String itemDisplayName = Constant.DEFAULT_NULL_EXCEPTION_VALUE;

        if(promotionPattern.getId() != null && !promotionPattern.getId().equals("")){
            itemDisplayName = promotionPattern.getRelatedParty().get(0).getName() != null ? promotionPattern.getRelatedParty().get(0).getName() : null;
        }else if(itemType.equals(Constant.GROUPING) && promotionPattern.getBanner() != null){
            itemDisplayName = promotionPattern.getBanner().getName();
        }else if(itemType.equals(Constant.MAJOR) && promotionPattern.getBanner() != null){
            itemDisplayName = promotionPattern.getBanner().getName();
        }

        return itemDisplayName;
    }

}
