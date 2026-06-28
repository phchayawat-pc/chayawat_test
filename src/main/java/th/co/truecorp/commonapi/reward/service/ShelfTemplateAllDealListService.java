package th.co.truecorp.commonapi.reward.service;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSectionDetail;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSectionDisplayName;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSystemConfig;
import th.co.truecorp.commonapi.reward.cms.jpa.service.*;
import th.co.truecorp.commonapi.reward.common.dto.PageDTO;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.dto.ShelfSectionDetailDto;
import th.co.truecorp.commonapi.reward.endpoint.*;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.model.EndpointResult;

import java.util.*;
//2025-03-07 เพิ่ม dtac GROUPING AND MAJOR
@Service
public class ShelfTemplateAllDealListService {

    private static Logger log = LoggerFactory.getLogger(ShelfTemplateAllDealListService.class);

    @Autowired
    private ResultService resultService;

    @Autowired
    private APIGWUtill apigwUtill;

    @Autowired
    private RewardUtill rewardUtill;

    @Autowired
    private RwdSystemConfigService rwdSystemConfigService;

    @Autowired
    private RwdSectionDisplayNameService rwdSectionDisplayNameService;

    @Autowired
    private RwdSectionDetailService rwdSectionDetailService;

    @Autowired
    private ShelfContentEndpoint shelfContentEndpoint;

    @Autowired
    private ShelfTrueInquiryShelfDetailEndpoint shelfTrueInquiryShelfDetailEndpoint;

    @Autowired
    private CampaignGroupServiceEndpoint campaignGroupServiceEndpoint;

    @Autowired
    private ErrorService errorService;

    Gson gson = new Gson();

    @EndpointLog (name = "TRUEAPP.GetTemplatealldeallist")
    public EndpointResult getTemplateAllDealList(Map<String, Object> tv, String brand) throws Exception {
        log.info("Start getTemplateAllDealList Service");
        EndpointResult endpointResult = null;
        tv.put(ComnConst.KEY_LANGUAGE, rewardUtill.handleLanguage(tv.get(ComnConst.KEY_LANGUAGE).toString()));
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
        int paging = 1;
        int limit = 10;
        if (tv.get("page") != null && !tv.get("page").toString().equals("")) {
            paging = Integer.parseInt(tv.get("page").toString());
        }
        if (tv.get("limit") != null && !tv.get("limit").toString().equals("")) {
            limit = Integer.parseInt(tv.get("limit").toString());
        }

        String sectionId = tv.get("sectionId") != null ? tv.get("sectionId").toString() : "";
        String templateCode = tv.get("templateCode") != null ? tv.get("templateCode").toString() : "";
        try {
            String sectionName = "";
            String sectionHeader = "";

            RwdSectionDisplayName rwdSectionDisplayName = null;
            Optional<RwdSystemConfig> optional = rwdSystemConfigService.findConfigs("SECTION_NAME",sectionId);
            log.info("RwdSystemConfig : SECTION_NAME");
            if(optional.isEmpty()){
                Optional<RwdSectionDisplayName> Optional = rwdSectionDisplayNameService.findSectionIdAndLang(sectionId, lang);
                log.info("RwdSectionDisplayName");
                if(!Optional.isEmpty()){
                    rwdSectionDisplayName = Optional.get();
                    sectionName = rwdSectionDisplayName.getSectionDisplayName();
                }
            }else{
                RwdSystemConfig systemConfig = optional.get();
                sectionName = systemConfig.getValue();
            }

            Optional<RwdSystemConfig> optional2 = rwdSystemConfigService.findConfigs("SECTION_HEADER",sectionId);
            log.info("RwdSystemConfig : SECTION_HEADER");
            if(!optional2.isEmpty()){
                RwdSystemConfig systemConfig2 = optional2.get();
                sectionHeader = systemConfig2.getValue();
            }

//            List<RwdSectionDetail> rwdSectionDetails = rwdSectionDetailService.findSectionId(sectionId);
            List<ShelfSectionDetailDto> rwdSectionDetails = rwdSectionDetailService.findShelfSectionDetailDtoBySectionId(sectionId);
            log.info("RwdSectionDetail : sectionId = "+sectionId +" : "+ rwdSectionDetails.size());
            List<TemplateAllDealListRsp> templateAllDealLists = new ArrayList<TemplateAllDealListRsp>();
            Integer seqNo = 0;
            for (ShelfSectionDetailDto rwdSectionDetail:rwdSectionDetails){
                String itemTypeCode = rwdSectionDetail.getitem_type_code();
                String itemMapping = rwdSectionDetail.getitem_mapping();
                if(itemTypeCode!=Constant.DEFAULT_NULL_EXCEPTION_VALUE&&itemTypeCode.equals("MERCHANT")){
                    log.info("Shelf MERCHANT , itemMapping: "+itemMapping);
                    endpointResult = errorService.revertMapResult(fetchShelfContent(tv, itemMapping, lang.toLowerCase(), -1));
                    List<TemplateAllDealListRsp> rsps = mapContentToShelfHighlightItemDetailRsp(tv, seqNo, (List<ShelfContentDataApiRsp.ContentData.DataDetails>) tv.get("GetContent"));
                    if(rsps != null){
                        seqNo = rsps.get(rsps.size()-1).getSeqNo();
                        templateAllDealLists.addAll(rsps);
                    }

                }else if(itemTypeCode!=Constant.DEFAULT_NULL_EXCEPTION_VALUE
                        &&itemTypeCode.equals("SHELF")){
                    log.info("Shelf SHELF");
                    String shelfTypeCode = rwdSectionDetail.getshelf_type_code();
                    log.info("itemMapping: "+itemMapping+", shelfTypeCode : " +shelfTypeCode);
                    if(shelfTypeCode.equals("NORMAL_TRUE")){
                        log.info("Shelf : NORMAL_TRUE");
                        endpointResult = errorService.revertMapResult(fetchTrueShelfInquiryShelfDetail(tv, itemMapping, lang, brand, 50));
                        List<TemplateAllDealListRsp> rsps = mapInquiryToShelfHighlightItemDetailRsp(tv, seqNo, (List<ShelfContentDataApiRsp.ContentData.DataDetails>) tv.get("GetShelfInquiryShelfDetail"));
                        if(rsps != null){
                            seqNo = rsps.get(rsps.size()-1).getSeqNo();
                            templateAllDealLists.addAll(rsps);
                        }
                    }
                } else if(itemTypeCode!=Constant.DEFAULT_NULL_EXCEPTION_VALUE
                            &&(itemTypeCode.equals(Constant.GROUPING) || itemTypeCode.equals(Constant.MAJOR))){
                    log.info("GROUPING AND MAJOR :" + itemMapping);
                    String levelSegment = rewardUtill.mapUserLevel(tv);
                    tv.put("levelSegment",levelSegment);
                    tv.put("itemMapping", itemMapping);
                    endpointResult = campaignGroupServiceEndpoint.getCampaignGroup(tv);
                    if(endpointResult.getHttpStatus() == ComnConst.STTS_HTTP_SUCC){
                        List<TemplateAllDealListRsp> rsps = mapCampaignGroupItemDetailRsp(tv, seqNo);
                        if(rsps != null){
                            seqNo = rsps.get(rsps.size()-1).getSeqNo();
                            templateAllDealLists.addAll(rsps);
                        }
                    }
                }
            }

            if(endpointResult != null && endpointResult.getHttpStatus() == 200) {
                processPagingTemplateAllDeal(tv, templateAllDealLists, paging, limit);
                templateAllDealLists = (List<TemplateAllDealListRsp>) tv.get("pagedTemplateAllDealListRsp");

                TemplateAllRsp templateAllRsp = new TemplateAllRsp();
                templateAllRsp.setSectionId(sectionId);
                templateAllRsp.setLang(lang);
                templateAllRsp.setSectionName(sectionName);
                templateAllRsp.setSectionHeader(sectionHeader);
                templateAllRsp.setTemplateCode(templateCode);
                templateAllRsp.setDealList(templateAllDealLists);

                if (templateAllDealLists == null) {
                    log.debug(Constant.ENDPOINT_SERVICE_GET_SHELF + " is failed");
                    EndpointResultRWD endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
                            tv.get("brand").toString(),
                            Objects.requireNonNull(Constant.ERROR_DATA_NOT_FOUND),
                            tv.get(ComnConst.KEY_LANGUAGE).toString(),
                            "data not found",
                            Constant.N_A,
                            Constant.MESSAGE);
                    endpointResult = errorService.revertMapResult(endpointResultRwd);
                } else if (templateAllDealLists != null) {
                    log.debug(Constant.ENDPOINT_SERVICE_GET_SHELF + " is success");
                    endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                }

                tv.put(Constant.TRANSACTION_RESPONSE_KEY,templateAllRsp);
            }else {
                EndpointResultRWD endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
                        tv.get("brand").toString(),
                        Objects.requireNonNull(Constant.ERROR_DATA_NOT_FOUND),
                        tv.get(ComnConst.KEY_LANGUAGE).toString(),
                        "data not found",
                        Constant.N_A,
                        Constant.MESSAGE);
                endpointResult = errorService.revertMapResult(endpointResultRwd);
            }

            log.info("endpointResult : "+endpointResult);
        } catch (Exception e) {
            log.info("error is " + e.getMessage());
            endpointResult = errorService.mapErrorException(e,tv);
            return endpointResult;
        }

        return endpointResult;
    }

    private List<TemplateAllDealListRsp> mapContentToShelfHighlightItemDetailRsp(Map<String, Object> tv, Integer seqNo, List<ShelfContentDataApiRsp.ContentData.DataDetails> privilegeList){
        log.info("map ContentToShelfHighlight ItemDetailRsp");
        List<TemplateAllDealListRsp> dealLists = new ArrayList<TemplateAllDealListRsp>();

        if(privilegeList != null){
            for(ShelfContentDataApiRsp.ContentData.DataDetails privilege:privilegeList){
                TemplateAllDealListRsp detail = new TemplateAllDealListRsp();
                seqNo++;
                String contentType = "";
                if (privilege.getContent_type().equals("privilege")) {
                    contentType = Constant.DEAL;
                } else if (privilege.getContent_type().equals("trueyoumerchant")){
                    contentType = Constant.MERCHANT;
                }else if(privilege.getContent_type().equals("trueyouarticle")) {
                    String thematicMainShelfIds = privilege.getSetting() != null ? privilege.getSetting().getThematic_main_shelf_ids() : "";
                    if(thematicMainShelfIds.isEmpty()){
                        contentType = Constant.THEMATIC;
                    }else{
                        contentType = Constant.ARTICLE;
                    }
                }

                detail.setSeqNo(seqNo);
                detail.setContentType(contentType);
                detail.setCampaignId(privilege.getId());
                detail.setCampaignCode(privilege.getCampaign_code());
                String timeCounterFlag = "";
                if(privilege.getInfo() != null){
                    timeCounterFlag = privilege.getInfo().getTime_counter_show();
                }
                detail.setTimeCounterFlag(timeCounterFlag);
                detail.setCampaignDescription(privilege.getTitle());
                detail.setCampaignExpireDate(privilege.getExpire_date());
                detail.setCampaignType(privilege.getCampaign_type());
                detail.setRegularPoint(privilege.getRedeem_point());
                detail.setOfferPoint(privilege.getRedeem_point());

                ThumbnailListRsp thumbnail = new ThumbnailListRsp();
                thumbnail.setThumbnail16x9(privilege.getThumb_list() != null ? privilege.getThumb_list().getHighlight16x9() : "");
                detail.setThumbnailList(thumbnail);

                String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
                if(lang.equalsIgnoreCase(Constant.TH)){
                    detail.setCampaignName(privilege.getInfo() != null ? privilege.getInfo().getBudget_save_currency_th() : "");
                }else{
                    detail.setCampaignName(privilege.getInfo() != null ? privilege.getInfo().getBudget_save_text_en() : "");
                }

                List<String> resultCardType = privilege.getCard_type();
                detail.setCardType(resultCardType);

                dealLists.add(detail);
            }
        }else{
            dealLists = null;
        }

        return dealLists;
    }

    private List<TemplateAllDealListRsp> mapInquiryToShelfHighlightItemDetailRsp(Map<String, Object> tv, Integer seqNo, List<ShelfContentDataApiRsp.ContentData.DataDetails> shelfItemsList){
        log.info("map InquiryToShelfHighlight ItemDetailRsp");
        List<TemplateAllDealListRsp> dealLists = new ArrayList<TemplateAllDealListRsp>();

        if(shelfItemsList != null){
            for(ShelfContentDataApiRsp.ContentData.DataDetails shelfItems:shelfItemsList) {
                TemplateAllDealListRsp detail = new TemplateAllDealListRsp();
                seqNo++;
                String contentType = "";
                if (shelfItems.getContent_type().equals("privilege")) {
                    contentType = Constant.DEAL;
                } else if (shelfItems.getContent_type().equals("trueyoumerchant")){
                    contentType = Constant.MERCHANT;
                }else if(shelfItems.getContent_type().equals("trueyouarticle")) {
                    String thematicMainShelfIds = shelfItems.getSetting() != null ? shelfItems.getSetting().getThematic_main_shelf_ids() : "";
                    if(thematicMainShelfIds.equals("")){
                        contentType = Constant.THEMATIC;
                    }else{
                        contentType = Constant.ARTICLE;
                    }
                }

                detail.setSeqNo(seqNo);
                detail.setContentType(contentType);
                detail.setCampaignId(shelfItems.getId());
                detail.setCampaignCode(shelfItems.getCampaign_code());
                detail.setTimeCounterFlag(shelfItems.getInfo() != null ? shelfItems.getInfo().getTime_counter_show() : "");
                detail.setCampaignDescription(shelfItems.getTitle());
                detail.setCampaignExpireDate(shelfItems.getExpire_date());
                detail.setCampaignType(shelfItems.getCampaign_type());
                detail.setRegularPoint(shelfItems.getRedeem_point());
                detail.setOfferPoint(shelfItems.getRedeem_point());

                ThumbnailListRsp thumbnail = new ThumbnailListRsp();
                thumbnail.setThumbnail16x9(shelfItems.getThumb_list() != null ? shelfItems.getThumb_list().getHighlight16x9() : "");
                detail.setThumbnailList(thumbnail);

                String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
                if(lang.toUpperCase().equals(Constant.TH)){
                    detail.setCampaignName(shelfItems.getInfo() != null ? shelfItems.getInfo().getBudget_save_currency_th() : "");
                }else{
                    detail.setCampaignName(shelfItems.getInfo() != null ? shelfItems.getInfo().getBudget_save_text_en() : "");
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

    private List<TemplateAllDealListRsp> mapCampaignGroupItemDetailRsp(Map<String, Object> tv, Integer seqNo){
        log.info("map CampaignGroup ItemDetailRsp");
        CampaignGroupResponse campaignGroupResponse = (CampaignGroupResponse) tv.get(Constant.ENDPOINT_SERVICE_CAMPAIGN_GROUP);
        List<TemplateAllDealListRsp> dealLists = new ArrayList<TemplateAllDealListRsp>();
        String languageKey = Objects.equals(tv.get(ComnConst.KEY_LANGUAGE).toString().toUpperCase(), Constant.TH) ? "thai" : "english";
        if(campaignGroupResponse != null
                && campaignGroupResponse.getData()!= null
                && campaignGroupResponse.getData().getCampaigns()!=null){
            for(Campaign campaign : campaignGroupResponse.getData().getCampaigns()) {
                TemplateAllDealListRsp detail = new TemplateAllDealListRsp();
                seqNo++;

                String campaignDescription = Optional.ofNullable(campaign.getNameLanguage().get(languageKey))
                        .map(Object::toString)
                        .orElse("");

                String campaignExpireDate = Optional.ofNullable(campaign.getEndDate())
                        .map(Object::toString)
                        .orElse("");

                String typeValue = Optional.ofNullable(campaign.getCharacteristic())
                        .orElse(Collections.emptyList())
                        .stream()
                        .filter(c -> "type".equals(c.getName()))
                        .map(Characteristic::getValue)
                        .findFirst()
                        .orElse(null);

                Map<String, String> typeMap = new HashMap<>();
                typeMap.put("1", Constant.DTAC_REWARD);
                typeMap.put("2", Constant.COIN);

                String campaignType = typeMap.getOrDefault(typeValue, null);

                List<String> cardTypes = rewardUtill.levelCardType(campaign.getUserLevel());

                String regularPoint = Optional.ofNullable(campaign.getOriginalPoint())
                        .map(Object::toString)
                        .orElse(null);

                String offerPoint = Optional.ofNullable(campaign.getPointPerUnit())
                        .map(Object::toString)
                        .orElse(null);

                String campaignId = Optional.ofNullable(campaign.getCharacteristic())
                        .orElse(Collections.emptyList())
                        .stream()
                        .filter(c -> "campaignIdDtac".equals(c.getName()))
                        .map(Characteristic::getValue)
                        .findFirst()
                        .orElse(null);

                ThumbnailListRsp thumbnail = new ThumbnailListRsp();
                thumbnail.setThumbnail3x2(campaign.getImageUrl()!=null ? campaign.getImageUrl():null);

                detail.setSeqNo(seqNo);
                detail.setContentType(Constant.DEAL);
                detail.setCampaignId(campaign.getId());
                detail.setCampaignCode(campaignId);
                detail.setTimeCounterFlag("N");
                detail.setCampaignDescription(campaignDescription);
                detail.setCampaignExpireDate(campaignExpireDate);
                detail.setCampaignType(campaignType);
                detail.setRegularPoint(regularPoint);
                detail.setOfferPoint(offerPoint);
                detail.setThumbnailList(thumbnail);
                detail.setCardType(cardTypes);

                dealLists.add(detail);
            }
        }else{
            dealLists = null;
        }

        return dealLists;
    }

    private void processPagingTemplateAllDeal(Map<String, Object> tv, List<TemplateAllDealListRsp> templateAllDealListRsp, int paging, int limit) {
        PagedResult<TemplateAllDealListRsp> pagedTemplateAllDealListRsp = apigwUtill.paginate(templateAllDealListRsp, paging, limit);

        PageDTO pageReq = new PageDTO();
        pageReq.setPageNumber(paging);
        pageReq.setPageSize(limit);
        pageReq.setCount(pagedTemplateAllDealListRsp.getTotalCount());
        pageReq.setTotalPage(pagedTemplateAllDealListRsp.getTotalPages());

        tv.put("paging", pageReq);
        tv.put("pagedTemplateAllDealListRsp", pagedTemplateAllDealListRsp.getItems());
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

}
