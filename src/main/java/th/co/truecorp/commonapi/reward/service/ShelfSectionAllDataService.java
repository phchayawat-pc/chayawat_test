package th.co.truecorp.commonapi.reward.service;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSystemConfig;
import th.co.truecorp.commonapi.reward.cms.jpa.service.*;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.dto.*;
import th.co.truecorp.commonapi.reward.endpoint.*;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import java.text.ParseException;
import java.util.*;

@Service
public class ShelfSectionAllDataService {

    private static Logger log = LoggerFactory.getLogger(ShelfSectionAllDataService.class);

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
    private ErrorService errorService;

    public EndpointResult getSectionAllData(Map<String, Object> tv , String brand) throws Exception {

        EndpointResult endpointResult = null;
        tv.put(ComnConst.KEY_LANGUAGE, rewardUtill.handleLanguage(tv.get(ComnConst.KEY_LANGUAGE).toString()));
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
        EndpointResultRWD endpointResultRwd = new EndpointResultRWD();

        String sectionId = tv.get("sectionId") != null ? tv.get("sectionId").toString() : "";
        String displayTypeCode = tv.get("displayTypeCode") != null ? tv.get("displayTypeCode").toString() : "";

        try {

            ShelfSectionAllDataRsp shelfSectionAllDataRsp = new ShelfSectionAllDataRsp();
            List<ShelfSectionAllDataRsp.DealList> dealLists = new ArrayList<ShelfSectionAllDataRsp.DealList>();

            Optional<RwdSystemConfig> optional = rwdSystemConfigService.findConfigs("MAX_SLIDE","FIX");
            RwdSystemConfig rwdSystemConfig = optional.get();
            Integer maxRow = Integer.valueOf(rwdSystemConfig.getValue());

            if(!apigwUtill.isDtac(brand)){
                List<ShelfSectionAllDataDto> sectionAllDataDtos = rwdSectionDetailService.findSectionAllDTOBySectionId(sectionId, lang);

                if(!sectionAllDataDtos.isEmpty()){
                    for(ShelfSectionAllDataDto sectionAllDataDto:sectionAllDataDtos){
                        List<ShelfSectionAllDataRsp.DealList.ItemList> itemLists = null;
                        if("MERCHANT".equals(sectionAllDataDto.getitem_type_code())){
                            log.info("MERCHANT");
                            endpointResult = errorService.revertMapResult(fetchShelfContent(tv, sectionAllDataDto.getitem_mapping(), lang.toLowerCase(), maxRow));
                            List<ShelfContentDataApiRsp.ContentData.DataDetails> privilegeLists = (List<ShelfContentDataApiRsp.ContentData.DataDetails>) tv.get("GetContent");
                            itemLists = mapContentToShelfSectionAllDataRsp(tv, privilegeLists);
                        }else if("SHELF".equals(sectionAllDataDto.getitem_type_code())){
                            log.info("SHELF");
                            endpointResult = errorService.revertMapResult(fetchTrueShelfInquiryShelfDetail(tv, sectionAllDataDto.getitem_mapping(), lang, brand, maxRow));
                            List<ShelfContentDataApiRsp.ContentData.DataDetails> shelfItemsList = (List<ShelfContentDataApiRsp.ContentData.DataDetails>) tv.get("GetShelfInquiryShelfDetail");
                            itemLists = mapInquiryShelfToShelfSectionAllDataRsp(tv, shelfItemsList);
                        }
                        ShelfSectionAllDataRsp.DealList dealList = new ShelfSectionAllDataRsp.DealList();
                        ShelfSectionAllDataRsp.DealList.ThumbnailList itemImageList = new ShelfSectionAllDataRsp.DealList.ThumbnailList();
                        itemImageList.setImageIcon(sectionAllDataDto.getitem_icon());
                        itemImageList.setImage1x1(sectionAllDataDto.getitem_image1x1());
                        itemImageList.setImage3x2(null);
                        itemImageList.setImage4x3(sectionAllDataDto.getitem_image4x3());
                        itemImageList.setImage16x9(sectionAllDataDto.getitem_image16x9());
                        itemImageList.setImage9x16(sectionAllDataDto.getitem_image9x16());

                        dealList.setItemNo(sectionAllDataDto.getseq_no());
                        dealList.setItemName(sectionAllDataDto.getitem_name());
                        dealList.setItemDisplayName(sectionAllDataDto.getitem_display_name() != null ? sectionAllDataDto.getitem_display_name():sectionAllDataDto.getitem_display_name_en());
                        dealList.setItemImageList(itemImageList);
                        dealList.setItemType(sectionAllDataDto.getitem_type_code());
                        dealList.setItemSubtype(sectionAllDataDto.getitem_subtype());
                        dealList.setShelfType(sectionAllDataDto.getshelf_type_code());
                        dealList.setItemMapping(sectionAllDataDto.getitem_mapping());
                        dealList.setItemMapping2(sectionAllDataDto.getitem_mapping2());
                        dealList.setItemList(itemLists);

                        dealLists.add(dealList);
                        endpointResult = endpointResult == null ? resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA) : endpointResult;
                    }
                }else{
                    endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
                            tv.get("brand").toString(),
                            Objects.requireNonNull(Constant.ERROR_DATA_NOT_FOUND),
                            tv.get(ComnConst.KEY_LANGUAGE).toString(),
                            "data not found",
                            Constant.N_A,
                            Constant.MESSAGE);
                    endpointResult = errorService.revertMapResult(endpointResultRwd);
                }
            } else {
               endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
                        apigwUtill.isDtac(brand) ? "DTAC" : "TRUE",
                        Objects.requireNonNull(Constant.ERROR_TEMPLATE_NOT_SUPPORTED),
                        tv.get(ComnConst.KEY_LANGUAGE).toString(),
                        "Not supported for this template",
                        Constant.N_A,
                        Constant.MESSAGE);
               endpointResult = errorService.revertMapResult(endpointResultRwd);
                log.info("Not supported for this template.");
            }

            shelfSectionAllDataRsp.setSectionId(sectionId);
            shelfSectionAllDataRsp.setLang(lang);
            shelfSectionAllDataRsp.setDisplayTypeCode(displayTypeCode);
            shelfSectionAllDataRsp.setExpireDate(new Date());
            shelfSectionAllDataRsp.setDealList(dealLists);

            if(endpointResult.getHttpStatus() == 200){
                if(shelfSectionAllDataRsp.getDealList() != null){
                    log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + "is success with deal list");
                    endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                }else{
                    log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + "is success without deal list");
                    endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC, ComnConst.STTS_CODE_SUCC);
                }
            }

            tv.put(Constant.TRANSACTION_RESPONSE_KEY,shelfSectionAllDataRsp);

            log.info("endpointResult : "+endpointResult);
        } catch (Exception e) {
            log.info("error is " + e.getMessage());
            endpointResult = resultService.getEndpointExceptionResult(tv, e);
            return endpointResult;
        }

        return endpointResult;
    }

    private List<ShelfSectionAllDataRsp.DealList.ItemList> mapContentToShelfSectionAllDataRsp (Map<String, Object> tv, List<ShelfContentDataApiRsp.ContentData.DataDetails> shelfItemsLists) throws ParseException {
        log.info("map Content To ShelfSectionAllDataRsp");
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
        List<ShelfSectionAllDataRsp.DealList.ItemList> itemLists = new ArrayList<ShelfSectionAllDataRsp.DealList.ItemList>();
        Integer seqNo = 0;
        if(shelfItemsLists != null){
            for(ShelfContentDataApiRsp.ContentData.DataDetails shelfItemsList:shelfItemsLists){
                seqNo++;
                ShelfSectionAllDataRsp.DealList.ItemList itemList = new ShelfSectionAllDataRsp.DealList.ItemList();
                ShelfSectionAllDataRsp.DealList.ItemList.ThumbnailList thumbnailList = new ShelfSectionAllDataRsp.DealList.ItemList.ThumbnailList();
                thumbnailList.setThumbnail4x3(null);
                thumbnailList.setThumbnail16x9(
                        shelfItemsList.getThumb_list() != null &&
                                shelfItemsList.getThumb_list().getHighlight16x9() != null &&
                                !shelfItemsList.getThumb_list().getHighlight16x9().isEmpty()
                                ? shelfItemsList.getThumb_list().getHighlight16x9()
                                : Constant.DEFAULT_NULL_EXCEPTION_VALUE
                );

                String contentType = null;
                if(shelfItemsList.getContent_type() != null){
                    if("trueyoumerchant".equals(shelfItemsList.getContent_type())){
                        contentType = "MERCHANT";
                    }else if("trueyouarticle".equals(shelfItemsList.getContent_type())){
                        if(shelfItemsList.getSetting() != null && shelfItemsList.getSetting().getThematic_main_shelf_ids() != null && !shelfItemsList.getSetting().getThematic_main_shelf_ids().isEmpty()){
                            contentType = "THEMATIC";
                        }else{
                            contentType = "ARTICLE";
                        }
                    }else if("privilege".equals(shelfItemsList.getContent_type())){
                        contentType = "DEAL";
                    }
                }

                itemList.setSeqNo(seqNo);
                itemList.setCampaignId(shelfItemsList.getId());
                itemList.setCampaignCode(shelfItemsList.getCampaign_code());
                itemList.setContentType(contentType);
                itemList.setTimeCounterFlag(shelfItemsList.getInfo() != null ? shelfItemsList.getInfo().getTime_counter_show() : null);
                itemList.setThumbnailList(thumbnailList);
                itemList.setCampaignName(shelfItemsList.getInfo() != null ? (lang.equalsIgnoreCase(Constant.TH) ? shelfItemsList.getInfo().getMerchant_name_th():shelfItemsList.getInfo().getMerchant_name_en()) : null);
                itemList.setCampaignDescription(shelfItemsList.getTitle());
                itemList.setCampaignExpireDate(shelfItemsList.getExpire_date());
                itemList.setCampaignType(shelfItemsList.getCampaign_type());
                itemList.setCardType(shelfItemsList.getCard_type());
                itemList.setRegularPoint(shelfItemsList.getRedeem_point());
                itemList.setOfferPoint(shelfItemsList.getRedeem_point());

                itemLists.add(itemList);
            }
        }
        return itemLists;
    }

    private List<ShelfSectionAllDataRsp.DealList.ItemList> mapInquiryShelfToShelfSectionAllDataRsp (Map<String, Object> tv, List<ShelfContentDataApiRsp.ContentData.DataDetails> privilegeLists) throws ParseException {
        log.info("map InquiryShelf To ShelfSectionAllDataRsp");
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
        List<ShelfSectionAllDataRsp.DealList.ItemList> itemLists = new ArrayList<ShelfSectionAllDataRsp.DealList.ItemList>();
        Integer seqNo = 0;
        if(privilegeLists != null){
            for(ShelfContentDataApiRsp.ContentData.DataDetails privilegeList:privilegeLists){
                seqNo++;
                ShelfSectionAllDataRsp.DealList.ItemList itemList = new ShelfSectionAllDataRsp.DealList.ItemList();
                ShelfSectionAllDataRsp.DealList.ItemList.ThumbnailList thumbnailList = new ShelfSectionAllDataRsp.DealList.ItemList.ThumbnailList();
                thumbnailList.setThumbnail4x3(null);
                thumbnailList.setThumbnail16x9(
                        privilegeList.getThumb_list() != null &&
                                privilegeList.getThumb_list().getHighlight16x9() != null &&
                                !privilegeList.getThumb_list().getHighlight16x9().isEmpty()
                                ? privilegeList.getThumb_list().getHighlight16x9()
                                : Constant.DEFAULT_NULL_EXCEPTION_VALUE
                );

                String contentType = null;
                if(privilegeList.getContent_type() != null){
                    if("trueyoumerchant".equals(privilegeList.getContent_type())){
                        contentType = "MERCHANT";
                    }else if("trueyouarticle".equals(privilegeList.getContent_type())){
                        if(privilegeList.getSetting() != null
                                && privilegeList.getSetting().getThematic_main_shelf_ids() != null
                                && !privilegeList.getSetting().getThematic_main_shelf_ids().isEmpty()){
                            contentType = "THEMATIC";
                        }else{
                            contentType = "ARTICLE";
                        }
                    }else if("privilege".equals(privilegeList.getContent_type())){
                        contentType = "DEAL";
                    }
                }

                itemList.setSeqNo(seqNo);
                itemList.setCampaignId(privilegeList.getId());
                itemList.setCampaignCode(privilegeList.getCampaign_code());
                itemList.setContentType(contentType);
                itemList.setTimeCounterFlag(privilegeList.getInfo() != null ? privilegeList.getInfo().getTime_counter_show() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                itemList.setThumbnailList(thumbnailList);
                itemList.setCampaignName(privilegeList.getInfo() != null ? (lang.equalsIgnoreCase(Constant.TH) ? privilegeList.getInfo().getMerchant_name_th():privilegeList.getInfo().getMerchant_name_en()) : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                itemList.setCampaignDescription(privilegeList.getTitle());
                itemList.setCampaignExpireDate(privilegeList.getExpire_date());
                itemList.setCampaignType(privilegeList.getCampaign_type());
                itemList.setCardType(privilegeList.getCard_type());
                itemList.setRegularPoint(privilegeList.getRedeem_point());
                itemList.setOfferPoint(privilegeList.getRedeem_point());

                itemLists.add(itemList);
            }
        }
        return itemLists;
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
