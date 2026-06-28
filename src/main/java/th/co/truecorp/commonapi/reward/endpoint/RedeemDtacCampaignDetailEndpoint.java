package th.co.truecorp.commonapi.reward.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import th.co.truecorp.commonapi.reward.cms.jpa.service.ErrorService;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.model.EndpointResultRWD;
import th.co.truecorp.commonapi.reward.model.ShelfDtacCampaignDetailApiRsp;
import th.co.truecorp.commonapi.reward.model.endpoint.ApiGwCampaignDetailRsp;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.service.APIGWService;

import java.util.*;

@Component
public class RedeemDtacCampaignDetailEndpoint {

    private static final Logger log = LoggerFactory.getLogger(RedeemDtacCampaignDetailEndpoint.class);

    @Autowired
    private LogContextService logContextService;

    @Autowired
    private APIGWService apigwService;

    @Autowired
    private ResultService resultService;

    @Autowired
    private ErrorService errorService;

    @Autowired
    private RewardUtill rewardUtill;

    @EndpointLog(name = Constant.ENDPOINT_SOURCE_SYSTEM_ID_APIGW + ".getRedeemDtacCampaignDetail")
    public EndpointResult getRedeemDtacCampaignDetail(Map<String, Object> tv) throws Exception {

        EndpointLogContext logContext = logContextService.getEndpointLoggingContext();

        try {
            List<ShelfDtacCampaignDetailApiRsp> shelfDtacCampaignDetailApiRsp = new ArrayList<ShelfDtacCampaignDetailApiRsp>();
            EndpointResult endpointResult = null;
            EndpointResultRWD endpointResult2 = new EndpointResultRWD();

            log.info("Call getRedeemDtacCampaignDetail");
            HttpHeaders headers = createHeaders(tv);
            Map<String, Object> pathParams = createPathParams(tv);
            Map<String, Object> queryParams = createQueryParams(tv);
            log.info("Calling API getRedeemDtacCampaignDetail with endpoint");
            ResponseEntity<ApiGwCampaignDetailRsp> gwResponse = apigwService.execute(
                    logContext,
                    HttpMethod.GET,
                    Constant.ENDPOINT_SOURCE_SYSTEM_ID,
                    Constant.ENDPOINT_SERVICE_GET_CAMPAIGN_DETAIL,
                    ApiGwCampaignDetailRsp.class,
                    tv,
                    headers,
                    pathParams,
                    queryParams
            );

            if (gwResponse != null && !gwResponse.getStatusCode().is2xxSuccessful()) {
                log.error("api failed!");
                var responseBody = gwResponse.getBody();
                String errorCode = responseBody != null ? responseBody.getCode() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                String messageApigw = responseBody != null ? responseBody.getMessage() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                String descriptionApigw = responseBody != null ? responseBody.getDescription() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                String businessErrorApigw = responseBody != null ? responseBody.getBusinessError() : null;
                String errorApigw = responseBody != null ? responseBody.getError() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                String timestamp = (responseBody != null && responseBody.getTimestamp() != null) ? responseBody.getTimestamp() : "";
                String errorMessage = rewardUtill.mapError(descriptionApigw, messageApigw, errorApigw, timestamp);

                endpointResult2 = errorService.mapErrorCode(Constant.ELIGIBLE,
                        tv.get("brand").toString().toUpperCase(),
                        Objects.requireNonNull(errorCode),
                        tv.get(ComnConst.KEY_LANGUAGE).toString(),
                        errorMessage,
                        null != businessErrorApigw ? businessErrorApigw : Constant.N_A,
                        Constant.MESSAGE);
                shelfDtacCampaignDetailApiRsp = null;
                tv.put(Constant.ENDPOINT_RESULT_RWD, endpointResult2);
                endpointResult = resultService.mapEndpointResultAPIGW(tv, Constant.ENDPOINT_SOURCE_SYSTEM_ID, Constant.ENDPOINT_SERVICE_GET_SHELF_CONTENT, errorCode, gwResponse.getStatusCode().value(),
                        endpointResult2.getEndpointStatusType(),
                        endpointResult2.getHttpStatus(),
                        endpointResult2.getEndpointResponseCode(),
                        errorCode,
                        errorMessage);
            } else {
                log.info("api success!");
                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                endpointResult2 = errorService.convertMapResult(endpointResult);
                tv.put(Constant.ENDPOINT_RESULT_RWD, endpointResult2);
                shelfDtacCampaignDetailApiRsp = parseShelfCampaignDetail(gwResponse.getBody(), tv);
            }

            tv.put("GetShelfDtacCampaignDetail", shelfDtacCampaignDetailApiRsp);

            return endpointResult;
        } catch (Exception exception) {
            log.error("Error during execute apigw: {}", Constant.ENDPOINT_SERVICE_GET_CAMPAIGN_DETAIL, exception);
            EndpointResult endpointResult = errorService.mapErrorException(exception, tv);
            tv.put("endpointResult", endpointResult);
            tv.put(Constant.ENDPOINT_RESULT_RWD, errorService.convertMapResult(endpointResult));
            return endpointResult;
        }
    }

    private HttpHeaders createHeaders(Map<String, Object> tv) {
        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", tv.get(Constant.AUTHORIZATION).toString());
        return headers;
    }

    private Map<String, Object> createPathParams(Map<String, Object> tv) {
        Map<String, Object> pathParams = new HashMap<>();
        return pathParams;
    }

    private Map<String, Object> createQueryParams(Map<String, Object> tv) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("txid", tv.get("txid"));
        queryParams.put("id", tv.get("id"));
        return queryParams;
    }

    private List<ShelfDtacCampaignDetailApiRsp> parseShelfCampaignDetail(ApiGwCampaignDetailRsp jsonNode, Map<String, Object> tv) throws Exception {
        List<ShelfDtacCampaignDetailApiRsp> shelfDtacCampaignDetailApiRsps = new ArrayList<ShelfDtacCampaignDetailApiRsp>();

        log.debug("map response value from api...");

        for (ApiGwCampaignDetailRsp.CampaignInfo result : Optional.ofNullable(jsonNode.getCampaignInfo()).orElse(Collections.emptyList())) {
            if (result == null) {
                continue;
            }
            ShelfDtacCampaignDetailApiRsp pattern = new ShelfDtacCampaignDetailApiRsp();
            pattern.setTxid(result.getTxid() != null ? result.getTxid() : "");
            pattern.setBzbId(result.getId() != null ? result.getId() : "");
            pattern.setName(result.getName() != null ? mapName(result.getName()) : null);
            pattern.setRelatedParty(result.getRelatedParty() != null ? mapeRelatedParty(result.getRelatedParty()) : null);
            pattern.setDescription(result.getDescription() != null ? mapDescription(result.getDescription()) : null);
            pattern.setCriteria(result.getCriteria() != null ? mapCriteria(result.getCriteria()) : null);
            pattern.setValidFor(result.getValidFor() != null ? mapValidFor(result.getValidFor()) : null);
            pattern.setType(result.getType() != null ? result.getType() : "");
            pattern.setSubType(result.getSubType() != null ? result.getSubType() : "");
            pattern.setOnlyDisplay(result.getOnlyDisplay() != null ? result.getOnlyDisplay() : "");
            pattern.setOriginalPoint(result.getOriginalPoint() != null ? Integer.parseInt(result.getOriginalPoint()) : 0);
            pattern.setPointPerUnit(result.getPointPerUnit() != null ? Integer.parseInt(result.getPointPerUnit()) : 0);
            pattern.setCouponDetail(result.getCouponDetail() != null ? mapCouponDetail(result.getCouponDetail()) : null);
            pattern.setHref(result.getHref() != null ? result.getHref() : "");
            pattern.setCharacteristic(result.getCharacteristic() != null ? mapCharacteristic(result.getCharacteristic()) : null);
            shelfDtacCampaignDetailApiRsps.add(pattern);
        }

        log.debug("map response value from api success");
        return shelfDtacCampaignDetailApiRsps;
    }

    private ShelfDtacCampaignDetailApiRsp.Name mapName(ApiGwCampaignDetailRsp.LocalizedText jsonNode) throws Exception {
        ShelfDtacCampaignDetailApiRsp.Name name = new ShelfDtacCampaignDetailApiRsp.Name();

        log.debug("map response value from api...");
        name.setTh(jsonNode.getTh() != null ? jsonNode.getTh() : "");
        name.setEn(jsonNode.getEn() != null ? jsonNode.getEn() : "");

        log.debug("map response value from api success");
        return name;
    }

    private List<ShelfDtacCampaignDetailApiRsp.RelatedParty> mapeRelatedParty(List<ApiGwCampaignDetailRsp.RelatedParty> jsonNode) throws Exception {
        List<ShelfDtacCampaignDetailApiRsp.RelatedParty> relatedPartys = new ArrayList<ShelfDtacCampaignDetailApiRsp.RelatedParty>();

        log.debug("map response value from api...");
        for (ApiGwCampaignDetailRsp.RelatedParty result : jsonNode) {
            if (result == null) {
                continue;
            }
            ShelfDtacCampaignDetailApiRsp.RelatedParty relatedParty = new ShelfDtacCampaignDetailApiRsp.RelatedParty();
            relatedParty.setName(result.getName() != null ? result.getName() : "");
            relatedParty.setNameEn(result.getNameEn() != null ? result.getNameEn() : "");
            relatedParty.setBranch(result.getBranch() != null ? mapListString(result.getBranch()) : null);
            relatedParty.setBranchEn(result.getBranchEn() != null ? mapListString(result.getBranchEn()) : null);
            relatedPartys.add(relatedParty);
        }

        log.debug("map response value from api success");
        return relatedPartys;
    }

    private List<String> mapListString(List<String> jn) {
        List<String> listVal = new ArrayList<>();
        for (String j : jn) {
            listVal.add(j.replace("\"", ""));
        }
        return listVal;
    }

    private ShelfDtacCampaignDetailApiRsp.Description mapDescription(ApiGwCampaignDetailRsp.LocalizedText jsonNode) throws Exception {
        ShelfDtacCampaignDetailApiRsp.Description description = new ShelfDtacCampaignDetailApiRsp.Description();

        description.setTh(jsonNode != null && jsonNode.getTh() != null ? jsonNode.getTh() : "");
        description.setEn(jsonNode != null && jsonNode.getEn() != null ? jsonNode.getEn() : "");

        return description;
    }

    private ShelfDtacCampaignDetailApiRsp.Criteria mapCriteria(ApiGwCampaignDetailRsp.LocalizedText jsonNode) throws Exception {
        ShelfDtacCampaignDetailApiRsp.Criteria criteria = new ShelfDtacCampaignDetailApiRsp.Criteria();

        criteria.setTh(jsonNode != null && jsonNode.getTh() != null ? jsonNode.getTh() : "");
        criteria.setEn(jsonNode != null && jsonNode.getEn() != null ? jsonNode.getEn() : "");

        return criteria;
    }

    private ShelfDtacCampaignDetailApiRsp.ValidFor mapValidFor(ApiGwCampaignDetailRsp.ValidFor jsonNode) throws Exception {
        ShelfDtacCampaignDetailApiRsp.ValidFor validFor = new ShelfDtacCampaignDetailApiRsp.ValidFor();

        validFor.setStartDateTime(jsonNode != null && jsonNode.getStartDateTime() != null ? jsonNode.getStartDateTime() : "");
        validFor.setEndDateTime(jsonNode != null && jsonNode.getEndDateTime() != null ? jsonNode.getEndDateTime() : "");
        validFor.setRemainingDays(Optional.ofNullable(jsonNode)
                        .map(ApiGwCampaignDetailRsp.ValidFor::getRemainingDays)
                        .filter(remainingDays -> remainingDays.matches("\\d+")) // ตรวจสอบว่าเป็นตัวเลข
                        .map(Integer::parseInt)
                        .orElse(0)
        );

        return validFor;
    }

    private ShelfDtacCampaignDetailApiRsp.CouponDetail mapCouponDetail(ApiGwCampaignDetailRsp.CouponDetail jsonNode) throws Exception {
        ShelfDtacCampaignDetailApiRsp.CouponDetail couponDetail = new ShelfDtacCampaignDetailApiRsp.CouponDetail();

        couponDetail.setCouponApp_th(jsonNode != null && jsonNode.getCouponApp_th() != null ? jsonNode.getCouponApp_th() : "");
        couponDetail.setCouponApp_en(jsonNode != null && jsonNode.getCouponApp_en() != null ? jsonNode.getCouponApp_en() : "");
        couponDetail.setCouponApp_my(jsonNode != null && jsonNode.getCouponApp_my() != null ? jsonNode.getCouponApp_my() : "");
        couponDetail.setCouponApp_km(jsonNode != null && jsonNode.getCouponApp_km() != null ? jsonNode.getCouponApp_km() : "");
        couponDetail.setCouponApp_url(jsonNode != null && jsonNode.getCouponApp_url() != null ? jsonNode.getCouponApp_url() : "");
        couponDetail.setCouponWeb_th(jsonNode != null && jsonNode.getCouponWeb_th() != null ? jsonNode.getCouponWeb_th() : "");
        couponDetail.setCouponWeb_en(jsonNode != null && jsonNode.getCouponWeb_en() != null ? jsonNode.getCouponWeb_en() : "");
        couponDetail.setCouponWeb_my(jsonNode != null && jsonNode.getCouponWeb_my() != null ? jsonNode.getCouponWeb_my() : "");
        couponDetail.setCouponWeb_km(jsonNode != null && jsonNode.getCouponWeb_km() != null ? jsonNode.getCouponWeb_km() : "");
        couponDetail.setCouponWeb_url(jsonNode != null && jsonNode.getCouponWeb_url() != null ? jsonNode.getCouponWeb_url() : "");

        return couponDetail;
    }

    private List<ShelfDtacCampaignDetailApiRsp.characteristic> mapCharacteristic(List<ApiGwCampaignDetailRsp.characteristic> jn) throws Exception {

        List<ShelfDtacCampaignDetailApiRsp.characteristic> characteristicList = new ArrayList<ShelfDtacCampaignDetailApiRsp.characteristic>();

        for (ApiGwCampaignDetailRsp.characteristic ca : jn) {
            ShelfDtacCampaignDetailApiRsp.characteristic characteristic = new ShelfDtacCampaignDetailApiRsp.characteristic();

            characteristic.setName(ca != null && ca.getName() != null ? ca.getName() : "");
            characteristic.setValue(ca != null && ca.getValue() != null ? ca.getValue() : "");

            characteristicList.add(characteristic);
        }

        return characteristicList;
    }

}
