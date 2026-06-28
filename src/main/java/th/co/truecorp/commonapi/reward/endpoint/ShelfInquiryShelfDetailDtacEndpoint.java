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
import th.co.truecorp.commonapi.reward.model.ShelfDtacInquiryShelfDetailApiRsp;
import th.co.truecorp.commonapi.reward.model.endpoint.ApiGwInquiryShelfDetailRsp;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.service.APIGWService;

import java.util.*;

@Component
public class ShelfInquiryShelfDetailDtacEndpoint {

    private static final Logger log = LoggerFactory.getLogger(ShelfInquiryShelfDetailDtacEndpoint.class);

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

    @EndpointLog(name = "APIGW."+Constant.ENDPOINT_SERVICE_GET_INQUIRY_SHELF_DETAIL)
    public EndpointResult getShelfInquiryShelfDetailDtac(Map<String, Object> tv, Map<String, Object> queryParams) throws Exception {

        EndpointLogContext logContext = logContextService.getEndpointLoggingContext();

        try {
            ShelfDtacInquiryShelfDetailApiRsp shelfDtacInquiryShelfDetailApiRsp = new ShelfDtacInquiryShelfDetailApiRsp();
            EndpointResult endpointResult = null;
            EndpointResultRWD endpointResult2 = new EndpointResultRWD();

            log.info("Call GetShelfDtacInquiryShelfDetail");
            HttpHeaders headers = createHeaders(tv);
            Map<String, Object> pathParams = createPathParams(tv);

            log.info("Calling API GetShelfInquiryShelfDetail with endpoint");
            ResponseEntity<ApiGwInquiryShelfDetailRsp> gwResponse = apigwService.execute(
                    logContext,
                    HttpMethod.GET,
                    Constant.ENDPOINT_SOURCE_SYSTEM_ID,
                    Constant.ENDPOINT_SERVICE_GET_INQUIRY_SHELF_DETAIL,
                    ApiGwInquiryShelfDetailRsp.class,
                    tv,
                    headers,
                    pathParams,
                    queryParams
            );

            logContext.putA("srvTxId",queryParams.get("id"));

            if (gwResponse != null && !gwResponse.getStatusCode().is2xxSuccessful()) {
                log.info("api failed!");
                String errorCode = gwResponse.getBody().getCode();
                String messageApigw = gwResponse.getBody().getMessage();
                String descriptionApigw = gwResponse.getBody().getDescription();
                String businessErrorApigw = gwResponse.getBody().getBusinessError();
                String errorApigw = gwResponse.getBody().getError();
                String timestamp = gwResponse.getBody().getTimestamp() != null ? gwResponse.getBody().getTimestamp() : "";
                String errorMessage = rewardUtill.mapError(descriptionApigw,messageApigw,errorApigw,timestamp);

                endpointResult2 = errorService.mapErrorCode(Constant.QUERY_DATA,
                        tv.get("brand").toString().toUpperCase(),
                        errorCode,
                        tv.get(ComnConst.KEY_LANGUAGE).toString().toUpperCase(),
                        errorMessage,
                        null != businessErrorApigw && !businessErrorApigw.isEmpty() ? businessErrorApigw : Constant.N_A,
                        Constant.MESSAGE);
                shelfDtacInquiryShelfDetailApiRsp = null;
                tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
                endpointResult = resultService.mapEndpointResultAPIGW(tv,Constant.ENDPOINT_SOURCE_SYSTEM_ID,Constant.ENDPOINT_SERVICE_GET_SHELF_CONTENT,errorCode,gwResponse.getStatusCode().value(),
                        endpointResult2.getEndpointStatusType(),
                        endpointResult2.getHttpStatus(),
                        endpointResult2.getEndpointResponseCode(),
                        errorCode,
                        errorMessage);
            } else {
                log.info("api success!");
                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                endpointResult2 = errorService.convertMapResult(endpointResult);
                tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
                shelfDtacInquiryShelfDetailApiRsp = parseShelfResponse(gwResponse.getBody(), tv);
            }

            tv.put("GetShelfDtacInquiryShelfDetail", shelfDtacInquiryShelfDetailApiRsp);

            return endpointResult;
        } catch (Exception exception) {
            log.error("Error during execute apigw: {}", Constant.ENDPOINT_SERVICE_GET_INQUIRY_SHELF_DETAIL, exception);
            EndpointResult endpointResult = errorService.mapErrorException(exception,tv);
            tv.put(Constant.ENDPOINT_RESULT_RWD, errorService.convertMapResult(endpointResult));
            return endpointResult;

        }
    }

    private HttpHeaders createHeaders(Map<String, Object> tv) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tv.get(Constant.AUTHORIZATION).toString());
        return headers;
    }

    private Map<String, Object> createPathParams(Map<String, Object> tv) {
        Map<String, Object> pathParams = new HashMap<>();
        return pathParams;
    }

    private ShelfDtacInquiryShelfDetailApiRsp parseShelfResponse(ApiGwInquiryShelfDetailRsp rootNode, Map<String, Object> tv) throws Exception {
        ShelfDtacInquiryShelfDetailApiRsp shelfDtacInquiryShelfDetailApiRsp = new ShelfDtacInquiryShelfDetailApiRsp();

        log.debug("map response value from api...");
        shelfDtacInquiryShelfDetailApiRsp.setId(rootNode.getId());

        List<ShelfDtacInquiryShelfDetailApiRsp.Pattern> patternArrayList = new ArrayList<>();
        for (ApiGwInquiryShelfDetailRsp. Pattern result : rootNode.getPattern()) {
            ShelfDtacInquiryShelfDetailApiRsp.Pattern pattern = new ShelfDtacInquiryShelfDetailApiRsp.Pattern();
//            pattern.setType(result.path("id").asText());
            pattern.setType(result.getType());
            pattern.setPromotionPattern(result.getPromotionPattern() != null ? parsePromotionPattern(result.getPromotionPattern()) : null);
            patternArrayList.add(pattern);
        }
        shelfDtacInquiryShelfDetailApiRsp.setPattern(patternArrayList);

        log.debug("map response value from api success");
        return shelfDtacInquiryShelfDetailApiRsp;
    }

    private List<ShelfDtacInquiryShelfDetailApiRsp.Pattern.PromotionPattern> parsePromotionPattern(List<ApiGwInquiryShelfDetailRsp.PromotionPattern> pp) {
        List<ShelfDtacInquiryShelfDetailApiRsp.Pattern.PromotionPattern> promotionPatternList = new ArrayList<>();
        for (ApiGwInquiryShelfDetailRsp.PromotionPattern result : pp) {
            ShelfDtacInquiryShelfDetailApiRsp.Pattern.PromotionPattern pattern = new ShelfDtacInquiryShelfDetailApiRsp.Pattern.PromotionPattern();
            pattern.setId(result.getId() != null ? result.getId():"");
            pattern.setName(result.getName() != null ? result.getName():"");
            pattern.setType(result.getType() != null ? result.getType():"");
            pattern.setRelationTypeInGroup(result.getRelationTypeInGroup() != null ? result.getRelationTypeInGroup():"");
            pattern.setHref(result.getHref() != null ? result.getHref():"");
            pattern.setDescription(result.getDescription() != null ? result.getDescription():"");
            pattern.setShortDescription(result.getShortDescription() != null ? result.getShortDescription():"");
            pattern.setBanner(result.getBanner() != null ? parseBanner(result.getBanner()) : null);
            pattern.setValidFor(result.getValidFor() != null ? parseValidFor(result.getValidFor()) : null);
            pattern.setRelatedParty(result.getRelatedParty() != null ? parseRelatedParty(result.getRelatedParty()) : null);
            pattern.setPromotionCriteriaGroup(result.getPromotionCriteriaGroup() != null ? parsePromotionCriteriaGroup(result.getPromotionCriteriaGroup()) : null);
            promotionPatternList.add(pattern);
        }
        return promotionPatternList;
    }

    private ShelfDtacInquiryShelfDetailApiRsp.Pattern.Banner parseBanner(ApiGwInquiryShelfDetailRsp.Banner bn) {
            ShelfDtacInquiryShelfDetailApiRsp.Pattern.Banner banner = new ShelfDtacInquiryShelfDetailApiRsp.Pattern.Banner();
            banner.setId(bn.getId() != null ? bn.getId():"");
            banner.setType(bn.getType() != null ? bn.getType():"");
            banner.setHref(bn.getHref() != null ? bn.getHref():"");
            banner.setDescription(bn.getDescription() != null ? bn.getDescription():"");
            banner.setCategory(bn.getCategory() != null ? bn.getCategory():"");
            banner.setSubCategory(bn.getSubCategory() != null ? bn.getSubCategory():"");
            banner.setCampaigns(bn.getCampaigns() != null ? parseCampaign(bn.getCampaigns()) : null);
        return banner;
    }

    private List<ShelfDtacInquiryShelfDetailApiRsp.Pattern.Campaign> parseCampaign(List<ApiGwInquiryShelfDetailRsp.Campaign> campaigns) {
        List<ShelfDtacInquiryShelfDetailApiRsp.Pattern.Campaign> campaignList = new ArrayList<ShelfDtacInquiryShelfDetailApiRsp.Pattern.Campaign>();

        for(ApiGwInquiryShelfDetailRsp.Campaign cp : campaigns){
            ShelfDtacInquiryShelfDetailApiRsp.Pattern.Campaign campaign = new ShelfDtacInquiryShelfDetailApiRsp.Pattern.Campaign();
            campaign.setId(cp.getId());
            campaign.setName(cp.getName());
            campaign.setHref(cp.getHref());
            campaign.setType(cp.getType());
            campaign.setRelationTypeInGroup(cp.getRelationTypeInGroup());
            campaign.setRelatedParty(cp.getRelatedParty() != null ? parseRelatedParty(cp.getRelatedParty()) : null);
            campaign.setPromotionCriteriaGroup(cp.getPromotionCriteriaGroup() != null ? parsePromotionCriteriaGroup(cp.getPromotionCriteriaGroup()) : null);
            campaign.setValidFor(cp.getValidFor() != null ? parseValidFor(cp.getValidFor()) : null);
            campaign.setPromotionCriteria(cp.getPromotionCriteria() != null ? parsePromotionCriteria(cp.getPromotionCriteria()) : null);

            campaignList.add(campaign);
        }

        return campaignList;
    }

    private ShelfDtacInquiryShelfDetailApiRsp.Pattern.ValidFor parseValidFor(ApiGwInquiryShelfDetailRsp.ValidFor vf) {
        ShelfDtacInquiryShelfDetailApiRsp.Pattern.ValidFor validFor = new ShelfDtacInquiryShelfDetailApiRsp.Pattern.ValidFor();
        validFor.setStartDateTime(vf.getStartDateTime() != null ? vf.getStartDateTime():"");
        validFor.setEndDateTime(vf.getEndDateTime() != null ? vf.getEndDateTime():"");
        validFor.setRemainingDays(vf.getRemainingDays() != null ? vf.getRemainingDays():"");
        return validFor;
    }

    private ShelfDtacInquiryShelfDetailApiRsp.Pattern.RelatedParty parseRelatedParty(ApiGwInquiryShelfDetailRsp.RelatedParty rp) {
        ShelfDtacInquiryShelfDetailApiRsp.Pattern.RelatedParty relatedParty = new ShelfDtacInquiryShelfDetailApiRsp.Pattern.RelatedParty();
        relatedParty.setName(rp.getName() != null ? rp.getName():"");
        relatedParty.setType(rp.getType() != null ? rp.getType():"");
        relatedParty.setHref(rp.getHref() != null ? rp.getHref():"");
        relatedParty.setDescription(rp.getDescription() != null ? rp.getDescription():"");
        return relatedParty;
    }

    private ShelfDtacInquiryShelfDetailApiRsp.Pattern.PromotionCriteriaGroup parsePromotionCriteriaGroup(ApiGwInquiryShelfDetailRsp.PromotionCriteriaGroup pcg) {
        ShelfDtacInquiryShelfDetailApiRsp.Pattern.PromotionCriteriaGroup promotionCriteriaGroup = new ShelfDtacInquiryShelfDetailApiRsp.Pattern.PromotionCriteriaGroup();
        promotionCriteriaGroup.setId(pcg.getId() != null ? pcg.getId():"");
        promotionCriteriaGroup.setType(pcg.getType() != null ? pcg.getType():"");
        promotionCriteriaGroup.setDescription(pcg.getDescription() != null ? pcg.getDescription():"");
        promotionCriteriaGroup.setShortDescription(pcg.getShortDescription() != null ? pcg.getShortDescription():"");
        promotionCriteriaGroup.setPromotionCriteria(pcg.getPromotionCriteria() != null ? parsePromotionCriteria(pcg.getPromotionCriteria()) : null);

        return promotionCriteriaGroup;
    }

    private List<ShelfDtacInquiryShelfDetailApiRsp.Pattern.PromotionCriteria> parsePromotionCriteria(List<ApiGwInquiryShelfDetailRsp.PromotionCriteria> pc) {

        List<ShelfDtacInquiryShelfDetailApiRsp.Pattern.PromotionCriteria> promotionCriteriaList = new ArrayList<>();
        for(ApiGwInquiryShelfDetailRsp.PromotionCriteria p: pc){
            ShelfDtacInquiryShelfDetailApiRsp.Pattern.PromotionCriteria promotionCriteria = new ShelfDtacInquiryShelfDetailApiRsp.Pattern.PromotionCriteria();
            promotionCriteria.setCriteriaPara(p.getCriteriaPara() != null ? p.getCriteriaPara():"");
            promotionCriteria.setCriteriaValue(p.getCriteriaValue() != null ? p.getCriteriaValue():"");

            promotionCriteriaList.add(promotionCriteria);
        }

        return promotionCriteriaList;
    }

}
