package th.co.truecorp.commonapi.reward.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.service.ErrorService;
import th.co.truecorp.commonapi.reward.common.model.IdAndType;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.endpoint.*;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemEligibleAndRedeem.req.EligibleAndRedeemReq;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemEligibleAndRedeem.req.EligibleAndRedeemReward;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemEligibleAndRedeem.res.EligibleAndRedeemResponse;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemEligibleAndRedeem.res.Redemption;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemGame.req.RedeemGameDataApiReq;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemGame.res.RedeemGameResponse;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemLoyaltyBurn.req.*;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemLoyaltyBurn.res.LoyaltyBurnResponse;
import th.co.truecorp.commonapi.reward.model.redeem.RedeemTakeRedeemResponse;
import th.co.truecorp.commonapi.reward.transaction.ClearCacheTransaction;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.log.context.LogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.log.model.TransactionResult;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RedeemGetTakeRedeemService {

    private static Logger log = LoggerFactory.getLogger(RedeemGetTakeRedeemService.class);

    @Autowired
    ClearCacheTransaction clearCacheTransaction;

    @Autowired
    private RewardUtill rewardUtill;

    @Autowired
    private APIGWUtill apigwUtill;

    @Autowired
    private LogContextService logContextService;

    @Autowired
    RedeemDtacRedeemGameEndpoint redeemDtacRedeemGameEndpoint;

    @Autowired
    RedeemDtacRedeemLoyaltyEndpoint redeemDtacRedeemLoyaltyEndpoint;

    @Autowired
    RedeemTrueEligibleAndRedeemEndpoint redeemTrueEligibleAndRedeemEndpoint;

    @Value("${app.zone}")
    String zone;

    @Autowired
    private ErrorService errorService;

    public EndpointResultRWD getTakeRedeem(Map<String, Object> tv, String brand, String lang, RedeemDataReq redeemDataReq, HttpServletRequest httpRequest) throws Exception {
        final LogContext logContext = logContextService.getCurrentContext();

        log.info("Starting getTakeRedeem - brand: {}, lang: {}, campaignCode: {}, campaignId: {}, campaignType: {}, source: {}",
                brand, lang, redeemDataReq.getCampaignCode(), redeemDataReq.getCampaignId(), redeemDataReq.getCampaignType(), redeemDataReq.getSource());

        lang = rewardUtill.handleLanguage(lang);
        tv.put(ComnConst.KEY_LANGUAGE, lang);

        EndpointResultRWD endpointResultRwd = new EndpointResultRWD();
        RedeemTakeRedeemResponse response = new RedeemTakeRedeemResponse();
        try {

            String cmpgCode = redeemDataReq.getCampaignCode();
            String cmpgId = redeemDataReq.getCampaignId();
            String cmpgType = redeemDataReq.getCampaignType();
            String source = redeemDataReq.getSource();

            log.debug("Setting campaign details in transaction variables - cmpgCode: {}, cmpgId: {}, cmpgType: {}, source: {}", cmpgCode, cmpgId, cmpgType, source);

            tv.put("cmpgCode",cmpgCode);
            tv.put("cmpgId",cmpgId);
            tv.put("cmpgType",cmpgType);
            tv.put("source",source);

            if (apigwUtill.isDtac(brand)) {
                if(redeemDataReq.getCampaignType().equalsIgnoreCase(Constant.COIN)){
                    log.info("Burning Dtac coin.");
                    Map<String,Object> pathParam = mapRedeemGamePathParams(tv,cmpgCode);
                    Map<String,Object> queryParam = new HashMap<>();
                    RedeemGameDataApiReq req = mapRedeemGameBody(tv, cmpgCode);
                    endpointResultRwd = fetchRedeemGameDetail(tv, pathParam, queryParam, req);
                    if (Optional.ofNullable(endpointResultRwd)
                            .map(result -> result.getHttpStatus() == 200)
                            .orElse(false)){
                        log.info("burn Dtac coin success!");
                        RedeemGameResponse redeemGameResponse = (RedeemGameResponse) tv.get(Constant.ENDPOINT_SERVICE_GET_REDEEM_GAME);
                        log.info("unit = " + redeemGameResponse.getQuantity().getUnit() + ", balance = " + redeemGameResponse.getQuantity().getBalance() + ", CampaignSerial = "+redeemGameResponse.getCampaignSerial());
                        response = mapHistoryPointsRsp(tv,redeemGameResponse);                    }
                } else if(redeemDataReq.getCampaignType().equalsIgnoreCase(Constant.DTAC_REWARD)){
                    log.info("get Dtac coupon");
                    Map<String,Object> pathParamLoyalty = new HashMap<>();
                    Map<String,Object> queryParamLoyalty = new HashMap<>();
                    LoyaltyBurnReq reqLoyalty = mapRedeemLoyaltyBody(tv, cmpgId, brand, lang);
                    endpointResultRwd = fetchRedeemLoyaltyDetail(tv, pathParamLoyalty, queryParamLoyalty, reqLoyalty, 0);
                    if(Optional.ofNullable(endpointResultRwd)
                            .map(result -> result.getHttpStatus() == 200)
                            .orElse(false)) {
                        log.info("Successfully fetched Dtac coupon.");
                        response = mapLoyaltyBurnRsp(tv);
                    }
                } else {
                    log.info("Invalid or blank campaign type for Dtac.");
                    endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
                            tv.get("brand").toString().toUpperCase(),
                            Objects.requireNonNull(Constant.ERR_CODE_INVALID_INPUT),
                            tv.get(ComnConst.KEY_LANGUAGE).toString(),
                            "Input parameter is blank or invalid",
                            Constant.N_A,
                            Constant.MESSAGE);
                }
            } else {
                log.info("get TRUE coupon with campaignType: {}", cmpgType);
                Map<String,Object> pathParamEligibleAndRedeem = new HashMap<>();
                Map<String,Object> queryParamEligibleAndRedeem = new HashMap<>();
                EligibleAndRedeemReq eligibleAndRedeemReq = mapRedeemEligibleAndRedeemBody(tv, cmpgCode, brand, lang);
                if(null != eligibleAndRedeemReq.getId()){
                    endpointResultRwd = fetchEligibleAndRedeemDetail(tv, pathParamEligibleAndRedeem, queryParamEligibleAndRedeem, eligibleAndRedeemReq);
                    if(Optional.ofNullable(endpointResultRwd)
                            .map(result -> result.getHttpStatus() == 200)
                            .orElse(false)){
                        log.info("Successfully fetched True coupon.");
                        EligibleAndRedeemResponse contentResponse = (EligibleAndRedeemResponse) tv.get(Constant.ENDPOINT_SERVICE_GET_REDEEM_ELIGIBLE_AND_REDEEM);
                        response =  mapEligibleAndRedeemRsp(tv);
                    }
                }else{
                    endpointResultRwd = errorService.convertMapResult((EndpointResult) tv.get("endpointResult"));
                }
            }

            tv.put(Constant.TRANSACTION_RESPONSE_KEY,response);

        } catch (Exception e) {
            log.debug("Error occurred in getTakeRedeem: {}", e.getMessage());
            return errorService.convertMapResult(errorService.mapErrorException(e,tv));
        }
        log.info("Returning EndpointResultRWD with HTTP status: {}", endpointResultRwd.getHttpStatus());
        return endpointResultRwd;
    }

    private Map<String, Object> mapRedeemGamePathParams(Map<String, Object> tv, String cmpgCode) throws Exception {
        log.debug("map dtac mapRedeemGamePathParams");
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("accountId", tv.get("digitalId").toString());
        return pathParams;
    }

    private RedeemGameDataApiReq mapRedeemGameBody(Map<String, Object> tv, String cmpgCode) throws Exception {
        log.debug("map dtac mapRedeemGameBody");
        RedeemGameDataApiReq req = new RedeemGameDataApiReq();
        req.setRefId(apigwUtill.generateRewardBackendId());
        req.setCampaignId(cmpgCode);
        return req;
    }

    private EndpointResultRWD fetchRedeemGameDetail(Map<String, Object> tv, Map<String, Object> pathParams, Map<String, Object> queryParams, RedeemGameDataApiReq req) throws Exception {
        redeemDtacRedeemGameEndpoint.getRedeemGameEndpoint(tv,pathParams,queryParams,req);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

    private LoyaltyBurnReq mapRedeemLoyaltyBody(Map<String, Object> tv,String cmpgId, String brand, String lang) throws Exception {
        log.info("map dtac mapRedeemLoyaltyBody");
        LoyaltyBurnReq req = new LoyaltyBurnReq();
        req.setId(apigwUtill.generateRewardBackendId());
        req.setHref("-");
        LoyaltyBurnUser user = new LoyaltyBurnUser();
        user.setId(tv.get("digitalId").toString());
        user.setHref(tv.get("digitalId").toString());
        req.setUser(user);
        List<LoyaltyBurnPrivilege> privileges = new ArrayList<>();
        LoyaltyBurnPrivilege privilege = new LoyaltyBurnPrivilege();
        privilege.setFunction(Constant.ENDPOINT_SOURCE_SYSTEM_ID);
        privileges.add(privilege);
        req.setPrivilege(privileges);
        req.setRequestDateTime(apigwUtill.convertTodayFormatYYYYMMDD());
        req.setLang(lang.equalsIgnoreCase(Constant.TH) ? "THAI":"ENG");
        req.setSmsFlag("N"); //Y,N

        LoyaltyProgramProduct programProduct = new LoyaltyProgramProduct();
        programProduct.setId(cmpgId);
        req.setLoyaltyProgramProduct(programProduct);

        LoyaltyBurnRelatedParty relatedParty = new LoyaltyBurnRelatedParty();
        relatedParty.setId(Constant.TRUEAPP);
        relatedParty.setName(Constant.TRUEAPP);
        req.setRelatedParty(relatedParty);
        return req;
    }

    private EndpointResultRWD fetchRedeemLoyaltyDetail(Map<String, Object> tv, Map<String, Object> pathParams, Map<String, Object> queryParams, LoyaltyBurnReq req, int startRetry) throws Exception {
        redeemDtacRedeemLoyaltyEndpoint.getRedeemLoyaltyBurnEndpoint(tv,pathParams,queryParams,req,startRetry);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

    private RedeemTakeRedeemResponse mapLoyaltyBurnRsp(Map<String, Object> tv) throws Exception {
        log.info("mapLoyaltyBurnRsp");
        RedeemTakeRedeemResponse rsp = new RedeemTakeRedeemResponse();
        LoyaltyBurnResponse response = (LoyaltyBurnResponse) tv.get(Constant.ENDPOINT_SERVICE_GET_REDEEM_LOYALTY);
        if(response != null){
            rsp.setName(response.getName());
            rsp.setDescription(response.getDescription() != null ? response.getDescription():"");
            rsp.setExpireDate(LocalDateTime.now().atZone(ZoneOffset.ofHours(7)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")));
            rsp.setCouponCode(response.getProductNumber() != null ? response.getProductNumber():null);
            rsp.setRedeemDate(LocalDateTime.now().atZone(ZoneOffset.ofHours(7)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")));
            rsp.setClearCacheFlag(false);
            log.info("Coupon = " + response.getProductNumber());
        } else {
            rsp = null;
        }
        return rsp;
    }

    private EligibleAndRedeemReq mapRedeemEligibleAndRedeemBody(Map<String, Object> tv, String cmpgId, String brand, String lang) throws Exception {
        log.info("map true mapRedeemEligibleAndRedeemBody");
        EligibleAndRedeemReq req = new EligibleAndRedeemReq();

        IdAndType idAndType = rewardUtill.getThaiIdOrPhoneNoForTrue(tv);

        if(null != idAndType.getId()){
            req.setId(idAndType.getId());
            req.setTrnNo(apigwUtill.generateRewardRedeemBackendIdYYYYMMDD(Constant.TRUEAPP+"_REDEEMB2B_",14));
            req.setIdentifierType(idAndType.getIdnType());
            req.setChannel("true");
            List<EligibleAndRedeemReward> eligibleAndRedeemRewardList = new ArrayList<>();
            EligibleAndRedeemReward redeemReward = new EligibleAndRedeemReward();
            redeemReward.setPricePlanCode(cmpgId);
            redeemReward.setQuantity(1);
            eligibleAndRedeemRewardList.add(redeemReward);
            req.setRewards(eligibleAndRedeemRewardList);
            req.setComment("PREPROD".equalsIgnoreCase(zone)
                    || "PROD".equalsIgnoreCase(zone)
                    || "UAT".equalsIgnoreCase(zone)
                    ? Constant.TRUEAPP+" "+zone
                    : ("DEV".equalsIgnoreCase(zone)
                    || "LOCAL".equalsIgnoreCase(zone))
                    ?  Constant.TRUEAPP+" DEV"
                    : "OTHER CHANNEL");
        }
        return req;
    }

    private EndpointResultRWD fetchEligibleAndRedeemDetail(Map<String, Object> tv, Map<String, Object> pathParams, Map<String, Object> queryParams, EligibleAndRedeemReq req) throws Exception {
        redeemTrueEligibleAndRedeemEndpoint.getRedeemTrueEndpoint(tv,pathParams,queryParams,req);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

    private RedeemTakeRedeemResponse mapEligibleAndRedeemRsp(Map<String, Object> tv) throws Exception {
        log.info("true mapEligibleAndRedeemRsp");
        RedeemTakeRedeemResponse rsp = new RedeemTakeRedeemResponse();
        try {
            EligibleAndRedeemResponse contentResponse = (EligibleAndRedeemResponse) tv.get(Constant.ENDPOINT_SERVICE_GET_REDEEM_ELIGIBLE_AND_REDEEM);
//            System.out.println("contentResponse "+new Gson().toJson(contentResponse));
            Redemption response = contentResponse.getRedemption();
            if (response != null) {
                rsp.setName(null);
                rsp.setDescription(response.getMessage() != null ? response.getMessage():"");
                if (response.getCampaignExpireDate() != null &&
                        response.getIssuedCoupons() != null &&
                        !response.getIssuedCoupons().isEmpty()) {

                    rsp.setExpireDate(
                            apigwUtill.convertToBKKTimeReturnFormat(
                                    response.getIssuedCoupons().get(0).getExpireDate(),
                                    null,
                                    "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
                            )
                    );

                    rsp.setCouponCode(
                            response.getIssuedCoupons().get(0).getCouponNumber() != null
                                    ? response.getIssuedCoupons().get(0).getCouponNumber()
                                    : ""
                    );
                } else {
                    rsp.setExpireDate(null);
                    rsp.setCouponCode("");
                }
                rsp.setRedeemDate(LocalDateTime.now().atZone(ZoneOffset.ofHours(7)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")));
                rsp.setClearCacheFlag(response.getPoints() != 0);
                log.info("Coupon = " + response.getIssuedCoupons().get(0).getCouponNumber());
            } else {
                rsp = null;
            }
        } catch (Exception e){
            log.info("mapEligibleAndRedeemRsp no data");
        }
        return rsp;
    }

    private RedeemTakeRedeemResponse mapHistoryPointsRsp(Map<String, Object> tv, RedeemGameResponse redeemGameResponse) throws Exception {
        log.info("mapHistoryPointsRsp");
        RedeemTakeRedeemResponse rsp = new RedeemTakeRedeemResponse();
        if(redeemGameResponse.getCampaignSerial() != null){
            rsp.setName(null);
            rsp.setDescription("");
            rsp.setExpireDate(LocalDateTime.now().atZone(ZoneOffset.ofHours(7)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")));
            rsp.setCouponCode(redeemGameResponse.getCampaignSerial() != null ? redeemGameResponse.getCampaignSerial():null);
            rsp.setRedeemDate(LocalDateTime.now().atZone(ZoneOffset.ofHours(7)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")));
            rsp.setClearCacheFlag(Objects.equals(redeemGameResponse.getQuantity().getUnit(), "POINT"));
            log.info("Coupon = " + redeemGameResponse.getCampaignSerial());
        } else {
            rsp.setName("Package Redeem Success");
            rsp.setDescription("Package Redeem Success");
            rsp.setExpireDate(null);
            rsp.setCouponCode(null);
            rsp.setRedeemDate(null);
            rsp.setClearCacheFlag(Objects.equals(redeemGameResponse.getQuantity().getUnit(), "POINT"));
        }
        return rsp;
    }

}
