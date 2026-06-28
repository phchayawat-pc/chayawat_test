package th.co.truecorp.commonapi.reward.service;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSystemConfig;
import th.co.truecorp.commonapi.reward.cms.jpa.service.*;
import th.co.truecorp.commonapi.reward.common.endpoint.CommonServiceProfileAndRewardEndpoint;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.dto.*;
import th.co.truecorp.commonapi.reward.endpoint.*;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.model.EndpointResult;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShelfTemplateHighlightService {

    private static Logger log = LoggerFactory.getLogger(ShelfTemplateHighlightService.class);

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
    private RwdSectionHighlightService rwdSectionHighlightService;

    @Autowired
    private ShelfContentEndpoint shelfContentEndpoint;

    @Autowired
    private ShelfTrueInquiryShelfDetailEndpoint shelfTrueInquiryShelfDetailEndpoint;

    @Autowired
    private CommonServiceProfileAndRewardEndpoint commonServiceProfileAndRewardEndpoint;

    @Autowired
    private ShelfInquiryShelfDetailDtacEndpoint shelfInquiryShelfDetailDtacEndpoint;

    Gson gson = new Gson();

    @Autowired
    private ErrorService errorService;

    public EndpointResult getTemplateHighlight(Map<String, Object> tv) throws Exception {
        log.info("Get TemplateHighlight Service");
        EndpointResultRWD endpointResultRwd = new EndpointResultRWD();
        EndpointResult endpointResult = null;


        tv.put(ComnConst.KEY_LANGUAGE, rewardUtill.handleLanguage(tv.get(ComnConst.KEY_LANGUAGE)!=null
                ? tv.get(ComnConst.KEY_LANGUAGE).toString():""));
        String lang = tv.get(ComnConst.KEY_LANGUAGE)!=null? tv.get(ComnConst.KEY_LANGUAGE).toString():"";
        String brand = tv.get("brand")!=null? tv.get("brand").toString():"";
        String sectionId = tv.get("sectionId") != null ? tv.get("sectionId").toString() : "";
        String templateCode = tv.get("templateCode") != null ? tv.get("templateCode").toString() : "";
        String useCmsContent = tv.get("useCmsContent") != null ? tv.get("useCmsContent").toString().toUpperCase() : "";
        String productType = tv.get("productType") != null ? tv.get("productType").toString() : "";
        String shelfId = tv.get("shelfId") != null ? tv.get("shelfId").toString() : "";

        try {
            List<ShelfTemplateHighlightRsp.HighlightItem> highlightItems = new ArrayList<>();

            log.info("RwdSystemConfig findConfigs : MAX_SLIDE : FIX");
            Optional<RwdSystemConfig> optional = rwdSystemConfigService.findConfigs("MAX_SLIDE","FIX");
            RwdSystemConfig rwdSystemConfig = optional.get();
            Integer maxRow = Integer.valueOf(rwdSystemConfig.getValue());

            if(!useCmsContent.equals("Y")) {
                log.info("get RwdSectionHighlight  is useCmsContent:N ,sectionId:"+ sectionId);

                List<ShelfSectionHighlightDto> sectionHighlight = rwdSectionHighlightService.findShelfSectionHighlightDtoBySectionId(sectionId);
                highlightItems = mapHighlightItemCmsContent(sectionHighlight);

                if (highlightItems.isEmpty()) {
                    log.debug(Constant.ENDPOINT_SERVICE_GET_SHELF + " is failed");
                    endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
                            brand,
                            Objects.requireNonNull(Constant.ERROR_DATA_NOT_FOUND),
                            lang,
                            "data not found",
                            Constant.N_A,
                            Constant.MESSAGE);
                    endpointResult = errorService.revertMapResult(endpointResultRwd);
                } else if (!highlightItems.isEmpty()) {
                    log.debug(Constant.ENDPOINT_SERVICE_GET_SHELF + " is success");
                    endpointResultRwd = errorService.convertMapResult(resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA));
                    endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                }
            }else{
                log.info("get RwdSystemConfig is TEMPLATE_DEFAULT");
                List<RwdSystemConfig> rwdSystemConfigs = rwdSystemConfigService.findConfigTemplateDefault("TEMPLATE_DEFAULT");

                log.info("get RwdSectionHighlight is useCmsContent:Y ,sectionId:" + sectionId);
                List<ShelfSectionHighlightDto> sectionHighlights = new ArrayList<ShelfSectionHighlightDto>();

                if(!shelfId.equals("")){
                    if(apigwUtill.isDtac(brand)) {
                        log.info("DTAC Fetching ShelfSectionHighlightDto by sectionId and shelfId. : " + shelfId);
                        sectionHighlights = rwdSectionHighlightService.findShelfSectionHighlightDtoBySectionIdAndItemMapping(sectionId, shelfId);
                    }else {
                        log.info("TRUE Fetching ShelfSectionHighlightDto by sectionId and shelfId. : " + shelfId);
                        List<ShelfSectionDetailDto2> shelfSectionDetailDto = rwdSectionDetailService.findShelfSectionDetailDto2BySectionId(sectionId);
                        log.info("shelfSectionDetailDto size : " + shelfSectionDetailDto.size());
                        shelfSectionDetailDto = filterShelfSectionDetailDto2ToShelfId(shelfSectionDetailDto, shelfId);
                        log.info("filter " + shelfId + " shelfSectionDetailDto size : " + shelfSectionDetailDto.size());

                        if (!shelfSectionDetailDto.isEmpty() && shelfSectionDetailDto.size() > 0) {
                            for (ShelfSectionDetailDto2 dto : shelfSectionDetailDto) {
                                log.info("get row number : " + dto.getr_no());
                                Integer rNo = dto.getr_no();
                                List<ShelfSectionHighlightDto> sectionHighlight = rwdSectionHighlightService.findShelfSectionHighlightDtoBySectionIdAndRowNum(sectionId, rNo);
                                sectionHighlights.addAll(sectionHighlight);
                            }

                            if (sectionHighlights.isEmpty() || sectionHighlights.size() <= 0) {
                                log.info("row number : 1");
                                sectionHighlights = rwdSectionHighlightService.findShelfSectionHighlightDtoBySectionIdAndRowNum(sectionId, 1);
                            }
                        }
                    }
                }else{
                    log.info("Fetching ShelfSectionHighlightDto by sectionId.");
                    sectionHighlights = rwdSectionHighlightService.findShelfSectionHighlightDtoBySectionId(sectionId);
                }
                Integer itemNo = 0;
                if(sectionHighlights != null && !sectionHighlights.isEmpty()){
                    for(ShelfSectionHighlightDto sectionHighlight:sectionHighlights) {
                        String itemTypeCode = sectionHighlight.getitem_type_code();
                        String itemMapping = sectionHighlight.getitem_mapping();
                        if("MERCHANT".equals(itemTypeCode)){
                            log.info("MERCHANT");
                            endpointResultRwd = fetchShelfContent(tv, itemMapping, lang.toLowerCase(), maxRow);
                            endpointResult = errorService.revertMapResult(endpointResultRwd);
                            List<ShelfContentDataApiRsp.ContentData.DataDetails> privilegeList = (List<ShelfContentDataApiRsp.ContentData.DataDetails>) tv.get("GetContent");
                            List<ShelfTemplateHighlightRsp.HighlightItem> highlightItemList = mapHighlightItemByContent(tv, itemNo, rwdSystemConfigs, privilegeList);
                            highlightItems.addAll(highlightItemList);
                            if (highlightItems != null && !highlightItems.isEmpty()) {
                                itemNo = Integer.valueOf(highlightItems.get(highlightItems.size() - 1).getItemNo());
                            }
                        }else if("SHELF".equals(itemTypeCode)){
                            String shelfTypeCode = sectionHighlight.getshelf_type_code();
                            if("NORMAL_TRUE".equals(shelfTypeCode)){
                                log.info("NORMAL_TRUE");
                                endpointResultRwd = fetchTrueShelfInquiryShelfDetail(tv, itemMapping, lang, brand, maxRow);
                                endpointResult = errorService.revertMapResult(endpointResultRwd);
                                List<ShelfContentDataApiRsp.ContentData.DataDetails> shelfItemsList = (List<ShelfContentDataApiRsp.ContentData.DataDetails>) tv.get("GetShelfInquiryShelfDetail");
                                List<ShelfTemplateHighlightRsp.HighlightItem> highlightItemList = mapHighlightItemByInquiryShelf(tv, itemNo, rwdSystemConfigs, shelfItemsList);
                                highlightItems.addAll(highlightItemList);
                                if (highlightItems != null && !highlightItems.isEmpty()) {
                                    itemNo = Integer.valueOf(highlightItems.get(highlightItems.size() - 1).getItemNo());
                                }
                            } else if ("BANNER".equals(shelfTypeCode)) {
                                log.info("SHELF: BANNER");
                                String customerGrade = null;
                                String customerNumber = null;

                                endpointResult = commonServiceProfileAndRewardEndpoint.getCommonServiceProfileAndReward(tv);
                                ProfileAndRewardRsp profileAndRewardRsp = (ProfileAndRewardRsp) tv.get("profileAndRewardRspEndpoint");

                                if (profileAndRewardRsp.getCustomerProfileRsp() != null && profileAndRewardRsp.getServiceProfileRsp() != null) {
                                    log.info("commonProfileRsp data exist");
                                    customerGrade = profileAndRewardRsp.getCustomerProfileRsp().getCardType();
                                    log.info("customerGrade: " + customerGrade);
                                    customerNumber = profileAndRewardRsp.getServiceProfileRsp().getCustomerNumber();
                                    log.info("customerNumber: " + customerNumber);

                                    Map<String, Object> queryParams = mapQueryDtacSpecialShelfInquiryShelfDetail(tv, lang, brand, productType, customerNumber, customerGrade);
                                    endpointResult = errorService.revertMapResult(fetchDtacShelfInquiryShelfDetail(tv, queryParams));

                                    ShelfDtacInquiryShelfDetailApiRsp detailRsp = (ShelfDtacInquiryShelfDetailApiRsp) tv.get("GetShelfDtacInquiryShelfDetail");
                                    log.info("ShelfDtacInquiryShelfDetailApiRsp Pattern size : "+detailRsp.getPattern().size());
                                    if (detailRsp != null) {
                                        List<RwdSystemConfig> rwdSystemConfigsBanner = rwdSystemConfigService.findConfigGroupTemplateDefault(itemMapping);
                                        log.info("itemMapping RwdSystemConfig :: " + rwdSystemConfigsBanner);

                                        List<ShelfDtacInquiryShelfDetailApiRsp.Pattern> patterns = filterPatternType(detailRsp.getPattern());
                                        log.info("patterns size : "+ patterns.size());

                                        if (!rwdSystemConfigsBanner.isEmpty()) {
                                            for (RwdSystemConfig systemConfig : rwdSystemConfigsBanner) {
                                                String valueConfig = systemConfig.getValue();
                                                if (valueConfig != null && !valueConfig.equals("")) {
                                                    String[] valueConfigParts = valueConfig.split(",");
                                                    String catg = "";
                                                    String subCatg = "";
                                                    String type = "";
                                                    if (valueConfigParts.length == 2) {
                                                        if (!valueConfigParts[0].equals("-") && !valueConfigParts[1].equals("-")) {
                                                            catg = valueConfigParts[0];
                                                            subCatg = valueConfigParts[1];
                                                            String[] valueParts = subCatg.split("_");
                                                            if (valueParts.length == 2) {
                                                                subCatg = valueParts[0];
                                                                type = valueParts[1].equals("C") ? "2" : "";
                                                            }
                                                        } else if (!valueConfigParts[0].equals("-") && valueConfigParts[1].equals("-")) {
                                                            catg = valueConfigParts[0];
                                                        } else if (valueConfigParts[0].equals("-") && !valueConfigParts[1].equals("-")) {
                                                            subCatg = valueConfigParts[1];
                                                            String[] valueParts = subCatg.split("_");
                                                            if (valueParts.length == 2) {
                                                                subCatg = valueParts[0];
                                                                type = valueParts[1].equals("C") ? "2" : "";//C = type 2
                                                            }
                                                        }
                                                    }
                                                    if(patterns != null){
                                                        List<ShelfTemplateHighlightRsp.HighlightItem> highlightItemList = mapHighlightItemByInquiryShelfBanner(tv, itemNo, patterns, type, catg, subCatg);
                                                        highlightItems.addAll(highlightItemList);
                                                        if (highlightItems != null && highlightItems.size() > 0) {
                                                            itemNo = Integer.valueOf(highlightItems.get(highlightItems.size() - 1).getItemNo());
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }else{
                    log.debug("Get RwdSectionHighlight is failed");
                    endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
                            tv.get("brand")!=null? tv.get("brand").toString():"",
                            Objects.requireNonNull(Constant.ERROR_DATA_NOT_FOUND),
                            tv.get(ComnConst.KEY_LANGUAGE)!=null?tv.get(ComnConst.KEY_LANGUAGE).toString():"",
                            "data not found",
                            Constant.N_A,
                            Constant.MESSAGE);
                    endpointResult = errorService.revertMapResult(endpointResultRwd);
                }


                if(Optional.ofNullable(endpointResult)
                        .map(result -> result.getHttpStatus() == 200)
                        .orElse(false)){
                    if (highlightItems == null || highlightItems.isEmpty()) {
                        log.debug(Constant.ENDPOINT_SERVICE_GET_SHELF + " is failed");
//                        endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
//                                tv.get("brand").toString(),
//                                Objects.requireNonNull(Constant.ERROR_DATA_NOT_FOUND),
//                                tv.get(ComnConst.KEY_LANGUAGE).toString(),
//                                "data not found",
//                                Constant.N_A,
//                                Constant.MESSAGE);
//                        endpointResult = errorService.revertMapResult(endpointResultRwd);
                        endpointResultRwd = errorService.convertMapResult(resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC, ComnConst.STTS_CODE_SUCC));
                        endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC, ComnConst.STTS_CODE_SUCC);
                    } else if (!highlightItems.isEmpty()) {
                        log.debug(Constant.ENDPOINT_SERVICE_GET_SHELF + " is success");
                        endpointResultRwd = errorService.convertMapResult(resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA));
                        endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                    }
                }
            }

            ShelfTemplateHighlightRsp templateHighlightRsp = new ShelfTemplateHighlightRsp();
            templateHighlightRsp.setSectionId(sectionId);
            templateHighlightRsp.setLang(lang);
            templateHighlightRsp.setTemplateCode(templateCode);
            templateHighlightRsp.setHighlightItem(highlightItems);

            tv.put(Constant.TRANSACTION_RESPONSE_KEY,templateHighlightRsp);

            log.info("endpointResultRwd : "+endpointResultRwd);
        } catch (Exception e) {
            log.info("error is " + e.getMessage());
            endpointResult = errorService.mapErrorException(e,tv);
            return endpointResult;
        }

        return endpointResult;
    }

    private List<ShelfSectionDetailDto2> filterShelfSectionDetailDto2ToShelfId(List<ShelfSectionDetailDto2> shelfSectionDetailDto2List, String itemMapping) {
        log.info("filterShelfSectionDetailDto2ToShelfId : item_mapping = "+ itemMapping);

        return shelfSectionDetailDto2List.stream()
                .filter(date -> date.getitem_mapping().equals(itemMapping))
                .collect(Collectors.toList());
    }

    private List<ShelfTemplateHighlightRsp.HighlightItem> mapHighlightItemByContent(Map<String, Object> tv, Integer itemNo, List<RwdSystemConfig> rwdSystemConfigs, List<ShelfContentDataApiRsp.ContentData.DataDetails> privilegeList){
        log.info("map HighlightItem is Content");
        if (tv == null || itemNo == null) {
            return new ArrayList<>();
        }

        List<ShelfTemplateHighlightRsp.HighlightItem> highlightItemList = new ArrayList<>();
        String lang = tv.get(ComnConst.KEY_LANGUAGE)!=null? tv.get(ComnConst.KEY_LANGUAGE).toString().toUpperCase():"";
        if(privilegeList != null && !privilegeList.isEmpty()){
            for(ShelfContentDataApiRsp.ContentData.DataDetails privilege : privilegeList){
                if (privilege == null) continue;

                itemNo++;
                String itemType = getItemType(privilege);
                String itemSubtype = getItemSubtype(rwdSystemConfigs, itemType);

                ShelfTemplateHighlightRsp.HighlightItem highlightItem = new ShelfTemplateHighlightRsp.HighlightItem();
                ShelfTemplateHighlightRsp.HighlightItem.ItemImageList itemImageList = new ShelfTemplateHighlightRsp.HighlightItem.ItemImageList();

                if (privilege.getThumb_list() != null) {
                    itemImageList.setImage16x9(privilege.getThumb_list().getHighlight16x9());
                }

                highlightItem.setItemNo(String.valueOf(itemNo));
                highlightItem.setItemName(privilege.getId());
                if (privilege.getInfo() != null) {
                    highlightItem.setItemDisplayName(lang.equals(Constant.TH) ?
                            privilege.getInfo().getMerchant_name_th() :
                            privilege.getInfo().getMerchant_name_en());
                }
                highlightItem.setItemImageList(itemImageList);
                highlightItem.setItemType(itemType);
                highlightItem.setItemSubtype(itemSubtype);
                highlightItem.setItemMapping(privilege.getId());

                highlightItemList.add(highlightItem);
            }
        }

        return highlightItemList;
    }

    private String getItemType(ShelfContentDataApiRsp.ContentData.DataDetails privilege) {
        if (privilege.getContent_type() == null) return "";

        String content_type = privilege.getContent_type()!=null? privilege.getContent_type().toLowerCase():"";
        switch (content_type) {
            case "privilege" -> {
                return "DEAL";
            }
            case "trueyoumerchant" -> {
                return "MERCHANT";
            }
            case "trueyouarticle" -> {
                if (privilege.getSetting() != null &&
                        privilege.getSetting().getThematic_main_shelf_ids() != null &&
                        !privilege.getSetting().getThematic_main_shelf_ids().isEmpty()) {
                    return "THEMATIC";
                }
                return "ARTICLE";
            }
        }
        return "";
    }

    private String getItemSubtype(List<RwdSystemConfig> rwdSystemConfigs, String itemType) {
        if (rwdSystemConfigs == null || itemType == null || itemType.isEmpty()) {
            return "";
        }

        return rwdSystemConfigs.stream()
                .filter(config -> config.getValue() != null && config.getValue().toUpperCase().equals(itemType))
                .findFirst()
                .map(config -> config.getValue().toUpperCase())
                .orElse("");
    }

    private List<ShelfTemplateHighlightRsp.HighlightItem> mapHighlightItemByInquiryShelf(Map<String, Object> tv, Integer itemNo, List<RwdSystemConfig> rwdSystemConfigs, List<ShelfContentDataApiRsp.ContentData.DataDetails> shelfItemsList){
        log.info("map HighlightItem is ItemByInquiryShelf");
        List<ShelfTemplateHighlightRsp.HighlightItem> highlightItemList = new ArrayList<>();
        String lang = tv.get(ComnConst.KEY_LANGUAGE)!=null?tv.get(ComnConst.KEY_LANGUAGE).toString():"";

        if(shelfItemsList != null && !shelfItemsList.isEmpty()){
            for(ShelfContentDataApiRsp.ContentData.DataDetails shelfItems : shelfItemsList){
                if (shelfItems == null) continue;

                itemNo++;
                String itemType = getItemType(shelfItems);
                String itemSubtype = getItemSubtype(rwdSystemConfigs, itemType);

                ShelfTemplateHighlightRsp.HighlightItem highlightItem = new ShelfTemplateHighlightRsp.HighlightItem();
                ShelfTemplateHighlightRsp.HighlightItem.ItemImageList itemImageList = new ShelfTemplateHighlightRsp.HighlightItem.ItemImageList();

                if (shelfItems.getThumb_list() != null) {
                    itemImageList.setImage16x9(shelfItems.getThumb_list().getHighlight16x9());
                }

                highlightItem.setItemNo(String.valueOf(itemNo));
                highlightItem.setItemName(shelfItems.getId());
                if (shelfItems.getInfo() != null) {
                    highlightItem.setItemDisplayName(lang.equals(Constant.TH) ?
                            shelfItems.getInfo().getMerchant_name_th() :
                            shelfItems.getInfo().getMerchant_name_en());
                }
                highlightItem.setItemImageList(itemImageList);
                highlightItem.setItemType(itemType);
                highlightItem.setItemSubtype(itemSubtype);
                highlightItem.setItemMapping(shelfItems.getId());

                highlightItemList.add(highlightItem);
            }
        }

        return highlightItemList;
    }

    private List<ShelfTemplateHighlightRsp.HighlightItem> mapHighlightItemCmsContent(List<ShelfSectionHighlightDto> sectionHighlights){
        log.info("map HighlightItem is Cms");
        List<ShelfTemplateHighlightRsp.HighlightItem> highlightItemList = new ArrayList<>();

        Integer itemNo = 0;
        if(sectionHighlights != null && !sectionHighlights.isEmpty()){
            for(ShelfSectionHighlightDto sh : sectionHighlights){
                if (sh == null) continue;

                itemNo++;
                ShelfTemplateHighlightRsp.HighlightItem highlightItem = new ShelfTemplateHighlightRsp.HighlightItem();
                ShelfTemplateHighlightRsp.HighlightItem.ItemImageList itemImageList = new ShelfTemplateHighlightRsp.HighlightItem.ItemImageList();
                itemImageList.setImage1x1(sh.getitem_image1x1());
                itemImageList.setImage4x3(sh.getitem_image4x3());
                itemImageList.setImage9x16(sh.getitem_image9x16());
                itemImageList.setImage16x9(sh.getitem_image16x9());

                highlightItem.setItemNo(String.valueOf(itemNo));
                highlightItem.setItemName(sh.getitem_mapping());
                highlightItem.setItemImageList(itemImageList);
                highlightItem.setItemType(sh.getitem_type_code());
                highlightItem.setItemSubtype(sh.getitem_subtype());
                highlightItem.setShelfType(sh.getshelf_type_code());
                highlightItem.setItemMapping(sh.getitem_mapping());
                highlightItem.setItemMapping2(sh.getitem_mapping2());

                highlightItemList.add(highlightItem);
            }
        }

        return highlightItemList;
    }

    private List<ShelfTemplateHighlightRsp.HighlightItem> mapHighlightItemByInquiryShelfBanner(Map<String, Object> tv, Integer itemNo, List<ShelfDtacInquiryShelfDetailApiRsp.Pattern> patterns, String type, String catg, String subCatg) {
        log.info("map HighlightItem is Content");
        List<ShelfTemplateHighlightRsp.HighlightItem> highlightItemList = null;
        String lang = tv.get(ComnConst.KEY_LANGUAGE)!=null?tv.get(ComnConst.KEY_LANGUAGE).toString().toUpperCase():"";
        if (patterns != null && patterns.size() > 0) {
            for (ShelfDtacInquiryShelfDetailApiRsp.Pattern pattern : patterns) {
                List<ShelfDtacInquiryShelfDetailApiRsp.Pattern.PromotionPattern> promotionPatternList = filterTypeAndCategoryAndSubCategory(pattern.getPromotionPattern(), type, catg, subCatg);
                log.info("promotionPatternList szie : "+promotionPatternList.size());
                if(promotionPatternList!=null&&promotionPatternList.size() > 0) {
                    highlightItemList = new ArrayList<ShelfTemplateHighlightRsp.HighlightItem>();
                    for (ShelfDtacInquiryShelfDetailApiRsp.Pattern.PromotionPattern promotionPattern : promotionPatternList) {
                        itemNo++;

                        String itemType = mapItemTypeSPECIAL(promotionPattern);

                        ShelfTemplateHighlightRsp.HighlightItem highlightItem = new ShelfTemplateHighlightRsp.HighlightItem();
                        ShelfTemplateHighlightRsp.HighlightItem.ItemImageList itemImageList = new ShelfTemplateHighlightRsp.HighlightItem.ItemImageList();
                        itemImageList.setImage3x2(promotionPattern.getBanner()!=null
                                ?promotionPattern.getBanner().getHref():"");

                        highlightItem.setItemNo(String.valueOf(itemNo));
                        highlightItem.setItemName(promotionPattern.getId());
                        highlightItem.setItemDisplayName(promotionPattern.getRelatedParty()!=null
                                ?promotionPattern.getRelatedParty().getName():"");
                        highlightItem.setItemImageList(itemImageList);
                        highlightItem.setItemType(!itemType.equals("") ? itemType : null);
                        highlightItem.setItemMapping(promotionPattern.getId());

                        highlightItemList.add(highlightItem);
                    }
                }
            }
        }

        return highlightItemList;
    }

    private String mapItemTypeSPECIAL(ShelfDtacInquiryShelfDetailApiRsp.Pattern.PromotionPattern promotionPattern){
        String itemType = "";

        if (promotionPattern.getId() != null && !promotionPattern.getId().equals("")) {
            log.info("Promotion ID is not null or empty. Setting contentType to DEAL.");
            itemType = Constant.DEAL;
        } else {
            if(promotionPattern.getBanner()!=null){
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
            }else {
                log.info(" Banner type: is null ");
            }
        }

        return itemType;
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

    private Map<String, Object> mapQueryDtacSpecialShelfInquiryShelfDetail(Map<String, Object> tv, String lang, String brand, String productType, String customerNumber, String custGrade) throws Exception {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("lang", Objects.equals(lang, Constant.TH) ? "T" : "E");
        queryParams.put("id", apigwUtill.generateRewardBackendId());
        queryParams.put("phoneNumber", apigwUtill.encryptPhoneNoToApiGw(tv));
        queryParams.put("relatedParty.type", rewardUtill.getShortProductTypeForDtac(productType));
        queryParams.put("channel", apigwUtill.isDtac(brand) ? "dtac" : "true");
        queryParams.put("shelfType", 2);
        queryParams.put("bannerType", 9);
        queryParams.put("type", custGrade.replace("_", "%20"));//segment decrypt from FE
        queryParams.put("customerNumber", customerNumber);

        System.out.println("::: " + gson.toJson(queryParams));
        return queryParams;
    }

    private EndpointResultRWD fetchDtacShelfInquiryShelfDetail(Map<String, Object> tv, Map<String, Object> queryParams) throws Exception {
        log.info("fetchDtacShelfInquiryShelfDetail");
        shelfInquiryShelfDetailDtacEndpoint.getShelfInquiryShelfDetailDtac(tv, queryParams);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

    private List<ShelfDtacInquiryShelfDetailApiRsp.Pattern> filterPatternType(List<ShelfDtacInquiryShelfDetailApiRsp.Pattern> patterns) {
        log.info("filterPatternType : pattern.type = 2 and 3");

        return patterns.stream()
                .filter(date -> date.getType().equals("2") || date.getType().equals("3"))
                .collect(Collectors.toList());
    }

    private List<ShelfDtacInquiryShelfDetailApiRsp.Pattern.PromotionPattern> filterTypeAndCategoryAndSubCategory(List<ShelfDtacInquiryShelfDetailApiRsp.Pattern.PromotionPattern> promotionPatterns, String type, String catg, String subCatg) {
        log.info("PromotionPattern Type And Banner.Category And Banner.SubCategory : type = " + type + ", catg = " + catg + ", subCatg = " + subCatg);

        if(promotionPatterns != null && !promotionPatterns.isEmpty()) {
            if (!type.equals("") && !catg.equals("") && !subCatg.equals("")) {
                log.info("filter : type = " + type + ", catg = " + catg + ", subCatg = " + subCatg);
                return promotionPatterns.stream()
                        .filter(date -> date.getType().equals(type) && date.getBanner().getCategory().equals(catg) && date.getBanner().getSubCategory().equals(subCatg))
                        .collect(Collectors.toList());
            } else if (type.equals("") && !catg.equals("") && subCatg.equals("")) {
                log.info("filter : catg = " + catg);
                return promotionPatterns.stream()
                        .filter(date -> date.getBanner().getCategory().equals(catg))
                        .collect(Collectors.toList());
            } else if (!type.equals("") && !catg.equals("") && subCatg.equals("")) {
                log.info("filter : type = " + type + ", catg = " + catg);
                return promotionPatterns.stream()
                        .filter(date -> date.getType().equals(type) && date.getBanner().getCategory().equals(catg))
                        .collect(Collectors.toList());
            } else if (type.equals("") && catg.equals("") && !subCatg.equals("")) {
                log.info("filter : subCatg = " + subCatg);
                return promotionPatterns.stream()
                        .filter(date -> date.getBanner().getSubCategory().equals(subCatg))
                        .collect(Collectors.toList());
            } else if (!type.equals("") && catg.equals("") && !subCatg.equals("")) {
                log.info("filter : type = " + type + ", subCatg = " + subCatg);
                return promotionPatterns.stream()
                        .filter(date -> date.getType().equals(type) && date.getBanner().getSubCategory().equals(subCatg))
                        .collect(Collectors.toList());
            } else if (!type.equals("") && catg.equals("") && subCatg.equals("")) {
                log.info("filter : type = " + type);
                return promotionPatterns.stream()
                        .filter(date -> date.getType().equals(type))
                        .collect(Collectors.toList());
            }
        }

        return promotionPatterns;
    }

}
