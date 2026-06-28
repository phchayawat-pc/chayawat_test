package th.co.truecorp.commonapi.reward.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import th.co.truecorp.commonapi.reward.model.ShelfSectionDetailDtacInquiryShelfDetailRsp;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.service.APIGWService;

import java.util.*;

@Component
public class ShelfSectionDetailDtacInquiryShelfDetailEndpoint {

    private static final Logger log = LoggerFactory.getLogger(ShelfSectionDetailDtacInquiryShelfDetailEndpoint.class);

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
    public EndpointResult getShelfDtacInquiryShelfDetail(Map<String, Object> tv, Map<String, Object> queryParams) throws Exception {

        EndpointLogContext logContext = logContextService.getEndpointLoggingContext();

        try {
            ShelfSectionDetailDtacInquiryShelfDetailRsp shelfDtacInquiryShelfDetailApiRsp = new ShelfSectionDetailDtacInquiryShelfDetailRsp();
            EndpointResult endpointResult = null;
            EndpointResultRWD endpointResult2 = new EndpointResultRWD();

            log.info("Call GetShelfDtacInquiryShelfDetail");
            HttpHeaders headers = createHeaders(tv);
            Map<String, Object> pathParams = createPathParams(tv);
            log.info("Calling API GetShelfInquiryShelfDetail with endpoint");
            ResponseEntity<ShelfSectionDetailDtacInquiryShelfDetailRsp> gwResponse = apigwService.execute(
                    logContext,
                    HttpMethod.GET,
                    Constant.ENDPOINT_SOURCE_SYSTEM_ID,
                    Constant.ENDPOINT_SERVICE_GET_INQUIRY_SHELF_DETAIL,
                    ShelfSectionDetailDtacInquiryShelfDetailRsp.class,
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
                shelfDtacInquiryShelfDetailApiRsp = parseShelfResponse(gwResponse.getBody(),tv);
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

    private ShelfSectionDetailDtacInquiryShelfDetailRsp parseShelfResponse(ShelfSectionDetailDtacInquiryShelfDetailRsp rootNode, Map<String, Object> tv) throws Exception {
        ShelfSectionDetailDtacInquiryShelfDetailRsp shelfDtacInquiryShelfDetailApiRsp = new ShelfSectionDetailDtacInquiryShelfDetailRsp();

        log.debug("map response value from api...");
        shelfDtacInquiryShelfDetailApiRsp.setId(rootNode.getId());
        shelfDtacInquiryShelfDetailApiRsp.setDescription(rootNode.getDescription());
        shelfDtacInquiryShelfDetailApiRsp.setLifecycleStatus(rootNode.getLifecycleStatus());
        shelfDtacInquiryShelfDetailApiRsp.setName(rootNode.getName());
        shelfDtacInquiryShelfDetailApiRsp.setListMode(rootNode.getListMode());
        ShelfSectionDetailDtacInquiryShelfDetailRsp.ValidFor validFor = new ShelfSectionDetailDtacInquiryShelfDetailRsp.ValidFor();
        validFor.setStartDateTime(rootNode.getValidFor().getStartDateTime() != null ? rootNode.getValidFor().getStartDateTime():null);
        validFor.setEndDateTime(rootNode.getValidFor().getEndDateTime() != null ? rootNode.getValidFor().getEndDateTime():null);
        shelfDtacInquiryShelfDetailApiRsp.setValidFor(validFor);
        shelfDtacInquiryShelfDetailApiRsp.setPromotionPattern(rootNode.getPromotionPattern() != null ? parsePromotionPattern(rootNode.getPromotionPattern()) : null);

        log.debug("map response value from api success");
        return shelfDtacInquiryShelfDetailApiRsp;
    }

    private List<ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionPattern> parsePromotionPattern(List<ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionPattern> pp) {
        List<ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionPattern> promotionPatternList = new ArrayList<>();
        for (ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionPattern result : pp) {
            ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionPattern pattern = new ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionPattern();
            pattern.setId(result.getId() != null ? result.getId():"");
            pattern.setHref(result.getHref() != null ? result.getHref():"");
            pattern.setName(result.getName() != null ? result.getName():"");
            pattern.setPriority(result.getPriority() != null ? result.getPriority():"");
            pattern.setType(result.getType() != null ? result.getType():"");
            pattern.setDescription(result.getDescription() != null ? result.getDescription():"");
            pattern.setShortDescription(result.getShortDescription() != null ? result.getShortDescription():"");
            pattern.setCategory(result.getCategory() != null ? result.getCategory():"");
            pattern.setSubCategory(result.getSubCategory() != null ? result.getSubCategory():"");
            pattern.setValidFor(result.getValidFor() != null ? parseValidFor(result.getValidFor()) : null);
            pattern.setRelatedParty(result.getRelatedParty() != null ? parseRelatedParty(result.getRelatedParty()) : null);
            pattern.setPromotionCriteriaGroup(result.getPromotionCriteriaGroup() != null ? parsePromotionCriteriaGroup(result.getPromotionCriteriaGroup()) : null);
            pattern.setPromotionAction(result.getPromotionAction() != null ? parsePromotionAction(result.getPromotionAction()) : null);
            pattern.setBanner(parseBanner(result.getBanner()));


            promotionPatternList.add(pattern);
        }
        return promotionPatternList;
    }

    private ShelfSectionDetailDtacInquiryShelfDetailRsp.ValidFor parseValidFor(ShelfSectionDetailDtacInquiryShelfDetailRsp.ValidFor vf) {
        ShelfSectionDetailDtacInquiryShelfDetailRsp.ValidFor validFor = new ShelfSectionDetailDtacInquiryShelfDetailRsp.ValidFor();
        validFor.setStartDateTime(vf.getStartDateTime() != null ? vf.getStartDateTime():"");
        validFor.setEndDateTime(vf.getEndDateTime() != null ? vf.getEndDateTime():"");
        validFor.setRemainingDays(vf.getRemainingDays() != null ? vf.getRemainingDays():"");
        return validFor;
    }

    private List<ShelfSectionDetailDtacInquiryShelfDetailRsp.RelatedParty> parseRelatedParty(List<ShelfSectionDetailDtacInquiryShelfDetailRsp.RelatedParty> rp) {
        List<ShelfSectionDetailDtacInquiryShelfDetailRsp.RelatedParty> relatedPartyList = new ArrayList<ShelfSectionDetailDtacInquiryShelfDetailRsp.RelatedParty>();

        for(ShelfSectionDetailDtacInquiryShelfDetailRsp.RelatedParty result : rp){
            ShelfSectionDetailDtacInquiryShelfDetailRsp.RelatedParty relatedParty = new ShelfSectionDetailDtacInquiryShelfDetailRsp.RelatedParty();
            relatedParty.setId(result.getId() != null ? result.getId():"");
            relatedParty.setName(result.getName() != null ? result.getName():"");
            relatedParty.setHref(result.getHref() != null ? result.getHref():"");
            relatedParty.setDescription(result.getDescription() != null ? result.getDescription():"");
            relatedPartyList.add(relatedParty);
        }

        return relatedPartyList;
    }

    private List<ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteriaGroup> parsePromotionCriteriaGroup(List<ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteriaGroup> pcg) {
        List<ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteriaGroup> promotionCriteriaGroupList = new ArrayList<ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteriaGroup>();

        for(ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteriaGroup result : pcg) {
            ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteriaGroup promotionCriteriaGroup = new ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteriaGroup();
            promotionCriteriaGroup.setId(result.getId() != null ? result.getId() : "");
            promotionCriteriaGroup.setGroupName(result.getGroupName() != null ? result.getGroupName() : "");
            promotionCriteriaGroup.setHref(result.getHref() != null ? result.getHref() : "");
            promotionCriteriaGroup.setType(result.getType() != null ? result.getType() : "");
            promotionCriteriaGroup.setDescription(result.getDescription() != null ? result.getDescription() : "");
            promotionCriteriaGroup.setShortDescription(result.getShortDescription() != null ? result.getShortDescription() : "");
            promotionCriteriaGroup.setRelationTypeInGroup(result.getRelationTypeInGroup() != null ? result.getRelationTypeInGroup() : "");
            promotionCriteriaGroup.setValidFor(result.getValidFor() != null ? parseValidFor(result.getValidFor()) : null);
            promotionCriteriaGroup.setPromotionCriteria(result.getPromotionCriteria() != null ? parsePromotionCriteria(result.getPromotionCriteria()) : null);
            promotionCriteriaGroup.setPromotionAction(result.getPromotionAction() != null ? parsePromotionAction(result.getPromotionAction()) : null);
            promotionCriteriaGroup.setRelatedParty(result.getRelatedParty() != null ? parseRelatedParty(result.getRelatedParty()) : null);
            promotionCriteriaGroupList.add(promotionCriteriaGroup);
        }

        return promotionCriteriaGroupList;
    }

    private List<ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteria> parsePromotionCriteria(List<ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteria> pc) {

        List<ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteria> promotionCriteriaList = new ArrayList<>();
        for(ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteria result: pc){
            ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteria promotionCriteria = new ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteria();
            promotionCriteria.setId(result.getId() != null ? result.getId():"");
            promotionCriteria.setCriteriaPara(result.getCriteriaPara() != null ? result.getCriteriaPara():"");
            promotionCriteria.setCriteriaValue(result.getCriteriaValue() != null ? result.getCriteriaValue():"");

            promotionCriteriaList.add(promotionCriteria);
        }

        return promotionCriteriaList;
    }

    private List<ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionAction> parsePromotionAction(List<ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionAction> pc) {

        List<ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionAction> promotionActionList = new ArrayList<>();
        for(ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionAction result: pc){
            ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionAction promotionAction = new ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionAction();
            promotionAction.setRecommended(result.getRecommended() != null ? result.getRecommended():"");
            promotionAction.setActionType(result.getActionType() != null ? result.getActionType():"");
            promotionAction.setActionValue(result.getActionValue() != null ? result.getActionValue():"");
            promotionAction.setHref(result.getHref() != null ? result.getHref():"");

            promotionActionList.add(promotionAction);
        }

        return promotionActionList;
    }

    private ShelfSectionDetailDtacInquiryShelfDetailRsp.Banner parseBanner(ShelfSectionDetailDtacInquiryShelfDetailRsp.Banner bn) {
        ShelfSectionDetailDtacInquiryShelfDetailRsp.Banner banner = new ShelfSectionDetailDtacInquiryShelfDetailRsp.Banner();
        banner.setId(bn.getId() != null ? bn.getId():"");
        banner.setName(bn.getName() != null ? bn.getName():"");
        banner.setType(bn.getType() != null ? bn.getType():"");
        banner.setHref(bn.getHref() != null ? bn.getHref():"");
        banner.setDescription(bn.getDescription() != null ? bn.getDescription():"");
        banner.setCampaigns(parseCampaign(bn.getCampaigns()));

        return banner;
    }

    private List<ShelfSectionDetailDtacInquiryShelfDetailRsp.Campaign> parseCampaign(List<ShelfSectionDetailDtacInquiryShelfDetailRsp.Campaign> campaigns) {
        List<ShelfSectionDetailDtacInquiryShelfDetailRsp.Campaign> campaignList = new ArrayList<ShelfSectionDetailDtacInquiryShelfDetailRsp.Campaign>();

        if(campaigns != null && !campaigns.isEmpty()){
            for(ShelfSectionDetailDtacInquiryShelfDetailRsp.Campaign cp : campaigns){
                ShelfSectionDetailDtacInquiryShelfDetailRsp.Campaign campaign = new ShelfSectionDetailDtacInquiryShelfDetailRsp.Campaign();
                campaign.setId(cp.getId() != null ? cp.getId():"");
                campaign.setName(cp.getName() != null ? cp.getName():"");
                campaign.setHref(cp.getHref() != null ? cp.getHref():"");
                campaign.setType(cp.getType() != null ? cp.getType():"");
                campaign.setRelationTypeInGroup(cp.getRelationTypeInGroup() != null ? cp.getRelationTypeInGroup():"");
                campaign.setPromotionType(cp.getPromotionType() != null ? cp.getPromotionType():"");
                campaign.setValidFor(cp.getValidFor() != null ? parseValidFor(cp.getValidFor()) : null);
                campaign.setRelatedParty(cp.getRelatedParty() != null ? parseRelatedPartyObj(cp.getRelatedParty()) : null);
                campaign.setBanner(cp.getBanner() != null ? parseBanner(cp.getBanner()) : null);
                campaign.setPromotionCriteriaGroup(cp.getPromotionCriteriaGroup() != null ? parsePromotionCriteriaGroupObj(cp.getPromotionCriteriaGroup()) : null);
                campaign.setPromotionCriteria(cp.getPromotionCriteria() != null ? parsePromotionCriteria(cp.getPromotionCriteria()) : null);
                campaign.setListMode(cp.getListMode() != null ? cp.getListMode():"");

                campaignList.add(campaign);
            }
        }else{
            campaignList = null;
        }

        return campaignList;
    }

    private ShelfSectionDetailDtacInquiryShelfDetailRsp.RelatedParty parseRelatedPartyObj(ShelfSectionDetailDtacInquiryShelfDetailRsp.RelatedParty rp) {
        ShelfSectionDetailDtacInquiryShelfDetailRsp.RelatedParty relatedParty = new ShelfSectionDetailDtacInquiryShelfDetailRsp.RelatedParty();
        relatedParty.setId(rp.getId() != null ? rp.getId():"");
        relatedParty.setName(rp.getName() != null ? rp.getName():"");
        relatedParty.setHref(rp.getDescription() != null ? rp.getDescription():"");
        relatedParty.setDescription(rp.getHref() != null ? rp.getHref():"");
        return relatedParty;
    }

    private ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteriaGroup parsePromotionCriteriaGroupObj(ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteriaGroup pcg) {

        ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteriaGroup relatedParty = new ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteriaGroup();
        relatedParty.setId(pcg.getName() != null ? pcg.getName():"");
        relatedParty.setType(pcg.getType() != null ? pcg.getType():"");
        relatedParty.setDescription(pcg.getDescription() != null ? pcg.getDescription():"");
        relatedParty.setShortDescription(pcg.getShortDescription() != null ? pcg.getShortDescription():"");

        return relatedParty;
    }
}
