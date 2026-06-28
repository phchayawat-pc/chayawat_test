package th.co.truecorp.commonapi.reward.service;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.service.*;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.endpoint.CampaignGroupServiceEndpoint;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonapi.reward.model.redeem.ShelfMajorRsp;
import th.co.truecorp.commonapi.reward.redis.RedisCacheService;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.model.EndpointResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class ShelfMajorListService {

    private static Logger log = LoggerFactory.getLogger(ShelfMajorListService.class);

    Gson gson = new Gson();

    @Autowired
    private ResultService resultService;

    @Autowired
    private APIGWUtill apigwUtill;

    @Autowired
    private RewardUtill rewardUtill;

    @Autowired
    private ErrorService errorService;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private CampaignGroupServiceEndpoint campaignGroupServiceEndpoint;
    
    @Autowired
    private ShelfGroupingListService shelfGroupingListService;

    @EndpointLog (name = "TRUEAPP.GetMajor")
    public EndpointResultRWD getMajorService(Map<String, Object> tv) throws Exception {
        log.info("Start getMajor Service");

        EndpointResultRWD endpointResultRwd = new EndpointResultRWD();
        EndpointResult endpointResult = null;
        tv.put(ComnConst.KEY_LANGUAGE, rewardUtill.handleLanguage(tv.get(ComnConst.KEY_LANGUAGE).toString()));
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
        String brand = tv.get("brand").toString();
        String itemType = tv.get("itemType") != null ? tv.get("itemType").toString() : "";
        String itemMapping = tv.get("itemMapping") != null ? tv.get("itemMapping").toString() : "";
        String useCMS = tv.get("useCMS") != null ? tv.get("useCMS").toString().toUpperCase() : "";
        String templateCode = tv.get("templateCode") != null ? tv.get("templateCode").toString().toUpperCase() : "MAJOR_PAGE";
        String sectionId = tv.get("sectionId") != null ? tv.get("sectionId").toString().toUpperCase() : "";
        String sourceApiName = tv.get("sourceApiName") != null ? tv.get("sourceApiName").toString() : "";
        String shelfId = tv.get("shelfId") != null ? tv.get("shelfId").toString() : "";

        try {
            ShelfMajorRsp shelfMajorRsp = new ShelfMajorRsp();

            if(apigwUtill.isDtac(brand)) {
                if(useCMS.equals("N")){

                    String levelSegment = rewardUtill.mapUserLevel(tv);
                    tv.put("levelSegment",levelSegment);
                    endpointResult = campaignGroupServiceEndpoint.getCampaignGroup(tv);
                    if(endpointResult.getHttpStatus() == ComnConst.STTS_HTTP_SUCC){
                        shelfMajorRsp = mapMajorFromApi(tv);
                    }else{
                       return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
                    }
                }else{
                    shelfMajorRsp = getRadisMapMajor(tv);//Major 1
                }

                if(shelfMajorRsp.getDealList() != null){
                    endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                    endpointResultRwd = errorService.convertMapResult(endpointResult);
                    tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResultRwd);
                    log.info("Deals found, successful response returned.");
                } else {
                    endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
                            apigwUtill.isDtac(brand) ? "DTAC" : "TRUE",
                            Objects.requireNonNull(Constant.ERROR_DATA_NOT_FOUND),
                            tv.get(ComnConst.KEY_LANGUAGE).toString(),
                            "data not found",
                            Constant.N_A,
                            Constant.MESSAGE);
                    log.info("No deals found, returning error response.");
                }
            }else{
                endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
                        apigwUtill.isDtac(brand) ? "DTAC" : "TRUE",
                        Objects.requireNonNull(Constant.ERROR_TEMPLATE_NOT_SUPPORTED),
                        tv.get(ComnConst.KEY_LANGUAGE).toString(),
                        "Not supported for this template",
                        Constant.N_A,
                        Constant.MESSAGE);
                log.info("Not supported for this template.");
            }

            tv.put(Constant.TRANSACTION_RESPONSE_KEY,shelfMajorRsp);

            log.info("Response returned: " + endpointResultRwd);
        } catch (Exception e) {
            log.info("Error occurred during getMajorService", e);
            endpointResultRwd = errorService.convertMapResult(errorService.mapErrorException(e,tv));
            return endpointResultRwd;
        }

        return endpointResultRwd;
    }

    private ShelfMajorRsp getRadisMapMajor(Map<String, Object> tv) throws Exception {
        log.info("get Radis Map Major");
        ShelfMajorRsp shelfMajorRsp = new ShelfMajorRsp();

        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
        String brand = tv.get("brand").toString();
        String itemType = tv.get("itemType") != null ? tv.get("itemType").toString() : "";
        String itemMapping = tv.get("itemMapping") != null ? tv.get("itemMapping").toString() : "";
        String templateCode = tv.get("templateCode") != null ? tv.get("templateCode").toString().toUpperCase() : "MAJOR_PAGE";
        String sectionId = tv.get("sectionId") != null ? tv.get("sectionId").toString().toUpperCase() : "";
        String sourceApiName = tv.get("sourceApiName") != null ? tv.get("sourceApiName").toString() : "";
        String shelfId = tv.get("shelfId") != null ? tv.get("shelfId").toString() : "";

        // Logging the input data
        log.info("Processing itemType: " + itemType );
        String type = "";
        switch (itemType) {
            case Constant.MAJOR:
                type = "2";
                break;
            case Constant.GROUPING:
                type = "7";
                break;
            case Constant.FESTIVE:
                type = "8";
                break;
        }

        if(!sectionId.equals("")){

            if(sourceApiName.equals("getSectionDetail")){
                log.info("Fetching details for getSectionDetail");

                String redisKey = sectionId+":rawdata:getSectionDetail:"+shelfId+":"+lang;
                log.info("Redis key for getSectionDetail: " + redisKey);

                shelfMajorRsp.setMajorId("");
                shelfMajorRsp.setLang(lang);
                shelfMajorRsp.setMajorName("");
                shelfMajorRsp.setThumbnailList(null);
                shelfMajorRsp.setTemplateCode(templateCode);
//                shelfMajorRsp.setSectionId(sectionId);
                shelfMajorRsp.setDealList(null);

                ShelfSectionDetailDtacInquiryShelfDetailRsp dataDetails = redisCacheService.get(redisKey,ShelfSectionDetailDtacInquiryShelfDetailRsp.class);

                if(dataDetails != null) {
                    log.info("Successfully fetched data from Redis for sectionId : " + sectionId + ", ShelfSectionDetailDtacInquiryShelfDetailRsp : " + gson.toJson(dataDetails));
                    List<ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionPattern> promotionPatterns = new ArrayList<>();
                    promotionPatterns = dataDetails.getPromotionPattern();
                    for (ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionPattern pp : promotionPatterns) {
                        log.info("Checking promotionPattern ID: " + pp.getBanner().getId() + " and type: " + pp.getBanner().getType());
                        if (pp.getBanner() != null && (itemMapping.equals(pp.getBanner().getId()) && type.equals(pp.getBanner().getType()))) {

                            ShelfMajorRsp.Thumbnail thumbnail = new ShelfMajorRsp.Thumbnail();
                            thumbnail.setThumbnail18x7(pp.getBanner().getDescription());

                            shelfMajorRsp.setMajorName(pp.getBanner().getName());
                            shelfMajorRsp.setThumbnailList(thumbnail);
                            shelfMajorRsp.setDealList(mapMajorDetailDealList(pp.getBanner().getCampaigns()));
                            log.info("Matching promotion pattern found and added to response.");
                        }
                    }
                }else{
                    log.info("No data found in Redis for sectionId: " + sectionId);
                }
            }else{
                log.info("Fetching details for " + sourceApiName);

                String redisKey = sectionId + ":rawdata:getSectionHeader:"+lang;
                log.info("Redis key for getSectionHeader: " + redisKey);

                shelfMajorRsp.setMajorId("");
                shelfMajorRsp.setLang(lang);
                shelfMajorRsp.setMajorName("");
                shelfMajorRsp.setThumbnailList(null);
                shelfMajorRsp.setTemplateCode(templateCode);
//                shelfMajorRsp.setSectionId(sectionId);
                shelfMajorRsp.setDealList(null);

                ShelfDtacInquiryShelfDetailApiRsp dataDetails = redisCacheService.get(redisKey,ShelfDtacInquiryShelfDetailApiRsp.class);

                if(dataDetails != null && dataDetails.getPattern() != null && !dataDetails.getPattern().isEmpty()){
                    log.info("Successfully fetched data from Redis for sectionId : " + sectionId + ", ShelfDtacInquiryShelfDetailApiRsp : " + gson.toJson(dataDetails));
                    List<ShelfDtacInquiryShelfDetailApiRsp.Pattern.PromotionPattern> promotionPatterns = new ArrayList<>();
                    promotionPatterns = dataDetails.getPattern().get(0).getPromotionPattern();
                    for (ShelfDtacInquiryShelfDetailApiRsp.Pattern.PromotionPattern sd : promotionPatterns) {
                        log.info("Checking promotionPattern ID: " + sd.getBanner().getId() + " and type: " + sd.getBanner().getType());
                        if(itemMapping.equals(sd.getBanner().getId()) && type.equals(sd.getBanner().getType())){

                            ShelfMajorRsp.Thumbnail thumbnail = new ShelfMajorRsp.Thumbnail();
                            thumbnail.setThumbnail18x7(sd.getBanner().getDescription());

                            shelfMajorRsp.setMajorName(sd.getName());
                            shelfMajorRsp.setThumbnailList(thumbnail);
                            shelfMajorRsp.setDealList(mapMajorHeaderDealList(sd.getBanner().getCampaigns()));
                            log.info("Matching promotion pattern found and added to response.");
                        }
                    }
                }else{
                    log.info("No data found in Redis for sectionId: " + sectionId);
                }
            }

        }else{
            log.info("sectionId is missing or empty");
            shelfMajorRsp.setMajorId("");
            shelfMajorRsp.setLang(lang);
            shelfMajorRsp.setMajorName("");
            shelfMajorRsp.setThumbnailList(null);
            shelfMajorRsp.setTemplateCode(templateCode);
//            shelfMajorRsp.setSectionId("");
            shelfMajorRsp.setDealList(null);
        }

        return shelfMajorRsp;
    }

    private List<ShelfMajorRsp.DealList> mapMajorHeaderDealList(List<ShelfDtacInquiryShelfDetailApiRsp.Pattern.Campaign> campaigns) throws ParseException {
        log.info("map Major Header DealList");
        List<ShelfMajorRsp.DealList> dealList = new ArrayList<ShelfMajorRsp.DealList>();

        int seqNo = 0;
        if(campaigns != null && !campaigns.isEmpty()){
            for(ShelfDtacInquiryShelfDetailApiRsp.Pattern.Campaign campaign : campaigns){

                Date date = new Date();
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startDateTime = campaign.getValidFor() != null ? inputFormat.parse(campaign.getValidFor().getStartDateTime()) : null;
                Date endDateTime = campaign.getValidFor() != null ? inputFormat.parse(campaign.getValidFor().getEndDateTime()) : null;

                if ((startDateTime != null && endDateTime != null ) && ((startDateTime.compareTo(date) <= 0) && (endDateTime.compareTo(date) >= 0))) {

                    Map<String, String> typeMap = new HashMap<>();
                    typeMap.put("1", Constant.DTAC_REWARD);
                    typeMap.put("2", Constant.COIN);

                    String campaignType = typeMap.getOrDefault(campaign.getType(), null);

                    Map<String, List<String>> userLevelMap = new HashMap<>();
                    userLevelMap.put("Welcome", List.of("welcome", "silver", "gold", "platinum_blue"));
                    userLevelMap.put("Silver", List.of("silver", "gold", "platinum_blue"));
                    userLevelMap.put("Gold", List.of("gold", "platinum_blue"));
                    userLevelMap.put("Platinum Blue", List.of("platinum_blue"));

                    List<String> cardTypes = userLevelMap.getOrDefault(campaign.getRelationTypeInGroup(), List.of("no card"));

                    ShelfMajorRsp.DealList deal = new ShelfMajorRsp.DealList();
                    ShelfMajorRsp.Thumbnail thumbnail = new ShelfMajorRsp.Thumbnail();
                    thumbnail.setThumbnail3x2(campaign.getHref());

                    deal.setSeqNo(String.valueOf(seqNo));
                    deal.setCampaignId(campaign.getId());
                    deal.setCampaignCode(campaign.getPromotionCriteriaGroup() != null ? campaign.getPromotionCriteriaGroup().getId() : null);
                    deal.setContentType(Constant.DEAL);
                    deal.setTimecounterFlag("N");
                    deal.setThumbnailList(thumbnail);
                    deal.setCampaignName(null);
                    deal.setCampaignDescription(campaign.getPromotionCriteriaGroup() != null ? campaign.getPromotionCriteriaGroup().getShortDescription() : null);
                    deal.setCampaignExpireDate(inputFormat.format(endDateTime));
                    deal.setCampaignType(campaignType);
                    deal.setCardType(cardTypes);
                    deal.setRegularPoint(null);
                    deal.setOfferPoint(null);
                    seqNo++;
                    dealList.add(deal);
                }
            }
        }else{
            log.info("Campaign is null");
            dealList = null;
        }

        return dealList;
    }

    private List<ShelfMajorRsp.DealList> mapMajorDetailDealList(List<ShelfSectionDetailDtacInquiryShelfDetailRsp.Campaign> campaigns) throws ParseException {
        log.info("map Major Detail DealList");
        List<ShelfMajorRsp.DealList> dealList = new ArrayList<ShelfMajorRsp.DealList>();

        int seqNo = 0;
        if(campaigns != null && !campaigns.isEmpty()){
            for(ShelfSectionDetailDtacInquiryShelfDetailRsp.Campaign campaign : campaigns){

                Map<String, String> typeMap = new HashMap<>();
                typeMap.put("1", Constant.DTAC_REWARD);
                typeMap.put("2", Constant.COIN);

                String campaignType = typeMap.getOrDefault(campaign.getType(), null);

                Map<String, List<String>> userLevelMap = new HashMap<>();
                userLevelMap.put("Welcome", List.of("welcome", "silver", "gold", "platinum_blue"));
                userLevelMap.put("Silver", List.of("silver", "gold", "platinum_blue"));
                userLevelMap.put("Gold", List.of("gold", "platinum_blue"));
                userLevelMap.put("Platinum Blue", List.of("platinum_blue"));

                List<String> cardTypes = userLevelMap.getOrDefault(campaign.getRelationTypeInGroup(), List.of("no card"));

                Date date = new Date();
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startDateTime = campaign.getValidFor() != null ? inputFormat.parse(campaign.getValidFor().getStartDateTime()) : null;
                Date endDateTime = campaign.getValidFor() != null ? inputFormat.parse(campaign.getValidFor().getEndDateTime()) : null;

                if ((startDateTime != null && endDateTime != null) && ((startDateTime.compareTo(date) <= 0) && (endDateTime.compareTo(date) >= 0))) {

                    ShelfMajorRsp.DealList deal = new ShelfMajorRsp.DealList();
                    ShelfMajorRsp.Thumbnail thumbnail = new ShelfMajorRsp.Thumbnail();
                    thumbnail.setThumbnail3x2(campaign.getHref());

                    deal.setSeqNo(String.valueOf(seqNo));
                    deal.setCampaignId(campaign.getId());
                    deal.setCampaignCode(campaign.getPromotionCriteriaGroup() != null ? campaign.getPromotionCriteriaGroup().getId() : null);
                    deal.setContentType(Constant.DEAL);
                    deal.setTimecounterFlag("N");
                    deal.setThumbnailList(thumbnail);
                    deal.setCampaignName(null);
                    deal.setCampaignDescription(campaign.getPromotionCriteriaGroup() != null ? campaign.getPromotionCriteriaGroup().getShortDescription() : null);
                    deal.setCampaignExpireDate(inputFormat.format(endDateTime));
                    deal.setCampaignType(campaignType);
                    deal.setCardType(cardTypes);
                    deal.setRegularPoint(null);
                    deal.setOfferPoint(null);
                    seqNo++;
                    dealList.add(deal);
                }
            }
        }else{
            log.info("Campaign is null");
            dealList = null;
        }

        return dealList;
    }

    public ShelfMajorRsp mapMajorFromApi(Map<String, Object> tv) throws ParseException {
        log.info("start map major from api");

        CampaignGroupResponse campaignGroupResponse = (CampaignGroupResponse) tv.get(Constant.ENDPOINT_SERVICE_CAMPAIGN_GROUP);

        String languageKey = Objects.equals(tv.get(ComnConst.KEY_LANGUAGE).toString().toUpperCase(), Constant.TH) ? "thai" : "english";

        String groupingName = Optional.ofNullable(campaignGroupResponse.getData())
                .map(DataContainer::getDetailLanguage)
                .map(lang -> lang.getOrDefault(languageKey, ""))
                .orElse("");

        String groupingId = Optional.ofNullable(tv.get("itemMapping"))
                .map(Object::toString)
                .orElse("");

        String newUrl = Optional.ofNullable(campaignGroupResponse.getData())
                .map(DataContainer::getBannerInfo)
                .map(BannerInfo::getNewUrl)
                .orElse("Default URL");

        String templateCode = Objects.toString(tv.get("templateCode"), "MAJOR_PAGE").toUpperCase();

        int seqNo = 0;

        ShelfMajorRsp.Thumbnail thumbnail = new ShelfMajorRsp.Thumbnail();
        thumbnail.setThumbnail18x7(newUrl);

        ShelfMajorRsp ShelfMajorRsp = new ShelfMajorRsp();
        ShelfMajorRsp.setMajorId(groupingId);
        ShelfMajorRsp.setLang(tv.get(ComnConst.KEY_LANGUAGE).toString());
        ShelfMajorRsp.setMajorName(groupingName);
        ShelfMajorRsp.setThumbnailList(thumbnail);
        ShelfMajorRsp.setTemplateCode(templateCode);

        List<ShelfMajorRsp.DealList> dealList = new ArrayList<ShelfMajorRsp.DealList>();

        if(campaignGroupResponse.getData()!=null &&campaignGroupResponse.getData().getCampaigns()!= null){
            for(Campaign campaign : campaignGroupResponse.getData().getCampaigns()) {
                LocalDateTime startDateTime = campaign.getStartDate();
                LocalDateTime endDateTime = campaign.getEndDate();

                if ((!LocalDateTime.now().isBefore(startDateTime)) && (!LocalDateTime.now().isAfter(endDateTime))) {

                    ShelfMajorRsp.DealList list = new ShelfMajorRsp.DealList();

                    ShelfMajorRsp.Thumbnail thumbnailDeal = new ShelfMajorRsp.Thumbnail();
                    thumbnailDeal.setThumbnail3x2(campaign.getImageUrl());

                    String campaignDescription = Optional.ofNullable(campaign.getNameLanguage().get(languageKey))
                            .map(Object::toString)
                            .orElse("");

                    String campaignExpireDate = Optional.ofNullable(campaign.getEndDate())
                            .map(Object::toString)
                            .orElse("");

                    String typeValue = Optional.ofNullable(campaign.getCharacteristic())
                            .orElse(Collections.emptyList())
                            .stream()
                            .filter(c -> "type".equals(c.getName()))
                            .map(Characteristic::getValue)
                            .findFirst()
                            .orElse(null);

                    Map<String, String> typeMap = new HashMap<>();
                    typeMap.put("1", Constant.DTAC_REWARD);
                    typeMap.put("2", Constant.COIN);

                    String campaignType = typeMap.getOrDefault(typeValue, null);

                    String campaignId = Optional.ofNullable(campaign.getCharacteristic())
                            .orElse(Collections.emptyList())
                            .stream()
                            .filter(c -> "campaignIdDtac".equals(c.getName()))
                            .map(Characteristic::getValue)
                            .findFirst()
                            .orElse(null);

                    List<String> cardTypes = rewardUtill.levelCardType(campaign.getUserLevel());

                    String regularPoint = Optional.ofNullable(campaign.getOriginalPoint())
                            .map(Object::toString)
                            .orElse(null);

                    String offerPoint = Optional.ofNullable(campaign.getPointPerUnit())
                            .map(Object::toString)
                            .orElse(null);

                    list.setSeqNo(String.valueOf(seqNo));
                    list.setCampaignCode(campaign.getId());
                    list.setThumbnailList(thumbnailDeal);
                    list.setCampaignDescription(campaignDescription);
                    list.setCampaignExpireDate(campaignExpireDate);
                    list.setCampaignType(campaignType);
                    list.setCardType(cardTypes);
                    list.setRegularPoint(regularPoint);
                    list.setOfferPoint(offerPoint);
                    list.setTimecounterFlag("N");
                    list.setContentType(Constant.DEAL);
                    list.setCampaignId(campaignId);

                    dealList.add(list);
                    seqNo++;
                }
                ShelfMajorRsp.setDealList(dealList);
            }
        }else{
            ShelfMajorRsp.setDealList(dealList);
        }
        log.info("end map Major from api");
        return ShelfMajorRsp;
    }

}
