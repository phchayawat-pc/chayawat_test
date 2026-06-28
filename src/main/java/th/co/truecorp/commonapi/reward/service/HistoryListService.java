package th.co.truecorp.commonapi.reward.service;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdRedeemHistory;
import th.co.truecorp.commonapi.reward.cms.jpa.service.CustomMappingMessageService;
import th.co.truecorp.commonapi.reward.cms.jpa.service.ErrorService;
import th.co.truecorp.commonapi.reward.cms.jpa.service.RwdRedeemHistoryService;
import th.co.truecorp.commonapi.reward.common.dto.PageDTO;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.endpoint.EarnHistoryServiceEndpoint;
import th.co.truecorp.commonapi.reward.endpoint.GetTransactionServiceEndpoint;
import th.co.truecorp.commonapi.reward.endpoint.PrivilegeHistoryServiceEndpoint;
import th.co.truecorp.commonapi.reward.common.model.DateRange;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.endpoint.RedeemDtacScanCodeEndpoint;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemCampaignDetail.CampaignDetailResponse;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemCampaignDetail.CampaignInfo;
import th.co.truecorp.commonapi.reward.model.endpoint.redeem.redeemCampaignDetail.CouponDetail;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.context.ContextSignature;
import th.co.truecorp.commonlib.log.context.LogContextService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class HistoryListService {

    @Autowired
    GetTransactionServiceEndpoint getTransactionServiceEndpoint ;

    @Autowired
    PrivilegeHistoryServiceEndpoint privilegeHistoryServiceEndpoint;

    @Autowired
    EarnHistoryServiceEndpoint earnHistoryServiceEndpoint ;

    @Autowired
    RedeemDtacScanCodeEndpoint redeemDtacScanCodeEndpoint;

    @Autowired
    private RwdRedeemHistoryService rwdRedeemHistoryService;

    @Autowired
    private ResultService resultService;

    @Autowired
    private APIGWUtill apigwUtill;

    @Autowired
    private LogContextService logContextService;

    @Autowired
    private RewardUtill rewardUtill;

    @Autowired
    private CustomMappingMessageService customMappingMessageService;

    @Autowired
    private ErrorService errorService;

    Gson gson = new Gson();

    private static Logger log = LoggerFactory.getLogger(HistoryListService.class);

//    @EndpointLog (name = "TRUEAPP.HistoryPoint")
    public EndpointResultRWD historyPointListService(Map<String, Object> tv, String brand, String subType, String strDate) throws Exception {
        log.info("Starting historyPointListService - Brand: {}, SubType: {}, Date: {}", brand, subType, strDate);
        ContextSignature contextSignature = logContextService.getCurrentContextSignature();

        EndpointResultRWD endpointResultRwd = new EndpointResultRWD();
        List<HistoryPointRsp> historyPointRsps = new ArrayList<>();
        List<HistoryPointRsp> redemptionHistoryRsp  = new ArrayList<>();
        int paging = tv.get("paging")!=null ? Integer.parseInt(tv.get("paging").toString()):1;
        int limit = tv.get("limit")!=null ? Integer.parseInt(tv.get("limit").toString()):10;
        tv.put("limit", String.valueOf(limit));
        log.info("start get history");
        log.info("Fetching history points with paging: {}, limit: {}", paging, limit);

        tv.put(ComnConst.KEY_LANGUAGE, rewardUtill.handleLanguage(tv.get(ComnConst.KEY_LANGUAGE).toString()));
        log.info("Language set to: {}", tv.get(ComnConst.KEY_LANGUAGE));

        try {
            if (apigwUtill.isDtac(brand)) {
                log.info("Handling Dtac brand for history points.");
                CompletableFuture<List<HistoryPointRsp>> future = CompletableFuture.supplyAsync(() -> {
                    List<HistoryPointRsp> redemptionHistoryRspList = new ArrayList<>();
                    try {
                        redemptionHistoryRspList = handleDtacBrandCampaignDetail(tv, strDate, contextSignature);

                    } catch (Exception e) {
                        log.error("Error in handleDtacBrandCampaignDetail: {}", e.getMessage(), e);
                    }

                    return redemptionHistoryRspList;
                });

                List<HistoryPointRsp> redemptionHistoryRspList = future.join();  // หรือใช้ .get()
                if(!redemptionHistoryRspList.isEmpty()){
                    tv.put("redemptionHistory",redemptionHistoryRspList);
                    redemptionHistoryRsp = (List<HistoryPointRsp>) tv.get("redemptionHistory");
                }

                endpointResultRwd = handleDtacBrand(tv, strDate, historyPointRsps);
            } else {
                log.info("Handling True brand for history points.");
                endpointResultRwd = handleTrueBrand(tv, strDate, historyPointRsps);
            }

            log.info("historyPointRsps.: {} ", gson.toJson(historyPointRsps));
            if (!historyPointRsps.isEmpty() || !redemptionHistoryRsp.isEmpty()) {
                processPaging(tv, historyPointRsps, paging, limit);
                filterDateTabPoint(tv);
                filterDateTabRedemption(tv, brand);
                if (tv.get("DateTabPoint") == null && tv.get("DateTabRedemption") == null) {
                    log.debug(Constant.ENDPOINT_SERVICE_GET_HISTORY + " is failed");
                    endpointResultRwd = errorService.convertMapResult(resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC, ComnConst.STTS_CODE_SUCC));
                } else {
                    log.debug(Constant.ENDPOINT_SERVICE_GET_HISTORY + " is success");
                    endpointResultRwd = errorService.convertMapResult(resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA));
                }
            }else{
                log.info(Constant.ENDPOINT_SERVICE_GET_HISTORY + " is no data");
                PageDTO pageReq = new PageDTO();
                pageReq.setPageNumber(paging);
                pageReq.setPageSize(limit);
                pageReq.setCount(0);
                pageReq.setTotalPage(0);

                tv.put("page", pageReq);
            }

            log.info("Final endpointResultRwd: {}", endpointResultRwd);
        } catch (Exception e) {
            log.error("Error in historyPointListService: {}", e.getMessage(), e);
            endpointResultRwd = errorService.convertMapResult(resultService.getEndpointExceptionResult(tv, e));
            return endpointResultRwd;
        }
        log.info("Ending historyPointListService.");
        return endpointResultRwd;
    }

    private List<HistoryPointRsp> handleDtacBrandCampaignDetail(
            Map<String, Object> tv, String strDate, ContextSignature contextSignature) throws Exception {
        log.info("Fetching campaign details for Dtac brand.");
        logContextService.joinContext(contextSignature);
        DateRange dateRange = APIGWUtill.createDateRange(strDate);
        tv.put("startDate", dateRange.getStartDate());
        tv.put("endDate", dateRange.getEndDate());
        String brand = tv.get("brand").toString().toUpperCase();
        String digitalId = tv.get("digitalId").toString();
        String language = tv.get(ComnConst.KEY_LANGUAGE).toString().toUpperCase();

        // ค้นหา campaignId และ RwdRedeemHistory ในครั้งเดียวเพื่อลดจำนวนการ query
        List<String> campaignIdList = rwdRedeemHistoryService.findCampaignIdToHistoryPointDto(
                "SUCCESS", brand, digitalId, dateRange.getStartDate(), dateRange.getEndDate());
        log.info("Campaign IDs fetched: {}", campaignIdList);
        List<RwdRedeemHistory> historyPointDtos = rwdRedeemHistoryService.findRwdRedeemHistoryToHistoryPointDto(
                "SUCCESS", brand, digitalId, dateRange.getStartDate(), dateRange.getEndDate());
        log.info("Redeem history fetched: {}", gson.toJson(historyPointDtos));
        // ประมวลผล CampaignDetailResponse แบบขนาน
        Map<String, CampaignDetailResponse> campaignDetailResponseMap = campaignIdList.parallelStream()
                .map(cmpId -> {
                    try {
                        logContextService.joinContext(contextSignature);
                        Map<String, Object> queryParam = mapCampaignDetailQueryParam(tv, cmpId);
                        EndpointResultRWD result = fetchRedeemCampaignDetail(tv, queryParam, contextSignature);
                        if (result.getHttpStatus() == 200) {
                            return Map.entry(cmpId, (CampaignDetailResponse) tv.get(Constant.ENDPOINT_SERVICE_GET_CAMPAIGN_DETAIL));
                        }
                    } catch (Exception e) {
                        log.error("Error processing campaignId: {}", cmpId, e);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // สร้างผลลัพธ์ HistoryPointRsp
        List<HistoryPointRsp> historyPointRsps = historyPointDtos.stream()
                .map(dto -> {
                    CampaignDetailResponse campaignDetailResponse = campaignDetailResponseMap.get(dto.getCampaignId());
                    if (campaignDetailResponse == null ||
                            campaignDetailResponse.getCampaignInfo() == null ||
                            campaignDetailResponse.getCampaignInfo().isEmpty()) {
                        return null;
                    }

                    CampaignInfo campaignInfo = campaignDetailResponse.getCampaignInfo().get(0);

                    String couponExpireDate = Optional.ofNullable(dto.getCouponExpireDate())
                            .map(date -> date.toLocalDateTime()
                                    .plusMinutes(campaignInfo.getValidFor().getRemainingDays())
                                    .atZone(ZoneOffset.ofHours(7))
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")))
                            .orElse("");

                    LocalDateTime couponExpiryDateTime = couponExpireDate.isEmpty() ? null :
                            LocalDateTime.parse(couponExpireDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
                    boolean isOverTime = couponExpiryDateTime != null && couponExpiryDateTime.isBefore(LocalDateTime.now());

                    return buildHistoryPointRsp(dto, campaignInfo, couponExpireDate, isOverTime, language);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        log.info("redeem history is: {} ", new Gson().toJson(historyPointRsps));
        log.info("end get redeem history");
        return historyPointRsps;
    }

    private HistoryPointRsp buildHistoryPointRsp(RwdRedeemHistory dto, CampaignInfo campaignInfo,
                                                 String couponExpireDate, boolean isOverTime, String language) {
        log.info("Building HistoryPointRsp for DTO: {}, CampaignInfo: {}, Language: {}, CouponExpireDate: {}, IsOverTime: {}",
                dto.getId(), campaignInfo.getId(), language, couponExpireDate, isOverTime);

        HistoryPointRsp redemptionHistoryRsp = new HistoryPointRsp();
        ThumbnailRsp thumbnail = new ThumbnailRsp();
        log.info("Setting thumbnail highlight16x9 with href: {}", campaignInfo.getHref());
        thumbnail.setHighlight16x9(campaignInfo.getHref());
        redemptionHistoryRsp.setThumbnail(thumbnail);

        log.info("Setting campaign name based on language: {}", language);
        redemptionHistoryRsp.setCampaignName(Optional.ofNullable(dto.getCouponCode())
                .map(code -> language.equalsIgnoreCase(Constant.TH) ? campaignInfo.getName().getTh() : campaignInfo.getName().getEn())
                .orElse(null));
        log.debug("Campaign name set to: {}", redemptionHistoryRsp.getCampaignName());

        log.info("Setting campaign description.");
        redemptionHistoryRsp.setCampaignDescription(Optional.ofNullable(dto.getCouponCode())
                .map(code -> language.equalsIgnoreCase(Constant.TH) ? campaignInfo.getDescription().getTh() : campaignInfo.getDescription().getEn())
                .orElse(dto.getPackageName()));
        log.debug("Campaign description set to: {}", redemptionHistoryRsp.getCampaignDescription());

        log.info("Setting points: {}", campaignInfo.getPointPerUnit());
        redemptionHistoryRsp.setPoints(campaignInfo.getPointPerUnit());

        log.info("Setting condition information.");
        String relatedPartyName = null;
        if (campaignInfo.getRelatedParty() != null && !campaignInfo.getRelatedParty().isEmpty()) {
            relatedPartyName = language.equalsIgnoreCase(Constant.TH)
                    ? campaignInfo.getRelatedParty().get(0).getName()
                    : campaignInfo.getRelatedParty().get(0).getNameEn();
        }

        redemptionHistoryRsp.setConditionInfo(
                conditionInfo(language, campaignInfo.getCouponDetail(), relatedPartyName)
        );

        log.debug("Condition information set to: {}", redemptionHistoryRsp.getConditionInfo());

        log.info("Formatting action date.");
        redemptionHistoryRsp.setDate(Optional.ofNullable(dto.getActionDate())
                .map(date -> date.toLocalDateTime()
                        .atZone(ZoneOffset.ofHours(7))
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")))
                .orElse(null));
        log.debug("Action date set to: {}", redemptionHistoryRsp.getDate());

        redemptionHistoryRsp.setCouponCode(dto.getCouponCode());
        redemptionHistoryRsp.setCouponExpiryDate(couponExpireDate);
        redemptionHistoryRsp.setCampaignType(isOverTime ? Constant.USED : Constant.AVAILABLE);
        redemptionHistoryRsp.setTimeCounterFlag(dto.getCouponCode() != null ? "Y" : "N");

        String textButton = customMappingMessageService.getMappingMessage(Constant.MESSAGE_TEXT_BUTTOM,
                Constant.BUTTON, language, Constant.MESSAGE);
        redemptionHistoryRsp.setTextButton(textButton);
        log.debug("Text button set to: {}", redemptionHistoryRsp.getTextButton());

        return redemptionHistoryRsp;
    }

    private EndpointResultRWD handleDtacBrand(Map<String, Object> tv, String strDate, List<HistoryPointRsp> historyPointRsps) throws Exception {
        log.info("Handling Dtac brand history points for date: {}", strDate);
        DateRange dateRange = APIGWUtill.createDateRange(strDate);
        log.info("Date range created - Start: {}, End: {}", dateRange.getStartDate(), dateRange.getEndDate());
        tv.put("startDate", dateRange.getStartDate());
        tv.put("endDate", dateRange.getEndDate());

        EndpointResultRWD endpointResultRwd = fetchHistoryPoints(tv, Constant.USED);
        if (isSuccess(endpointResultRwd)) {
            log.info("UsedPrivilegeHistory fetched successfully. Adding to historyPointRsps.");
            historyPointRsps.addAll((Collection<? extends HistoryPointRsp>) tv.get("UsedPrivilegeHistory"));

            endpointResultRwd = fetchHistoryPoints(tv, Constant.AVAILABLE);
            if (isSuccess(endpointResultRwd)) {
                log.info("AvailablePrivilegeHistory fetched successfully. Adding to historyPointRsps.");
                historyPointRsps.addAll((Collection<? extends HistoryPointRsp>) tv.get("AvailablePrivilegeHistory"));

                tv.put("transactionId", apigwUtill.generateRewardBackendId());
                earnHistoryServiceEndpoint.getEarnHistoryService(tv);
                endpointResultRwd = (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
                if (isSuccess(endpointResultRwd)) {
                    log.info("Earn history points fetched successfully. Adding to historyPointRsps.");
                    historyPointRsps.addAll((Collection<? extends HistoryPointRsp>) tv.get("EarnHistory"));
                } else {
                    log.info("Failed to fetch earn history points. Clearing historyPointRsps.");
                    historyPointRsps.clear();
                }
            }
        }

        return endpointResultRwd;
    }

    private EndpointResultRWD handleTrueBrand(Map<String, Object> tv, String strDate, List<HistoryPointRsp> historyPointRsps) throws Exception {
        log.info("Handling True brand history points for date: {}", strDate);
        DateRange dateRange = APIGWUtill.createDateRangeFormat(strDate, "yyyy-MM-dd'T'HH:mm:ss.SSZ");
        tv.put("dateFrom", dateRange.getStartDate());
        tv.put("dateTo", dateRange.getEndDate());

        getTransactionServiceEndpoint.getTransaction(tv);
        EndpointResultRWD endpointResultRwd = (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
        List<HistoryPointRsp> historyPointRspsList = (List<HistoryPointRsp>) tv.get("historyPointRsps");
        if(historyPointRspsList != null && !historyPointRspsList.isEmpty()) {
            log.info("History points retrieved successfully. Adding {} entries to historyPointRsps.", historyPointRspsList.size());
            historyPointRsps.addAll(historyPointRspsList);
        }
        log.info("end handleTrueBrand");
        return endpointResultRwd;
    }

    private void processPaging(Map<String, Object> tv, List<HistoryPointRsp> historyPointRsps, int paging, int limit) {
        PagedResult<HistoryPointRsp> pagedHistoryPoints = apigwUtill.paginate(historyPointRsps, paging, limit);

        PageDTO pageReq = new PageDTO();
        pageReq.setPageNumber(paging);
        pageReq.setPageSize(limit);
        pageReq.setCount(pagedHistoryPoints.getTotalCount());
        pageReq.setTotalPage(pagedHistoryPoints.getTotalPages());

        tv.put("page", pageReq);
        tv.put("HistoryPointResponses", pagedHistoryPoints.getItems());
    }

    private boolean isSuccess(EndpointResultRWD endpointResult) {
        return Objects.equals(endpointResult.getEndpointStatusType(), ComnConst.STTS_TYPE_SUCC);
    }

    private EndpointResultRWD fetchHistoryPoints(Map<String, Object> tv, String productStatus) {
        tv.put("transactionId", apigwUtill.generateRewardBackendId());
        tv.put("productStatus", productStatus);
        privilegeHistoryServiceEndpoint.getPrivilegeHistoryService(tv);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

//    SC2,3,4,5,6,7,8
    private void filterDateTabPoint (Map<String, Object> tv){
        List<HistoryPointRsp> dataObject = (List<HistoryPointRsp>) tv.get("HistoryPointResponses");
        List<HistoryPointRsp> filteredLists = new ArrayList<HistoryPointRsp>();
        try{
            filteredLists.addAll(filterSC2(dataObject));
            filteredLists.addAll(filterSC3(dataObject));
            filteredLists.addAll(filterSC5(dataObject));
            filteredLists.addAll(filterSC6(dataObject));
            filteredLists.addAll(filterSC7(dataObject));
            filteredLists.addAll(filterSC8(dataObject));

            filteredLists = sortDateTab(filteredLists);

            tv.put("DateTabPoint", filteredLists);
        }catch (Exception e){
            tv.put("DateTabRedemption", null);
            log.info("filterDateTabPoint : "+ e.getMessage());
        }
    }

//    SC1,2,3,5,7,11
    private void filterDateTabRedemption (Map<String, Object> tv, String brand){
        List<HistoryPointRsp> dataObject = (List<HistoryPointRsp>) tv.get("HistoryPointResponses");
        List<HistoryPointRsp> filteredLists = new ArrayList<HistoryPointRsp>();
        try{
            filteredLists.addAll(filterSC1(dataObject));
            filteredLists.addAll(filterSC2(dataObject));
            filteredLists.addAll(filterSC3(dataObject));
            filteredLists.addAll(filterSC5(dataObject));
            filteredLists.addAll(filterSC7(dataObject));

            if (apigwUtill.isDtac(brand)) {
                filteredLists.addAll(filterSC11(dataObject));
                List<HistoryPointRsp> dataHistory = (List<HistoryPointRsp>) tv.get("redemptionHistory");
                if(dataHistory != null && !dataHistory.isEmpty()){
                    filteredLists.addAll(dataHistory);
                }
            }

            filteredLists = sortDateTab(filteredLists);
            tv.put("DateTabRedemption", filteredLists);
        }catch (Exception e){
            tv.put("DateTabRedemption", null);
            log.info("error in filterDateTabRedemption : "+ e.getMessage());
        }

    }

    private List<HistoryPointRsp> sortDateTab  (List<HistoryPointRsp> dataObject){
        Collections.sort(dataObject,HistoryPointRsp.StrDataComparator);
        return dataObject;
    }

//    SC 1 : Campaign = Privilege & จำนวน point = 0  แสดงเฉพาะฝั่งขวา Redemption
    private List<HistoryPointRsp> filterSC1 (List<HistoryPointRsp> dataObject){

        return Optional.ofNullable(dataObject)
                .orElse(Collections.emptyList())
                .stream()
                .filter(date -> Constant.PRVILEAGE.equals(date.getType())
                        && "0".equals(date.getPoints()))
                .collect(Collectors.toList());

    }

//    SC 2 : Campaign = Privilege & มี Point  แสดงทั้งสองฝั่ง point& Redemption (ไม่สนคูปอง)
    private List<HistoryPointRsp> filterSC2 (List<HistoryPointRsp> dataObject){

        return Optional.ofNullable(dataObject)
                .orElse(Collections.emptyList())
                .stream()
                .filter(date -> Constant.PRVILEAGE.equals(date.getType())
                        && !"0".equals(date.getPoints()))
                .collect(Collectors.toList());

    }

//    SC 3 : Campaign = Point และ  point = 0 แสดงทั้งสองฝั่ง point & Redemption   (ไม่สนคูปอง) Privilege from other partner
    private List<HistoryPointRsp> filterSC3 (List<HistoryPointRsp> dataObject){

        return Optional.ofNullable(dataObject)
                .orElse(Collections.emptyList())
                .stream()
                .filter(date -> Constant.POINT.equals(date.getType())
                        && "0".equals(date.getPoints())
                        && date.getCouponCode() != null)
                .collect(Collectors.toList());

    }

//    SC 4 : Campaign = Point และ point = 0 และไม่มีคูปอง(field couponCode) แสดงเฉพาะฝฝั่งซ้าย point Scene นี้รวมถึงกรณี Privilege from other partner ด้วย
    private List<HistoryPointRsp> filterSC4 (List<HistoryPointRsp> dataObject){

        return Optional.ofNullable(dataObject)
                .orElse(Collections.emptyList())
                .stream()
                .filter(date -> Constant.POINT.equals(Optional.ofNullable(date.getType()).orElse(""))
                        && "0".equals(Optional.ofNullable(date.getPoints()).orElse(""))
                        && date.getCouponCode() == null)
                .collect(Collectors.toList());

    }

//    SC 5 : Campaign = Point และ point > 0  และมีคูปอง(field couponCode) จะแสดงทั้งสองฝั่ง point& Redemption (กรณี Earn +)
    private List<HistoryPointRsp> filterSC5 (List<HistoryPointRsp> dataObject){

        return Optional.ofNullable(dataObject)
                .orElse(Collections.emptyList())
                .stream()
                .filter(date -> Constant.POINT.equals(Optional.ofNullable(date.getType()).orElse("")) &&
                        isNegativeInteger(date.getPoints()) &&
                        date.getCouponCode() != null)
                .collect(Collectors.toList());

    }

//    SC 6 : Campaign = Point และ point > 0  และไม่มีคูปอง(field couponCode) จะแสดงแค่ฝั่ง point  (กรณี Earn +)
    private List<HistoryPointRsp> filterSC6 (List<HistoryPointRsp> dataObject){

        return dataObject.stream()
                .filter(date -> Constant.POINT.equals(date.getType())
                        && date.getPoints() != null
                        && isNegativeInteger(date.getPoints())
                        && date.getCouponCode() == null)
                .collect(Collectors.toList());
    }

//    SC 7 : Campaign = Point และ point < 0  และมีคูปอง(field couponCode) จะแสดงทั้งสองฝั่ง point & Redemption (กรณี Burn -)
    private List<HistoryPointRsp> filterSC7 (List<HistoryPointRsp> dataObject){

        return Optional.ofNullable(dataObject)
                .orElse(Collections.emptyList())
                .stream()
                .filter(date -> Constant.POINT.equals(Optional.ofNullable(date.getType()).orElse("")) &&
                        isNegativeInteger(date.getPoints()) &&
                        date.getCouponCode() == null)
                .collect(Collectors.toList());

    }

//    SC 8 : Campaign = Point และ point < 0  และไม่มีคูปอง(field couponCode) จะแสดงแค่ฝั่ง point  (กรณี Burn - )
    private List<HistoryPointRsp> filterSC8 (List<HistoryPointRsp> dataObject){

        return dataObject.stream()
                .filter(date -> Constant.POINT.equals(date.getType())
                        && isNegativeInteger(date.getPoints())
                        && date.getCouponCode() == null)
                .collect(Collectors.toList());

    }

    //    SC 11 : กรณีของ Dtac ใน Campaign  เฉพาะ CampaignType ประเภท Package (Uesr)  ถ้าไม่มีคูปอง  ก็ให้ขึ้นฝั่ง Redemption ด้วย
    private List<HistoryPointRsp> filterSC11 (List<HistoryPointRsp> dataObject){

        return dataObject.stream()
                .filter(date -> Constant.USED.equals(date.getCampaignType()) && date.getCampaignType() != null)
                .collect(Collectors.toList());
    }

    private Map<String, Object> mapCampaignDetailQueryParam(Map<String, Object> tv, String cmpgId) throws Exception {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("txid", apigwUtill.generateRewardBackendId());
        queryParams.put("id", cmpgId);
        return queryParams;
    }

    private EndpointResultRWD fetchRedeemCampaignDetail(Map<String, Object> tv, Map<String, Object> queryParams, ContextSignature contextSignature) throws Exception {
        redeemDtacScanCodeEndpoint.getDtacCampaignDetailApi(tv,queryParams,contextSignature);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

    private List<Condition> conditionInfo(String language , CouponDetail couponDetail , String accountId){

        List<Condition> conditionList = new ArrayList<Condition>();

        if ((couponDetail.getCouponApp_th() == null || "null".equals(couponDetail.getCouponApp_th())) &&
                (couponDetail.getCouponApp_en() == null || "null".equals(couponDetail.getCouponApp_en())) &&
                (couponDetail.getCouponApp_my() == null || "null".equals(couponDetail.getCouponApp_my())) &&
                (couponDetail.getCouponApp_km() == null || "null".equals(couponDetail.getCouponApp_km())) &&
                (couponDetail.getCouponApp_url() == null || "null".equals(couponDetail.getCouponApp_url())) &&
                (couponDetail.getCouponWeb_th() == null || "null".equals(couponDetail.getCouponWeb_th())) &&
                (couponDetail.getCouponWeb_en() == null || "null".equals(couponDetail.getCouponWeb_en())) &&
                (couponDetail.getCouponWeb_my() == null || "null".equals(couponDetail.getCouponWeb_my())) &&
                (couponDetail.getCouponWeb_km() == null || "null".equals(couponDetail.getCouponWeb_km())) &&
                (couponDetail.getCouponWeb_url() == null || "null".equals(couponDetail.getCouponWeb_url()))) {

            Condition condition = new Condition();
            condition.setType(Constant.TEXT);
            condition.setMessage(accountId);
            conditionList.add(condition);
        }

        if(!(couponDetail.getCouponApp_url() == null || "null".equals(couponDetail.getCouponApp_url()))){
            Condition condition = new Condition();
            condition.setType(Constant.BUTTON);
            condition.setLinkType(Constant.EXTERNAL_LINK);
            condition.setUrl(couponDetail.getCouponApp_url().replace("\"", ""));

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

        if(!(couponDetail.getCouponWeb_url() == null || "null".equals(couponDetail.getCouponWeb_url()))){
            Condition condition = new Condition();
            String message = customMappingMessageService.getMappingMessage(Constant.MESSAGE_CONDITION_LINK, Constant.MESSAGE, language, Constant.MESSAGE);
            condition.setMessage(message);
            condition.setType(Constant.LINK);
            condition.setLinkType(Constant.EXTERNAL_LINK);
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

    private boolean isNegativeInteger(String value) {
        try {
            return Integer.parseInt(value) < 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}