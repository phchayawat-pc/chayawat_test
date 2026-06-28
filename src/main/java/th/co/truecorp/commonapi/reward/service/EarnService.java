package th.co.truecorp.commonapi.reward.service;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.service.ErrorService;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.endpoint.RedeemTrueEligibleAndRedeemEndpoint;
import th.co.truecorp.commonapi.reward.model.EndpointResultRWD;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.earn.dtacenv.DtacPointAndCoinReq;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.earn.dtacenv.DtacPointAndCoinRes;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.earn.trueenv.req.EligibleAndRedeemReq;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.earn.trueenv.req.EligibleAndRedeemReward;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemEligibleAndRedeem.res.EligibleAndRedeemResponse;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemEligibleAndRedeem.res.Redemption;
import th.co.truecorp.commonapi.reward.model.redeem.EarnRedeemtionGiftResponse;
import th.co.truecorp.commonlib.constant.ComnConst;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class EarnService {

    private static Logger log = LoggerFactory.getLogger(EarnService.class);

    @Autowired
    private RewardUtill rewardUtill;

    @Autowired
    private APIGWUtill apigwUtill;

    @Autowired
    RedeemTrueEligibleAndRedeemEndpoint redeemTrueEligibleAndRedeemEndpoint;

    @Value("${app.zone}")
    String zone;

    @Autowired
    private ErrorService errorService;

    private EndpointResultRWD fetchEligibleAndRedeemDetail(Map<String, Object> tv, Map<String, Object> pathParams, Map<String, Object> queryParams, EligibleAndRedeemReq req) throws Exception {
        redeemTrueEligibleAndRedeemEndpoint.getEarnRedeemTrueEndpoint(tv, pathParams, queryParams, req);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

    public EndpointResultRWD getTrueCoupon(Map<String, Object> tv, String cmpgCode, String productIdDec) throws Exception {
        log.info("get TRUE coupon");
        EndpointResultRWD endpointResultRwd = new EndpointResultRWD();
        EarnRedeemtionGiftResponse response = new EarnRedeemtionGiftResponse();
        try {
            Map<String, Object> pathParamEligibleAndRedeem = new HashMap<>();
            Map<String, Object> queryParamEligibleAndRedeem = new HashMap<>();
            EligibleAndRedeemReq eligibleAndRedeemReq = mapRedeemEligibleAndRedeemBodyForEarn(tv, cmpgCode, productIdDec);
            endpointResultRwd = fetchEligibleAndRedeemDetail(tv, pathParamEligibleAndRedeem, queryParamEligibleAndRedeem, eligibleAndRedeemReq);
            if (Optional.ofNullable(endpointResultRwd)
                    .map(result -> result.getHttpStatus() == 200)
                    .orElse(false)) {
                response = mapEligibleAndRedeemRsp(tv);
            }
            tv.put(Constant.TRANSACTION_RESPONSE_KEY, response);
        } catch (Exception e) {
            log.debug("Error occurred in getTakeRedeem: {}", e.getMessage());
            return errorService.convertMapResult(errorService.mapErrorException(e, tv));
        }
        log.info("Returning EndpointResultRWD with HTTP status: {}", endpointResultRwd.getHttpStatus());
        return endpointResultRwd;
    }

    private EligibleAndRedeemReq mapRedeemEligibleAndRedeemBodyForEarn(Map<String, Object> tv, String cmpgId, String productIdDec) throws Exception {
        log.info("map true mapRedeemEligibleAndRedeemBodyForEarn");
        EligibleAndRedeemReq req = new EligibleAndRedeemReq();

        req.setId(this.rewardUtill.encryptApiGw(productIdDec));
        req.setTrnNo(apigwUtill.generateRewardRedeemBackendIdyyyyMMddHHmmssSSS(Constant.TRUEAPP + "_EARNPOINT_"));
        req.setIdentifierType("TMH");
        req.setChannel("true");
//        req.setSimulate("true");

        List<EligibleAndRedeemReward> eligibleAndRedeemRewardList = new ArrayList<>();
        EligibleAndRedeemReward redeemReward = new EligibleAndRedeemReward();
        redeemReward.setPricePlanCode(cmpgId);
        redeemReward.setQuantity(1);
//        redeemReward.setPoints(1);
        eligibleAndRedeemRewardList.add(redeemReward);

        req.setRewards(eligibleAndRedeemRewardList);
        req.setComment("PREPROD".equalsIgnoreCase(zone)
                || "PROD".equalsIgnoreCase(zone)
                || "UAT".equalsIgnoreCase(zone)
                ? Constant.TRUEAPP + " " + zone
                : ("DEV".equalsIgnoreCase(zone)
                || "LOCAL".equalsIgnoreCase(zone))
                ? Constant.TRUEAPP + " DEV"
                : "OTHER CHANNEL");
        return req;
    }

    private EarnRedeemtionGiftResponse mapEligibleAndRedeemRsp(Map<String, Object> tv) throws Exception {
        log.info("true mapEligibleAndRedeemRsp");
        EarnRedeemtionGiftResponse rsp = new EarnRedeemtionGiftResponse();
        try {
            EligibleAndRedeemResponse contentResponse = (EligibleAndRedeemResponse) tv.get(Constant.ENDPOINT_SERVICE_GET_REDEEM_ELIGIBLE_AND_REDEEM);
            System.out.println("contentResponse " + new Gson().toJson(contentResponse));
            Redemption response = contentResponse.getRedemption();
            if (response != null) {
                rsp.setEarnNumber(response.getPoints());
                rsp.setTrxId(Long.toString(response.getTransactionId()));
                if (response.getIssuedCoupons() != null && !response.getIssuedCoupons().isEmpty()) {
                    String couponNumber = response.getIssuedCoupons().get(0).getCouponNumber();
                    if (couponNumber != null) {
                        log.info("Coupon = " + couponNumber);
                    } else {
                        log.info("Coupon number is null");
                    }
                } else {
                    log.info("Issued coupons list is empty or null");
                }

            } else {
                rsp = null;
            }
        } catch (Exception e) {
            log.info("mapEligibleAndRedeemRsp no data");
        }
        return rsp;
    }

    public EndpointResultRWD getDtacEarnPoint(Map<String, Object> tv, String digitalId, String bzbProductId, String bzbAmount) throws Exception {
        log.info("get getDtacPointAndCoin");
        EndpointResultRWD endpointResultRwd = null;
        EarnRedeemtionGiftResponse response = new EarnRedeemtionGiftResponse();
        try {
            Map<String, Object> pathParamGetPointAndCoin = new HashMap<>();
            pathParamGetPointAndCoin.put("accountId", digitalId);
            String transactionId = apigwUtill.generateRewardRedeemBackendIdyyyyMMddHHmmssSSS("");
            DtacPointAndCoinReq dtacPointAndCoinReq = mapGetDtacPointAndCoinReq(tv, bzbProductId, bzbAmount, transactionId);
            endpointResultRwd = fetchDtacPointAndCoin(tv, pathParamGetPointAndCoin, dtacPointAndCoinReq, transactionId);
            if (Optional.ofNullable(endpointResultRwd)
                    .map(result -> result.getHttpStatus() == 200)
                    .orElse(false)) {
                response = mapGetDtacPointAndCoinRes(tv);
            }
            tv.put(Constant.TRANSACTION_RESPONSE_KEY, response);
        } catch (Exception e) {
            log.debug("Error occurred in getTakeRedeem: {}", e.getMessage());
            return errorService.convertMapResult(errorService.mapErrorException(e, tv));
        }
        log.info("Returning EndpointResultRWD with HTTP status: {}", endpointResultRwd.getHttpStatus());
        return endpointResultRwd;
    }

    private EndpointResultRWD fetchDtacPointAndCoin(Map<String, Object> tv, Map<String, Object> pathParams, DtacPointAndCoinReq req, String transactionId) throws Exception {
        redeemTrueEligibleAndRedeemEndpoint.getEarnDtacPoint(tv, pathParams, req, transactionId);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

    private DtacPointAndCoinReq mapGetDtacPointAndCoinReq(Map<String, Object> tv, String bzbProductId, String bzbAmount, String transactionId) throws Exception {
        log.info("mapGetDtacPointAndCoinReq");
        DtacPointAndCoinReq req = new DtacPointAndCoinReq();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        req.setCharacteristic(new ArrayList<>());

        req.getCharacteristic().add(new DtacPointAndCoinReq.Characteristic("ID", transactionId));
        req.getCharacteristic().add(new DtacPointAndCoinReq.Characteristic("LANG", tv.get(ComnConst.HEADER_LANGUAGE).toString()));
        req.getCharacteristic().add(new DtacPointAndCoinReq.Characteristic("PRODUCT_ID", bzbProductId));
        req.getCharacteristic().add(new DtacPointAndCoinReq.Characteristic("PERIOD", formattedDateTime));
        if (null != bzbAmount) {
            req.getCharacteristic().add(new DtacPointAndCoinReq.Characteristic("AMOUNT", bzbAmount));
        }

        return req;
    }

    private EarnRedeemtionGiftResponse mapGetDtacPointAndCoinRes(Map<String, Object> tv) throws Exception {
        log.info("mapGetDtacPointAndCoinRes");
        EarnRedeemtionGiftResponse rsp = new EarnRedeemtionGiftResponse();
        try {
            DtacPointAndCoinRes contentResponse = (DtacPointAndCoinRes) tv.get("GetPointService");
            if (contentResponse != null) {
                rsp.setEarnNumber(contentResponse.getQuantity() != null && contentResponse.getQuantity().getBalance() != null ? Integer.parseInt(contentResponse.getQuantity().getBalance()) : null);
                rsp.setTrxId(contentResponse.getId());
            } else {
                rsp = null;
            }
        } catch (Exception e) {
            log.info("mapGetDtacPointAndCoinRes Exception");
        }
        return rsp;
    }

}
