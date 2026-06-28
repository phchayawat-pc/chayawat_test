package th.co.truecorp.commonapi.reward.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.service.CustomMappingMessageService;
import th.co.truecorp.commonapi.reward.cms.jpa.service.ErrorService;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.endpoint.*;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemCampaignDetail.CampaignDetailResponse;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemCampaignDetail.CampaignInfo;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemCampaignDetail.CouponDetail;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemContent.ContentData;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemContent.ContentResponse;
import th.co.truecorp.commonapi.reward.model.redeem.RedeemDealScanCodeConditionResponse;
import th.co.truecorp.commonapi.reward.model.redeem.RedeemDealScanCodeResponse;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.context.ContextSignature;
import th.co.truecorp.commonlib.log.context.LogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class RedeemPrizeService {

    private static Logger log = LoggerFactory.getLogger(RedeemPrizeService.class);

    @Autowired
    private ResultService resultService;

    @Autowired
    private RewardUtill rewardUtill;

    @Autowired
    private APIGWUtill apigwUtill;

    @Autowired
    private LogContextService logContextService;

    @Autowired
    RedeemDtacScanCodeEndpoint redeemDtacScanCodeEndpoint;

    @Autowired
    RedeemTrueScanCodeEndpoint redeemTrueScanCodeEndpoint;

    @Autowired
    private ErrorService errorService;

    @Autowired
    private CustomMappingMessageService customMappingMessageService;

    public EndpointResultRWD getRedeemDealScanCode(Map<String, Object> tv, String brand, String lang, String cmpgIdEnc) throws Exception {
        final LogContext logContext = logContextService.getCurrentContext();

        log.info("Starting getRedeemDealScanCode - Brand: {}, Lang: {}, CampaignId: {}", brand, lang, cmpgIdEnc);

        lang = rewardUtill.handleLanguage(lang);
        log.info("Processed language: {}", lang);

        EndpointResultRWD endpointResultRwd = null;
        RedeemDealScanCodeResponse response = new RedeemDealScanCodeResponse();
        ContextSignature contextSignature = logContextService.getCurrentContextSignature();

        try {

            if (apigwUtill.isDtac(brand)) {
                log.info("Handling Dtac brand for CampaignId: {}", cmpgIdEnc);

                Map<String,Object> queryParam = mapRedeemCampaignDetailQueryParam(tv, cmpgIdEnc);
                log.debug("Dtac query parameters: {}", queryParam);

                CompletableFuture<EndpointResultRWD> future = CompletableFuture.supplyAsync(() -> {
                    logContextService.joinContext(contextSignature);
                    EndpointResultRWD endpointResultRwd1 = null;
                    try {
                        log.info("Fetching redeem campaign details asynchronously.");
                        endpointResultRwd1 = fetchRedeemCampaignDetail(tv, queryParam, contextSignature);
                    } catch (Exception e) {
                        log.error("Error in fetching Dtac campaign details: {}", e.getMessage(), e);
                    }

                    return endpointResultRwd1;
                });
                endpointResultRwd = future.join();

                log.info("Mapping campaign details for Dtac brand.");
                response = mapCampaignInfo(tv, lang, cmpgIdEnc);
            } else {
                log.info("Handling True brand for CampaignId: {}", cmpgIdEnc);
                Map<String,Object> queryParam = mapRedeemContentQueryParam(tv, lang, cmpgIdEnc);
                log.debug("True query parameters: {}", queryParam);

                log.info("Fetching redeem content for True brand.");
                endpointResultRwd = fetchRedeemContent(tv, queryParam);

                log.info("Mapping content details for True brand.");
                response = mapContent(tv, lang, cmpgIdEnc);
            }

            tv.put(Constant.TRANSACTION_RESPONSE_KEY,response);

             if (response == null) {
                 log.error("Redeem deal scan code response is null.");
             } else {
                 log.info("Redeem deal scan code response mapped successfully.");
                EndpointResult endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                endpointResultRwd = errorService.convertMapResult(endpointResult);
                 log.info("Endpoint result mapped successfully.");
             }

        } catch (Exception e) {
            log.info("Exception in getRedeemDealScanCode: {}", e.getMessage(), e);
            return errorService.convertMapResult(errorService.mapErrorException(e,tv));
        }
        return endpointResultRwd;
    }

    private Map<String, Object> mapRedeemCampaignDetailQueryParam(Map<String, Object> tv, String cmpgId) throws Exception {
        log.debug("Mapping query parameters for Dtac campaign detail - CampaignId: {}", cmpgId);
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("txid", apigwUtill.generateRewardBackendId());
        queryParams.put("id", cmpgId);
        return queryParams;
    }

    private Map<String, Object> mapRedeemContentQueryParam(Map<String, Object> tv, String lang, String cmpgId) throws Exception {
        log.debug("Mapping query parameters for True content - CampaignId: {}", cmpgId);
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("country", "th");
        queryParams.put("lang", lang);
        queryParams.put("expand", "privilege_list");
        queryParams.put("expand_limit", -1);
        queryParams.put("fields", "show_redeem_button,privilege_list,card_type,campaign_type,sub_campaign_type,campaign_code,detail,redeem_point,thumb_list,expire_date,info,allow_app,setting,term_and_condition");
        queryParams.put("cms_id", cmpgId);
        return queryParams;
    }

   public EndpointResultRWD fetchRedeemCampaignDetail(Map<String, Object> tv, Map<String, Object> queryParams, ContextSignature contextSignature) throws Exception {
        log.info("Fetching Dtac campaign detail.");
        redeemDtacScanCodeEndpoint.getDtacCampaignDetailApi(tv,queryParams, contextSignature);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

    private EndpointResultRWD fetchRedeemContent(Map<String, Object> tv, Map<String, Object> queryParams) throws Exception {
        log.info("Fetching True redeem content.");
        redeemTrueScanCodeEndpoint.getRedeemTrueScanCodeEndpoint(tv,queryParams);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

    private RedeemDealScanCodeResponse mapCampaignInfo(Map<String, Object> tv, String lang, String cmpgId) throws Exception {
        log.info("Mapping campaign info - CampaignId: {}", cmpgId);
        RedeemDealScanCodeResponse rsp = new RedeemDealScanCodeResponse();
        CampaignDetailResponse campaignDetailResponse = (CampaignDetailResponse) tv.get(Constant.ENDPOINT_SERVICE_GET_CAMPAIGN_DETAIL);
        List<CampaignInfo> responseList = campaignDetailResponse != null ? campaignDetailResponse.getCampaignInfo() : null;
        if(responseList != null && !responseList.isEmpty()){
            log.info("campaign id = " + responseList.get(0).getId());
            for (CampaignInfo response: responseList){

                String campaignName = null;
                if (response.getRelatedParty() != null && !response.getRelatedParty().isEmpty()) {
                    campaignName = lang.equalsIgnoreCase(Constant.TH)
                            ? response.getRelatedParty().get(0).getName()
                            : response.getRelatedParty().get(0).getNameEn();
                }

                String campaignDescription = null;
                if (response.getName() != null) {
                    campaignDescription = lang.equalsIgnoreCase(Constant.TH)
                            ? response.getName().getTh()
                            : response.getName().getEn();
                }

                rsp.setCampaignId(cmpgId);
                rsp.setCampaignName(campaignName);
                rsp.setCampaignDescription(campaignDescription);
                rsp.setTimeCounterFlag("Y");
                rsp.setCouponCode("");
                rsp.setCouponExpiryDate(response.getValidFor() != null ? response.getValidFor().getEndDateTime() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                rsp.setPrivilegeType("");

                rsp.setConditionInfo(mapConditionInfo(lang, response.getCouponDetail(), campaignName));

                String bottomLink = customMappingMessageService.getMappingMessage(Constant.MESSAGE_BUTTOM_LINK, Constant.MESSAGE, lang, Constant.MESSAGE);
                rsp.setBottomLink(bottomLink);
                String textButton = customMappingMessageService.getMappingMessage(Constant.MESSAGE_TEXT_BUTTOM, Constant.BUTTON, lang, Constant.MESSAGE);
                rsp.setTextButton(textButton); //if no data then null
            }
        } else {
            log.info("No campaign info found.");
            rsp = null;
        }
        return rsp;
    }

    private List<RedeemDealScanCodeConditionResponse> mapConditionInfo(String language , CouponDetail couponDetail , String partnerName){

        List<RedeemDealScanCodeConditionResponse> conditionList = new ArrayList<RedeemDealScanCodeConditionResponse>();

        if (couponDetail != null && ((couponDetail.getCouponApp_url() == null || "null".equals(couponDetail.getCouponApp_url())) &&
                (couponDetail.getCouponWeb_url() == null || "null".equals(couponDetail.getCouponWeb_url())))) {

            RedeemDealScanCodeConditionResponse condition = new RedeemDealScanCodeConditionResponse();
            condition.setType(Constant.TEXT);
            condition.setMessage(partnerName);
            condition.setUrl("");
            condition.setUrlName("");
            conditionList.add(condition);
        }

        if(couponDetail != null && (!(couponDetail.getCouponApp_url() == null || "null".equals(couponDetail.getCouponApp_url())))){
            RedeemDealScanCodeConditionResponse condition = new RedeemDealScanCodeConditionResponse();
            condition.setType(Constant.BUTTON);
            condition.setUrl(couponDetail.getCouponApp_url().replace("\"", ""));
            condition.setUrlName("");

            switch (language) {
                case Constant.TH:
                    condition.setMessage(couponDetail.getCouponApp_th().replace("\"", ""));
                    break;
                case Constant.MY:
                    condition.setMessage(couponDetail.getCouponApp_my().replace("\"", ""));
                    break;
                case Constant.KM:
                    condition.setMessage(couponDetail.getCouponApp_km().replace("\"", ""));
                    break;
                default :
                    condition.setMessage(couponDetail.getCouponApp_en().replace("\"", ""));
                    break;
            }
            conditionList.add(condition);
        }

        if(couponDetail != null && (!(couponDetail.getCouponWeb_url() == null || "null".equals(couponDetail.getCouponWeb_url())))){
            RedeemDealScanCodeConditionResponse condition = new RedeemDealScanCodeConditionResponse();
            String message = customMappingMessageService.getMappingMessage(Constant.MESSAGE_CONDITION_LINK, Constant.MESSAGE, language, Constant.MESSAGE);
            condition.setMessage(message);
            condition.setType(Constant.LINK);
            condition.setUrl(couponDetail.getCouponWeb_url().replace("\"", ""));

            switch (language) {
                case Constant.TH:
                    condition.setUrlName(couponDetail.getCouponWeb_th().replace("\"", ""));
                    break;
                case Constant.MY:
                    condition.setUrlName(couponDetail.getCouponWeb_my().replace("\"", ""));
                    break;
                case Constant.KM:
                    condition.setUrlName(couponDetail.getCouponWeb_km().replace("\"", ""));
                    break;
                default :
                    condition.setUrlName(couponDetail.getCouponWeb_en().replace("\"", ""));
                    break;
            }
            conditionList.add(condition);
        }
        return conditionList;
    }

    private RedeemDealScanCodeResponse mapContent(Map<String, Object> tv, String lang, String cmpgId) throws Exception {
        log.info("Mapping content info - CampaignId: {}", cmpgId);
        RedeemDealScanCodeResponse rsp = new RedeemDealScanCodeResponse();
        ContentResponse contentResponse = (ContentResponse) tv.get(Constant.ENDPOINT_SERVICE_GET_CONTENT);

        if(contentResponse != null){
            ContentData response = contentResponse.getContent();
//            log.info("campaign id = " + response.getData().getId());
            rsp.setCampaignId(cmpgId);
            if (response != null && response.getData() != null && response.getData().getInfo() != null) {
                if (lang.equalsIgnoreCase(Constant.TH)) {
                    rsp.setCampaignName(response.getData().getInfo().getMerchant_name_th());
                } else {
                    rsp.setCampaignName(response.getData().getInfo().getMerchant_name_en());
                }
            } else {
                rsp.setCampaignName(null);
            }
            rsp.setCampaignDescription(response != null && response.getData() != null ? response.getData().getTitle() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
            rsp.setTimeCounterFlag(response != null && response.getData() != null && response.getData().getInfo() != null ? response.getData().getInfo().getTime_counter_show() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
            rsp.setCouponCode("");
            rsp.setCouponExpiryDate("");
            rsp.setPrivilegeType("");

            List<RedeemDealScanCodeConditionResponse> conditionRsp = new ArrayList<>();
            RedeemDealScanCodeConditionResponse condition = new RedeemDealScanCodeConditionResponse();
            condition.setType("TEXT");
            condition.setMessage("");
            condition.setUrl("");
            condition.setUrlName("");
            conditionRsp.add(condition);

            rsp.setConditionInfo(conditionRsp);

            String bottomLink = customMappingMessageService.getMappingMessage(Constant.MESSAGE_BUTTOM_LINK, Constant.MESSAGE, lang, Constant.MESSAGE);
            rsp.setBottomLink(bottomLink);
            String textButton = customMappingMessageService.getMappingMessage(Constant.MESSAGE_TEXT_BUTTOM, Constant.BUTTON, lang, Constant.MESSAGE);
            rsp.setTextButton(textButton); //if no data then null

        } else {
            log.info("campaign id is null ");
            rsp = null;
        }
        return rsp;
    }

}
