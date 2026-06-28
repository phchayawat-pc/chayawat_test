package th.co.truecorp.commonapi.reward.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.service.*;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.endpoint.*;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import java.text.ParseException;
import java.util.*;

@Service
public class ShelfTemplateMerchantDealListService {

    private static Logger log = LoggerFactory.getLogger(ShelfTemplateMerchantDealListService.class);

    @Autowired
    private ResultService resultService;

    @Autowired
    private APIGWUtill apigwUtill;

    @Autowired
    private RewardUtill rewardUtill;

    @Autowired
    private RedeemContentEndpoint redeemContentEndpoint;

    @Autowired
    private ErrorService errorService;

    public EndpointResultRWD getTemplateMerchantDealList(Map<String, Object> tv , String brand) throws Exception {

        EndpointResultRWD endpointResultRwd = new EndpointResultRWD();

        tv.put(ComnConst.KEY_LANGUAGE, rewardUtill.handleLanguage(tv.get(ComnConst.KEY_LANGUAGE).toString()));
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();

        String merchantId = Objects.toString(tv.get("merchantId"), "");
        String templateCode = Objects.toString(tv.get("templateCode"), "");

        try {
            ShelfTemplateMerchantDealRsp templateMerchantDeal = new ShelfTemplateMerchantDealRsp();

            if (brand != null && apigwUtill.isDtac(brand)) {
                templateMerchantDeal = null;
            } else {
                endpointResultRwd = fetchTrueContent(tv, merchantId, lang.toLowerCase(), -1);

                templateMerchantDeal.setMerchantId(merchantId);
                templateMerchantDeal.setLang(lang);
                templateMerchantDeal.setTemplateCode(templateCode);

                if(Optional.ofNullable(endpointResultRwd)
                        .map(result -> result.getHttpStatus() == 200)
                        .orElse(false)){

                    ShelfContentDataApiRsp.ContentData.DataDetails dataDetails = (ShelfContentDataApiRsp.ContentData.DataDetails) tv.get("GetContent");

                    if(dataDetails != null && "trueyoumerchant".equalsIgnoreCase(dataDetails.getContent_type())) {
                        templateMerchantDeal = mapTemplateMerchantDeal(tv, templateMerchantDeal, dataDetails);
                    }

                    if(templateMerchantDeal.getDealList() != null){
                        log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + " is success with deal list");
                        endpointResultRwd = errorService.convertMapResult(resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA));
                    }else{
                        log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + " is success with no deal list");
                        endpointResultRwd = errorService.convertMapResult(resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC, ComnConst.STTS_CODE_SUCC));
                    }
                }
            }

            tv.put(Constant.TRANSACTION_RESPONSE_KEY,templateMerchantDeal);

            log.info("endpointResult : "+endpointResultRwd);
        } catch (Exception e) {
            log.info("error is " + e.getMessage());
            endpointResultRwd = errorService.convertMapResult(errorService.mapErrorException(e,tv));
            return endpointResultRwd;
        }

        return endpointResultRwd;
    }

    private ShelfTemplateMerchantDealRsp mapTemplateMerchantDeal (Map<String, Object> tv, ShelfTemplateMerchantDealRsp templateMerchantDeal, ShelfContentDataApiRsp.ContentData.DataDetails dataDetail) throws ParseException {
        if (templateMerchantDeal == null) {
            templateMerchantDeal = new ShelfTemplateMerchantDealRsp();
        }

        if (dataDetail != null) {
            ShelfTemplateMerchantDealRsp.ThumbnailList thumbnailList = new ShelfTemplateMerchantDealRsp.ThumbnailList();
            thumbnailList.setThumbnail16x9(
                Optional.ofNullable(dataDetail.getThumb_list())
                    .map(thumbList -> thumbList.getHighlight16x9())
                    .orElse(null)
            );

            templateMerchantDeal.setMerchantName(dataDetail.getTitle());
            templateMerchantDeal.setThumbnailList(thumbnailList);
            templateMerchantDeal.setDealList(mapDealList(tv, dataDetail.getPrivilege_list()));
        }

        return templateMerchantDeal;
    }

    private List<ShelfTemplateMerchantDealRsp.DealList> mapDealList (Map<String, Object> tv, List<ShelfContentDataApiRsp.ContentData.DataDetails> dataDetails) throws ParseException {
        if (tv == null || tv.get(ComnConst.KEY_LANGUAGE) == null) {
            return Collections.emptyList();
        }

        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
        
        if (dataDetails == null || dataDetails.isEmpty()) {
            return null;
        }

        List<ShelfTemplateMerchantDealRsp.DealList> dealLists = new ArrayList<>();
        Integer seqNo = 0;

        for (ShelfContentDataApiRsp.ContentData.DataDetails dataDetail : dataDetails) {
            if (dataDetail == null) continue;

            ShelfTemplateMerchantDealRsp.DealList dealList = new ShelfTemplateMerchantDealRsp.DealList();
            seqNo++;

            String contentType = Optional.ofNullable(dataDetail.getContent_type())
                .map(type -> {
                    switch (type) {
                        case "trueyoumerchant": return "MERCHANT";
                        case "truearticle": 
                            return Optional.ofNullable(dataDetail.getSetting())
                                .map(setting -> setting.getThematic_main_shelf_ids())
                                .filter(ids -> !ids.isEmpty())
                                .map(ids -> "THEMATIC")
                                .orElse("ARTICLE");
                        case "privilege": return "DEAL";
                        default: return null;
                    }
                })
                .orElse(null);

            ShelfTemplateMerchantDealRsp.ThumbnailList thumbnailList = new ShelfTemplateMerchantDealRsp.ThumbnailList();
            thumbnailList.setThumbnail16x9(
                Optional.ofNullable(dataDetail.getThumb_list())
                    .map(thumbList -> thumbList.getHighlight16x9())
                    .orElse(null)
            );

            dealList.setSeqNo(seqNo);
            dealList.setContentType(contentType);
            dealList.setCampaignId(dataDetail.getId());
            dealList.setCampaignCode(dataDetail.getCampaign_code());
            dealList.setTimeCounterFlag(
                Optional.ofNullable(dataDetail.getInfo())
                    .map(info -> info.getTime_counter_show())
                    .orElse(null)
            );
            dealList.setThumbnailList(thumbnailList);
            dealList.setCampaignName(
                Optional.ofNullable(dataDetail.getInfo())
                    .map(info -> Constant.TH.equals(lang) ? info.getMerchant_name_th() : info.getMerchant_name_en())
                    .orElse(null)
            );
            dealList.setCampaignDescription(dataDetail.getTitle());
            dealList.setCampaignExpireDate(dataDetail.getExpire_date());
            dealList.setCampaignType(dataDetail.getCampaign_type());
            dealList.setCardType(dataDetail.getCard_type());
            dealList.setRegularPoint(
                Optional.ofNullable(dataDetail.getRedeem_point())
                    .map(Integer::valueOf)
                    .orElse(0)
            );
            dealList.setOfferPoint(
                Optional.ofNullable(dataDetail.getRedeem_point())
                    .map(Integer::valueOf)
                    .orElse(0)
            );

            dealLists.add(dealList);
        }

        return dealLists;
    }

    private EndpointResultRWD fetchTrueContent(Map<String, Object> tv, String cmsId, String lang, Integer maxRow) throws Exception {
        log.info("fetchTrueContent");
        tv.put("country", "th"); //th
        tv.put("lang", lang);
        tv.put("expand", "privilege_list");
        tv.put("expand_limit", maxRow);
        tv.put("fields", "show_redeem_button,privilege_list,card_type,campaign_type,sub_campaign_type,campaign_code,detail,redeem_point,thumb_list,expire_date,info,allow_app,setting,term_and_condition");
        tv.put("cms_id", cmsId);
        tv.put("action",Constant.ELIGIBLE);
        redeemContentEndpoint.getRedeemContentApi(tv);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

}
