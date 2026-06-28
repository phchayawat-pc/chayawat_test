package th.co.truecorp.commonapi.reward.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.service.ErrorService;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.endpoint.*;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;

import java.util.*;

@Service
public class RedeemArticDetailService {

    private static Logger log = LoggerFactory.getLogger(RedeemArticDetailService.class);

    @Autowired
    private ResultService resultService;

    @Autowired
    private RewardUtill rewardUtill;

    @Autowired
    private APIGWUtill apigwUtill;

    @Autowired
    private RedeemContentEndpoint redeemContentEndpoint;

    @Autowired
    private ErrorService errorService;


    private EndpointResultRWD fetchShelfContent(Map<String, Object> tv, String cmsId, String lang, Integer maxRow, String fields) throws Exception {
        tv.put("country", "th");
        tv.put("lang", lang);
        tv.put("expand", "privilege_list");
        tv.put("expand_limit", maxRow);
        tv.put("fields", fields);
        tv.put("cms_id", cmsId);
        tv.put("action",Constant.ELIGIBLE);
        redeemContentEndpoint.getRedeemContentApi(tv);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

    public EndpointResultRWD getArticleDetail(Map<String, Object> tv , String brand) throws Exception {

        log.info("Starting getArticleDetail operation - Brand: {}", brand);

        EndpointResultRWD endpointResultRwd = new EndpointResultRWD();
        tv.put(ComnConst.KEY_LANGUAGE, rewardUtill.handleLanguage(tv.get(ComnConst.KEY_LANGUAGE).toString()));
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
        String campaignId = tv.get("campaignId") != null ? tv.get("campaignId").toString() : "";
        log.info("Language: {}, CampaignId: {}", lang, campaignId);

        RedeemArticelDetailRsp articelDetailRsp = new RedeemArticelDetailRsp();
        try {
            if(apigwUtill.isDtac(brand)){
                log.info("Brand is Dtac. Returning null article detail response.");
                articelDetailRsp = null;
                endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
                        apigwUtill.isDtac(brand) ? "DTAC" : "TRUE",
                        Objects.requireNonNull(Constant.ERROR_TEMPLATE_NOT_SUPPORTED),
                        tv.get(ComnConst.KEY_LANGUAGE).toString(),
                        "Not supported for this template",
                        Constant.N_A,
                        Constant.MESSAGE);
                log.info("Not supported for this template.");
            }else{
                log.info("Brand is NOT Dtac. Fetching shelf content for campaignId: {}", campaignId);
                String fields = "show_redeem_button,privilege_list,card_type,campaign_type,sub_campaign_type,campaign_code,detail,redeem_point,thumb_list,expire_date,info,allow_app,setting,term_and_condition";
                endpointResultRwd = fetchShelfContent(tv, campaignId, lang, -1, fields);

                log.info("Processing shelf content data.");
                ShelfContentDataApiRsp.ContentData.DataDetails dataDetail = (ShelfContentDataApiRsp.ContentData.DataDetails) tv.get("GetContent");

                if(dataDetail != null) {
                    log.info("Shelf content data found. Mapping to RedeemArticleDetailRsp.");
                    articelDetailRsp = mapRedeemArticelDetailRsp(tv, dataDetail);
                }else{
                    log.info("No shelf content data found.");
                    articelDetailRsp = null;
                    endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
                            brand,
                            Objects.requireNonNull(Constant.ERROR_DATA_NOT_FOUND),
                            tv.get(ComnConst.KEY_LANGUAGE).toString(),
                            "data not found",
                            Constant.N_A,
                            Constant.MESSAGE);
                }
            }

            if (articelDetailRsp != null && endpointResultRwd.getEndpointResponseCode() == null) {
                log.debug(Constant.ENDPOINT_SERVICE_GET_SHELF + " is success");
                endpointResultRwd = errorService.convertMapResult(resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA));
            }
            tv.put("err", endpointResultRwd);
            tv.put(Constant.TRANSACTION_RESPONSE_KEY,articelDetailRsp);

            log.info("Returning endpoint result: {}", endpointResultRwd);
        } catch (Exception e) {
            log.error("Exception occurred in getArticleDetail: {}", e.getMessage(), e);
            return errorService.convertMapResult(errorService.mapErrorException(e,tv));
        }

        return endpointResultRwd;
    }

    private RedeemArticelDetailRsp mapRedeemArticelDetailRsp(Map<String, Object> tv , ShelfContentDataApiRsp.ContentData.DataDetails dataDetail){
        log.info("map Content to RedeemArticel");
        RedeemArticelDetailRsp articelDetailRsp = new RedeemArticelDetailRsp();
        if(dataDetail != null){
            articelDetailRsp.setCampaignId(dataDetail.getId());
            articelDetailRsp.setCampaignName(dataDetail.getTitle());
            String highlight16x9 = null;
            if(dataDetail.getThumb_list() != null){
                highlight16x9 = dataDetail.getThumb_list().getHighlight16x9() != null ? dataDetail.getThumb_list().getHighlight16x9() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
            }
            articelDetailRsp.setHighlight(highlight16x9);
            articelDetailRsp.setDetail(dataDetail.getDetail());
        }else{
            articelDetailRsp = null;
        }
        return articelDetailRsp;
    }

}
