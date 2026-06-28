package th.co.truecorp.commonapi.reward.service;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSystemConfig;
import th.co.truecorp.commonapi.reward.cms.jpa.service.ErrorService;
import th.co.truecorp.commonapi.reward.cms.jpa.service.RwdSystemConfigService;
import th.co.truecorp.commonapi.reward.common.endpoint.CommonServiceEndpoint;
import th.co.truecorp.commonapi.reward.common.model.IdAndType;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.endpoint.*;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonapi.reward.model.endpoint.LoyaltyBurnRsp;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemEligibleAndRedeem.req.EligibleAndRedeemBody;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemEligibleAndRedeem.req.EligibleAndRedeemReward;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.model.EndpointResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RedeemDetailService {

    private static Logger log = LoggerFactory.getLogger(RedeemDetailService.class);
    Gson gson = new Gson();

    @Autowired
    private ResultService resultService;

    @Autowired
    private RewardUtill rewardUtill;

    @Autowired
    private APIGWUtill apigwUtill;

    @Autowired
    private RedeemContentEndpoint redeemContentEndpoint;

    @Autowired
    private RedeemDtacCampaignDetailEndpoint redeemDtacCampaignDetailEndpoint;

    @Autowired
    private RedeemDtacLoyaltyBurnEndpoint redeemDtacLoyaltyBurnEndpoint;

    @Autowired
    private CommonServiceEndpoint commonServiceEndpoint;

    @Autowired
    private RwdSystemConfigService rwdSystemConfigService;

    @Autowired
    RedeemTrueBodyEligibleAndRedeemEndpoint redeemTrueBodyEligibleAndRedeemEndpoint;

    @Autowired
    private ErrorService errorService;

    public EndpointResultRWD getDetail(Map<String, Object> tv , String brand) throws Exception {
        log.info("Starting getDetail - Brand: {}", brand);
        EndpointResultRWD endpointResultRwd = new EndpointResultRWD();

        tv.put(ComnConst.KEY_LANGUAGE, rewardUtill.handleLanguage(tv.get(ComnConst.KEY_LANGUAGE).toString()));
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
        String campaignId = Optional.ofNullable(tv.get("campaignId")).map(Object::toString).orElse("");
        String campaignCode = Optional.ofNullable(tv.get("campaignCode")).map(Object::toString).orElse(null);
        String timeCounterFlag = Optional.ofNullable(tv.get("timeCounterFlag")).map(Object::toString).orElse("");

        log.info("Lang: {}, CampaignId: {}, CampaignCode: {}, TimeCounterFlag: {}", lang, campaignId, campaignCode, timeCounterFlag);

        RedeemDealDetailRsp redeemDealDetailRsp = new RedeemDealDetailRsp();
        try {

            if(apigwUtill.isDtac(brand)){
                log.info("Handling Dtac brand.");
                try {
                    endpointResultRwd = fetchDtacShelfDtacCampaignDetail(tv, campaignId);
                    log.info("Dtac campaign detail fetched successfully.");
                }catch (Exception e){
                    log.info("API GW CampaignDetail not connect :{}", e.getMessage(), e);
                }

                if(Optional.ofNullable(endpointResultRwd)
                        .map(result -> result.getHttpStatus() == 200)
                        .orElse(false)){
                    redeemDealDetailRsp = mapCampaignDetailToRedeemDealDetailRsp(tv, campaignId, campaignCode, (List<ShelfDtacCampaignDetailApiRsp>) Optional.ofNullable(tv.get("GetShelfDtacCampaignDetail"))
                            .orElse(Collections.emptyList()));
                }else{
                    redeemDealDetailRsp = null;
                    log.info("Dtac campaign detail response has a non-success HTTP status.");
                }
            }else{
                log.info("Handling True brand.");
                String fields = "show_redeem_button,privilege_list,card_type,campaign_type,sub_campaign_type,campaign_code,detail,redeem_point,thumb_list,expire_date,info,allow_app,setting,term_and_condition";
                endpointResultRwd = fetchShelfContent(tv, campaignId, lang, -1, fields);
                log.info("Redeem detail fetched successfully.");

                ShelfContentDataApiRsp.ContentData.DataDetails contentDetails = (ShelfContentDataApiRsp.ContentData.DataDetails) tv.get("GetContent");

                if(Optional.ofNullable(endpointResultRwd)
                        .map(result -> result.getHttpStatus() == 200)
                        .orElse(false)) {
                    redeemDealDetailRsp = mapContentToRedeemDealDetailRsp(tv, campaignId, campaignCode, contentDetails);
                }else{
                    redeemDealDetailRsp = null;
                    log.info("Redeem detail response has a non-success HTTP status.");
                }

            }

            if (redeemDealDetailRsp != null) {
                endpointResultRwd = errorService.convertMapResult(resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA));
            }else {
                EndpointResult endpointResult = (EndpointResult) tv.get("endpointResult");
                endpointResultRwd = errorService.convertMapResult(endpointResult);
            }
            tv.put("err", endpointResultRwd);
            tv.put(Constant.TRANSACTION_RESPONSE_KEY,redeemDealDetailRsp);

            log.info("endpointResult : "+endpointResultRwd);
        } catch (Exception e) {
            log.info("Error in getDetail: {}", e.getMessage(), e);
            endpointResultRwd = errorService.convertMapResult(errorService.mapErrorException(e,tv));
            return endpointResultRwd;
        }

        return endpointResultRwd;
    }

    private RedeemDealDetailRsp mapContentToRedeemDealDetailRsp(Map<String, Object> tv, String campaignId, String campaignCode, ShelfContentDataApiRsp.ContentData.DataDetails contentDetails){

        RedeemDealDetailRsp dealLists = new RedeemDealDetailRsp();
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();

        if(contentDetails != null){

            ShelfContentDataApiRsp.InfoData info =  contentDetails.getInfo();

            dealLists.setCampaignId(campaignId);
            dealLists.setCampaignCode(contentDetails.getCampaign_code());
            dealLists.setPartnerImage(null);
            String highlight16x9 = Optional.ofNullable(contentDetails.getThumb_list())
                    .map(ShelfContentDataApiRsp.ThumbList::getHighlight16x9)
                    .orElse(null);
            dealLists.setHighLight(highlight16x9);
            dealLists.setRegularPoint(contentDetails.getRedeem_point());
            dealLists.setOfferPoint(null);
            dealLists.setCampaignName(info ==null? null: lang.equalsIgnoreCase(Constant.TH)
                    ? info.getMerchant_name_th() : info.getMerchant_name_en());
            dealLists.setCampaignDescription(contentDetails.getTitle());
            dealLists.setCampaignExpiryDate(contentDetails.getExpire_date());
            dealLists.setCampaignMessage(null);
            dealLists.setCampagnInfo(mapTrueCampagnInfo(lang, contentDetails.getTitle(), contentDetails.getDetail(), null));
            dealLists.setCampaignType(contentDetails.getCampaign_type());
            dealLists.setCardType(contentDetails.getCard_type());
            dealLists.setDisplayRedeem(true);
//            dealLists.setShowBottomBar(true);

            if(info!=null&&info.getTime_counter_show().equals("Y")){
                try {
                    String fields = "setting";
                    String campaignIdE = campaignId;
                    // ---------- test time conter error --------------
//                    if(campaignId.equals("qP5qVQXbeEvP")){
//                        campaignIdE = campaignId+"XXX";
//                    }
                    // ---------- end test time conter error ----------
                    EndpointResultRWD endpointResultRwd = fetchShelfContent(tv, campaignIdE, lang, -1, fields);
                    if(Optional.ofNullable(endpointResultRwd)
                            .map(result -> result.getHttpStatus() == 200)
                            .orElse(false)) {
                        log.info("is success API Content");
                        ShelfContentDataApiRsp.ContentData.DataDetails dataDetails = (ShelfContentDataApiRsp.ContentData.DataDetails) Optional.ofNullable(tv.get("GetContent")).orElse(null);
                        if(dataDetails != null) {
                            dealLists.setTimeCounterErr("N");
                            dealLists.setCouponTimeCouter(mapTrueTimeCouter(dataDetails));
                        }else{
                            dealLists.setTimeCounterErr("Y");
                            dealLists.setCouponTimeCouter(null);
                        }
                    }else {
                        log.info("is not success API Content");
                        dealLists.setTimeCounterErr("Y");
                        dealLists.setCouponTimeCouter(null);
                    }
                }catch (Exception e){
                    dealLists.setTimeCounterErr("Y");
                    dealLists.setCouponTimeCouter(null);
                    log.info("API GW Content Time_counter_show not connext : " + e);
                }
            }else{
                dealLists.setTimeCounterErr("N");
                dealLists.setCouponTimeCouter(null);
            }

            try{
                EligibleAndRedeemBody body = mapEligibleAndRedeemBody(tv, contentDetails.getCampaign_code());

                if(null != body.getId()){
                    Map<String,Object> pathParamLoyalty = new HashMap<>();
                    Map<String,Object> queryParamLoyalty = new HashMap<>();
                    EndpointResultRWD endpointResult = fetchEligibleAndRedeem(tv, pathParamLoyalty, queryParamLoyalty, body);
                    log.info("[EligibleAndRedeem] : "+ new Gson().toJson(tv.get(Constant.ENDPOINT_SERVICE_GET_REDEEM_ELIGIBLE_AND_REDEEM)));
//                EligibleAndRedeemResponse eligibleAndRedeemResponse = (EligibleAndRedeemResponse) tv.get(Constant.ENDPOINT_SERVICE_GET_REDEEM_ELIGIBLE_AND_REDEEM);
                    if(endpointResult.getHttpStatus() == 200) {
                        log.info("is success API EligibleAndRedeem");
                        dealLists.setEligibleStatus(true);
//                    dealLists.setEligibleDesc(Objects.equals(tv.get(ComnConst.KEY_LANGUAGE).toString().toUpperCase(), Constant.TH) ? "รับสิทธิ์" : "Redeem");
                        dealLists.setEligibleDesc(null);
                    }else{
                        log.info("is not success API EligibleAndRedeem");
                        dealLists.setEligibleStatus(false);
//                    endpointResult = errorService.mapErrorCode(Constant.ELIGIBLE,tv.get("brand").toString(),eligibleAndRedeemResponse.getCode(),tv.get(ComnConst.KEY_LANGUAGE).toString(),eligibleAndRedeemResponse.getMessage(),null !=  eligibleAndRedeemResponse.getBusinessError() && !eligibleAndRedeemResponse.getBusinessError().isEmpty() ? eligibleAndRedeemResponse.getBusinessError() : Constant.N_A,Constant.BUTTON);
                        dealLists.setEligibleDesc(endpointResult.getEndpointErrorDescription());
                    }
                }else{
                    log.info("not success in getThaiIdOrPhoneNoForTrue");
//                    EndpointResult endpointResult = (EndpointResult) tv.get("endpointResult");
                    dealLists = null;
                }

            }catch (Exception e){
                log.info("Failed API EligibleAndRedeem : "+e.getMessage());
                dealLists.setEligibleStatus(false);
                EndpointResultRWD endpointResultRwd = getErrorForCatch(tv);
                dealLists.setEligibleDesc(endpointResultRwd.getEndpointErrorDescription());
            }

        }else{
            log.info("mapContentToRedeemDealDetailRsp Data not found {} ",contentDetails);
            dealLists = null;
        }

        return dealLists;
    }

    private EndpointResultRWD fetchEligibleAndRedeem(Map<String, Object> tv, Map<String, Object> pathParams, Map<String, Object> queryParams, EligibleAndRedeemBody req) throws Exception {
        redeemTrueBodyEligibleAndRedeemEndpoint.PostEligibleAndRedeem(tv, pathParams, queryParams, req);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

    private EligibleAndRedeemBody mapEligibleAndRedeemBody(Map<String, Object> tv, String cmpCode) throws Exception {
        log.info("map EligibleAndRedeem Body");
        EligibleAndRedeemBody body = new EligibleAndRedeemBody();

        IdAndType idAndType = rewardUtill.getThaiIdOrPhoneNoForTrue(tv);

        if(null != idAndType && null != idAndType.getId() && !idAndType.getId().isEmpty()){
            body.setId(idAndType.getId());
            body.setTrnNo(apigwUtill.generateRewardRedeemBackendIdYYYYMMDD(Constant.TRUEAPP+"_REDEEMB2B_",14));
            body.setIdentifierType(idAndType.getIdnType());
            body.setChannel(Constant.TRUE);
            body.setSimulate(Constant.TRUE);
            List<EligibleAndRedeemReward> eligibleAndRedeemRewardList = new ArrayList<>();
            EligibleAndRedeemReward redeemReward = new EligibleAndRedeemReward();
            redeemReward.setPricePlanCode(cmpCode);
            redeemReward.setQuantity(1);
            eligibleAndRedeemRewardList.add(redeemReward);
            body.setRewards(eligibleAndRedeemRewardList);
            body.setComment("call api from "+Constant.TRUEAPP);
        }

        return body;
    }

    private RedeemDealDetailRsp.CampagnInfo mapTrueCampagnInfo(String lang, String detail, String condition, List<String> branch){

        RedeemDealDetailRsp.CampagnInfo campagnInfo = new RedeemDealDetailRsp.CampagnInfo();

        campagnInfo.setDetail(detail);
        campagnInfo.setCondition(condition);
//        campagnInfo.setBranch(branch);

        return campagnInfo;
    }

    private RedeemDealDetailRsp.CouponTimeCouter mapTrueTimeCouter(ShelfContentDataApiRsp.ContentData.DataDetails dataDetails) {
        RedeemDealDetailRsp.CouponTimeCouter couponTimeCouter = null;

        if(dataDetails != null){
            ShelfContentDataApiRsp.InfoData info =dataDetails.getInfo();
            if(info!=null&&info.getTime_counter_show().equals("N")){
                couponTimeCouter = null;
            }else{
                String value = "30";
                try{
                    log.info("get RwdSystemConfig TIMECOUNTER and TRUE");
                    Optional<RwdSystemConfig> optional = rwdSystemConfigService.findConfigs("TIMECOUNTER","TRUE");
                    RwdSystemConfig rwdSystemConfig = optional.get();
                    value = rwdSystemConfig.getValue();
                }catch (Exception e){
                    log.info("Connot Get RwdSystemConfig is : " + e);
                }
                couponTimeCouter = new RedeemDealDetailRsp.CouponTimeCouter();
                if(dataDetails.getSetting() == null){
                    couponTimeCouter.setDay(null);
                    couponTimeCouter.setHour(null);
                    couponTimeCouter.setMin(value);
                }else{
                    if(Optional.ofNullable(dataDetails.getSetting().getTime_counter_show()).isPresent()  && !dataDetails.getSetting().getTime_counter_show().isEmpty()) {
                        Integer remainingDays = Integer.valueOf(dataDetails.getSetting().getTime_counter_show());
                        Integer min = (remainingDays)%60;
                        Integer hour = ((remainingDays)/60)%24;
                        Integer day = ((remainingDays)/60)/24;

                        couponTimeCouter.setDay(day.toString());
                        couponTimeCouter.setHour(hour.toString());
                        couponTimeCouter.setMin(min.toString());
                    }else{
                        couponTimeCouter.setDay(null);
                        couponTimeCouter.setHour(null);
                        couponTimeCouter.setMin(value);
                    }
                }
            }
        }

        return couponTimeCouter;
    }

    private RedeemDealDetailRsp mapCampaignDetailToRedeemDealDetailRsp(Map<String, Object> tv, String campaignId, String campaignCode, List<ShelfDtacCampaignDetailApiRsp> detailLists){
        log.info("map CampaignDetail To RedeemDealDetail");
        RedeemDealDetailRsp dealLists = new RedeemDealDetailRsp();
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();

        if(!detailLists.isEmpty()){
            for(ShelfDtacCampaignDetailApiRsp detailList:detailLists){
                String campaignType = mapCampaignType(detailList.getType());

                String setcampaignCode = campaignType.equals(Constant.DTAC_REWARD) ? campaignId : detailList.getBzbId();
                log.info("set campaignCode : " + setcampaignCode);
//                List<String> type = Arrays.asList("WELCOME","SILVER","GOLD","PLATINUM_BLUE");
                List<String> cardType = List.of("no card");//new ArrayList<>();

                if(detailList.getCharacteristic() != null){
                    for(ShelfDtacCampaignDetailApiRsp.characteristic characteristic:detailList.getCharacteristic()){
                        if(characteristic.getName().equals("userLevel")){
                            log.info("userLevel : "+characteristic.getValue());
                            cardType = rewardUtill.levelCardType(characteristic.getValue());
                        }
                    }
                }

                Integer totalPoint = 0;

                try {
                    tv.put("productBrand", tv.get("brand"));
                    EndpointResult resultProfile = commonServiceEndpoint.getCommonService(tv);

                    if(resultProfile.getHttpStatus() == 200){
                        log.info("success get Profile Endpoint");
                        CustomerProfileRsp commonProfileRsp = (CustomerProfileRsp) tv.get("commonProfileRspEndpoint");
//                        String profiletypesss = commonProfileRsp.getMyPoint();
                        totalPoint = commonProfileRsp.getMyPoint().getTotalPoint();
//                        String profiletype = commonProfileRsp.getCardType().toUpperCase();
//                        for(String t:type){
//                            cardType.add(t);
//                            if(profiletype.equals(t)){
//                                break;
//                            }
//                        }
                    }else{
                        tv.put("endpointResult",resultProfile);
                        return dealLists = null;
                    }
                }catch (Exception e){
                    log.info("Error get Profile is "+e.getMessage());
                }

                Integer regularPoint = null == detailList.getOriginalPoint() || detailList.getOriginalPoint() == 0 ? detailList.getPointPerUnit() : detailList.getOriginalPoint();
                Integer offerPoint = null == detailList.getOriginalPoint() || detailList.getOriginalPoint() == 0 ? null : detailList.getPointPerUnit();

                log.info("regularPoint is {}",regularPoint);
                log.info("offerPoint is {}",offerPoint);
                log.info("totalPoint is {}",totalPoint);

                dealLists.setCampaignId(campaignId);
                dealLists.setCampaignCode(setcampaignCode);
                dealLists.setPartnerImage(null);
                dealLists.setHighLight(detailList.getHref());
                dealLists.setRegularPoint(regularPoint != null ? regularPoint.toString() : null);
                dealLists.setOfferPoint(null != offerPoint ? offerPoint.toString() : null);
                dealLists.setCampaignName(detailList.getRelatedParty() != null ? mapCampaignName(lang, detailList.getRelatedParty()) : null);
                dealLists.setCampaignDescription(detailList.getName()!= null ? (lang.equalsIgnoreCase(Constant.TH) ? detailList.getName().getTh() : detailList.getName().getEn()) : null);
                dealLists.setCampaignExpiryDate(detailList.getValidFor().getEndDateTime());
                dealLists.setCouponTimeCouter(detailList.getValidFor() != null ? mapDtacCouponTimeCouter(detailList.getValidFor()) : null);
                dealLists.setCampaignMessage(null);
                dealLists.setCampagnInfo((detailList.getDescription() != null && detailList.getCriteria() != null && detailList.getRelatedParty() != null) ? mapDtacCampagnInfo(lang, detailList.getDescription(), detailList.getCriteria(), detailList.getRelatedParty()) : null);
                dealLists.setCampaignType(campaignType);
                dealLists.setCardType(cardType);
                dealLists.setTimeCounterErr("N");

                if("16".equals(detailList.getOnlyDisplay())){
                    log.info("OnlyDisplay 16");
                    dealLists.setDisplayRedeem(false);
                }else{
                    dealLists.setDisplayRedeem(true);
                }

                EndpointResultRWD endpointResultRwd = new EndpointResultRWD();
                if("9".equals(detailList.getOnlyDisplay())){
                    log.info("OnlyDisplay 9 = Use at shop");
                    dealLists.setEligibleStatus(false);
                    endpointResultRwd = getErrorForOnlyDisplay9(tv);
                    dealLists.setEligibleDesc(endpointResultRwd.getEndpointErrorDescription());
                    log.info(gson.toJson(endpointResultRwd));
                }else if(offerPoint != null && offerPoint > totalPoint){
                    //customer code for dtac 51131
                    //for true 51033
                    log.info("Criteria Mismatch : The customer has points less than the points required for the current campaign.");
                    dealLists.setEligibleStatus(false);
                    endpointResultRwd = getErrorForPointExceed(tv);
                    dealLists.setEligibleDesc(endpointResultRwd.getEndpointErrorDescription());
                    log.info(gson.toJson(endpointResultRwd));
                }else if((offerPoint == null && regularPoint != null)&& regularPoint > totalPoint){
                    //customer code for dtac 51131
                    //for true 51033
                    log.info("Criteria Mismatch : The customer has points less than the points required for the current campaign.");
                    dealLists.setEligibleStatus(false);
                    endpointResultRwd = getErrorForPointExceed(tv);
                    dealLists.setEligibleDesc(endpointResultRwd.getEndpointErrorDescription());
                    log.info(gson.toJson(endpointResultRwd));
                }else{
                    try{
//                        EndpointResult endpointResult = new EndpointResult();
                        endpointResultRwd = fetchDtacRedeemLoyaltyBurn(tv, campaignId, detailList.getBzbId());
                        LoyaltyBurnRsp loyaltyBurn = (LoyaltyBurnRsp) tv.get("getRedeemDtacLoyaltyBurn");
                        if(endpointResultRwd.getHttpStatus()==200){
                            log.info("[getLoyaltyBurn] : "+ new Gson().toJson(loyaltyBurn));
                            log.info("is success API LoyaltyBurn : " + gson.toJson(endpointResultRwd));
                            Boolean status = Boolean.parseBoolean(loyaltyBurn.getStatus());
                            dealLists.setEligibleStatus(status);
                            String redeemStr = Objects.equals(tv.get(ComnConst.KEY_LANGUAGE).toString().toUpperCase(), Constant.TH) ? "รับสิทธิ์" : "Redeem";
                            endpointResultRwd = errorService.mapErrorCode(Constant.ELIGIBLE,tv.get("brand").toString().toUpperCase(),loyaltyBurn.getRemark(),tv.get(ComnConst.KEY_LANGUAGE).toString(),loyaltyBurn.getDescription(),Constant.N_A,Constant.BUTTON);
                            dealLists.setEligibleDesc(status ? null : endpointResultRwd.getEndpointErrorDescription());
                        }else{
                            //เอาโค้ดไปหา error service จาก apigw ไปหาประเภทปุ่ม
                            log.info("is not success API LoyaltyBurn : " + gson.toJson(endpointResultRwd));
                            dealLists.setEligibleStatus(false);
                            dealLists.setEligibleDesc(endpointResultRwd.getEndpointErrorDescription());
                        }
                    }catch (Exception e){
                        //สร้าง error code ใหม่ สำหรับปุ่ม invalid catch
                        dealLists.setEligibleStatus(false);
                        endpointResultRwd = getErrorForCatch(tv);
                        dealLists.setEligibleDesc(endpointResultRwd.getEndpointErrorDescription());
                        log.info("Failed API LoyaltyBurn : "+e.getMessage());
                    }
                }

            }
        }else{
            dealLists = null;
        }

        return dealLists;
    }

    private String mapCampaignType(String type){
        String campaignType = "";

        switch (type) {
            case "1":
                campaignType = Constant.DTAC_REWARD;
                break;
            case "2":
                campaignType = Constant.COIN;
                break;
        }

        return campaignType;
    }

    private String mapCampaignName(String lang , List<ShelfDtacCampaignDetailApiRsp.RelatedParty> relatedPartys){

        String campaignName = "";

        for(ShelfDtacCampaignDetailApiRsp.RelatedParty relatedParty:relatedPartys){
            campaignName = (lang.toUpperCase().equals(Constant.TH) ? relatedParty.getName() : relatedParty.getNameEn());
        }

        return campaignName;
    }

    private RedeemDealDetailRsp.CouponTimeCouter mapDtacCouponTimeCouter(ShelfDtacCampaignDetailApiRsp.ValidFor validFor){

        RedeemDealDetailRsp.CouponTimeCouter couponTimeCouter = null;
        if(validFor.getRemainingDays() != null) {
            if(validFor.getRemainingDays() != 0){
                Integer remainingDays = validFor.getRemainingDays();
                Integer min = (remainingDays)%60;
                Integer hour = ((remainingDays)/60)%24;
                Integer day = ((remainingDays)/60)/24;

                couponTimeCouter = new RedeemDealDetailRsp.CouponTimeCouter();
                couponTimeCouter.setMin(min.toString());
                couponTimeCouter.setHour(hour.toString());
                couponTimeCouter.setDay(day.toString());
            }
        }

        return couponTimeCouter;
    }

    private RedeemDealDetailRsp.CampagnInfo mapDtacCampagnInfo(String lang, ShelfDtacCampaignDetailApiRsp.Description description, ShelfDtacCampaignDetailApiRsp.Criteria criteria, List<ShelfDtacCampaignDetailApiRsp.RelatedParty> relatedPartys){

        RedeemDealDetailRsp.CampagnInfo campagnInfo = new RedeemDealDetailRsp.CampagnInfo();

        campagnInfo.setDetail(lang.toUpperCase().equals(Constant.TH) ? description.getTh() : description.getEn());
        campagnInfo.setCondition(lang.toUpperCase().equals(Constant.TH) ? criteria.getTh() : criteria.getEn());

        for(ShelfDtacCampaignDetailApiRsp.RelatedParty relatedParty:relatedPartys){
            campagnInfo.setBranch(lang.toUpperCase().equals(Constant.TH) ? relatedParty.getBranch() : relatedParty.getBranchEn());
        }

        return campagnInfo;
    }

    private EndpointResultRWD fetchShelfContent(Map<String, Object> tv, String cmsId, String lang, Integer maxRow, String fields) throws Exception {
        log.info("Fetching shelf content - CmsId: {}, Lang: {}, MaxRow: {}, Fields: {}", cmsId, lang, maxRow, fields);

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

    private EndpointResultRWD fetchDtacShelfDtacCampaignDetail(Map<String, Object> tv, String cmsId) throws Exception {
        log.info("Fetching Dtac campaign detail - CmsId: {}", cmsId);

        tv.put("txid",apigwUtill.generateRewardRedeemBackendId("RWDTRUEAPP_CPGBZB_"));
        tv.put("id",cmsId);
        redeemDtacCampaignDetailEndpoint.getRedeemDtacCampaignDetail(tv);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

    private EndpointResultRWD fetchDtacRedeemLoyaltyBurn(Map<String, Object> tv, String cmsId, String cmsCode) throws Exception {
        log.info("Fetching Dtac loyalty burn - CmsId: {}, CmsCode: {}", cmsId, cmsCode);

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String strDate = dateFormat.format(date);

        tv.put("id",apigwUtill.generateRewardRedeemBackendId("RWDTRUEAPP_B2B_"));
        tv.put("href","-");
        tv.put("user.id",tv.get("digitalId"));
        tv.put("user.href",tv.get("digitalId"));
        tv.put("privilege.function",Constant.ENDPOINT_SOURCE_SYSTEM_ID);
        tv.put("requestDateTime",strDate);
        tv.put("lang", tv.get(ComnConst.KEY_LANGUAGE).equals(Constant.TH) ? "THAI" : "ENG");//THAI
        tv.put("loyaltyProgramProduct.id",cmsId);
        tv.put("loyaltyProgramProduct.productSerialNumber",cmsCode);//911231
        tv.put("relatedParty.id",Constant.TRUEAPP);
        tv.put("relatedParty.name",Constant.TRUEAPP);//LLTRUEAPP
        redeemDtacLoyaltyBurnEndpoint.getRedeemDtacLoyaltyBurn(tv);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

   public EndpointResultRWD getErrorForPointExceed(Map<String, Object> tv) {

        EndpointResultRWD endpointResultRwd = new EndpointResultRWD();
        try {
            boolean isDtac = apigwUtill.isDtac(tv.get("brand").toString().toLowerCase());
            endpointResultRwd = errorService.mapErrorCode(Constant.ELIGIBLE,
                    tv.get("brand").toString().toUpperCase(),
                    isDtac ? Constant.ERROR_CODE_POINT_LIMIT_DTAC : Constant.ERROR_CODE_POINT_LIMIT_TRUE,
                    tv.get(ComnConst.KEY_LANGUAGE).toString(),
                    isDtac ? "Insufficient account balance" : "Transaction canceled due to business error: Campaign point limit exceed",
                    isDtac ? Constant.N_A : "14001",
                    Constant.BUTTON);
        } catch (Exception e) {
            endpointResultRwd = errorService.convertMapResult(errorService.mapErrorException(e, tv));
        }
        return endpointResultRwd;
    }

   public EndpointResultRWD getErrorForCatch(Map<String, Object> tv) {

        EndpointResultRWD endpointResultRwd = new EndpointResultRWD();
        try {
            endpointResultRwd = errorService.mapErrorCode(Constant.ELIGIBLE,
                    tv.get("brand").toString().toUpperCase(),
                    Constant.ERROR_CODE_FOR_CATCH,
                    tv.get(ComnConst.KEY_LANGUAGE).toString(),
                    "An error occurred. Please try again.",
                    Constant.N_A,
                    Constant.BUTTON);
        } catch (Exception e) {
            endpointResultRwd = errorService.convertMapResult(errorService.mapErrorException(e, tv));
        }
        return endpointResultRwd;
    }

   public EndpointResultRWD getErrorForOnlyDisplay9(Map<String, Object> tv) {

        EndpointResultRWD endpointResultRwd = new EndpointResultRWD();
        try {
            endpointResultRwd = errorService.mapErrorCode(Constant.ELIGIBLE,
                    tv.get("brand").toString().toUpperCase(),
                    Constant.ERROR_CODE_ONLY_DISPLAY_DTAC,
                    tv.get(ComnConst.KEY_LANGUAGE).toString(),
                    "OnlyDisplay:9 = Use at shop",
                    Constant.N_A,
                    Constant.BUTTON);
        } catch (Exception e) {
            endpointResultRwd = errorService.convertMapResult(errorService.mapErrorException(e, tv));
        }
        return endpointResultRwd;
    }
}
