package th.co.truecorp.commonapi.reward.service;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSystemConfig;
import th.co.truecorp.commonapi.reward.cms.jpa.service.*;
import th.co.truecorp.commonapi.reward.common.dto.PageDTO;
import th.co.truecorp.commonapi.reward.common.endpoint.CommonServiceEndpoint;
import th.co.truecorp.commonapi.reward.common.endpoint.CommonServiceProfileAndRewardEndpoint;
import th.co.truecorp.commonapi.reward.common.endpoint.CommonServiceProfileEndpoint;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.dto.*;
import th.co.truecorp.commonapi.reward.endpoint.*;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.model.EndpointResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShelfTemplateDetailListService {

    private static Logger log = LoggerFactory.getLogger(ShelfTemplateDetailListService.class);

    @Autowired
    private ResultService resultService;

    @Autowired
    private APIGWUtill apigwUtill;

    @Autowired
    private RewardUtill rewardUtill;

    @Autowired
    private RwdSectionDetailService rwdSectionDetailService;

    @Autowired
    private ShelfContentEndpoint shelfContentEndpoint;

    @Autowired
    private ShelfTrueInquiryShelfDetailEndpoint shelfTrueInquiryShelfDetailEndpoint;

    @Autowired
    private ErrorService errorService;


    @Autowired
    private RwdSystemConfigService rwdSystemConfigService;

    @Autowired
    private ShelfSectionDetailDtacInquiryShelfDetailEndpoint shelfSectionDetailDtacInquiryShelfDetailEndpoint;

    @Autowired
    private ShelfContentCategoryEndpoint shelfContentCategoryEndpoint;

    @Autowired
    private CommonServiceProfileEndpoint commonServiceProfileEndpoint;

    @Autowired
    private CommonServiceEndpoint commonServiceEndpoint;

    @Autowired
    private CommonServiceProfileAndRewardEndpoint commonServiceProfileAndRewardEndpoint;

    Gson gson = new Gson();

    public EndpointResult getTemplateDetailList(Map<String, Object> tv , String brand) throws Exception {

        EndpointResultRWD endpointResultRwd = new EndpointResultRWD();
        EndpointResult endpointResult = null;

        int paging = 1;
        int limit = 10;

        Object pageObj = tv.get("page");
        if (pageObj != null && !pageObj.toString().isEmpty()) {
            paging = Integer.parseInt(pageObj.toString());
        }

        Object pageSizeObj = tv.get("pageSize");
        if (pageSizeObj != null && !pageSizeObj.toString().isEmpty()) {
            limit = Integer.parseInt(pageSizeObj.toString());
        }

        int start_record = ((paging - 1) * limit) + 1;
        int end_record = (paging * limit);
        tv.put(ComnConst.KEY_LANGUAGE, rewardUtill.handleLanguage(tv.get(ComnConst.KEY_LANGUAGE).toString()));
        String lang = tv.get(ComnConst.KEY_LANGUAGE)!= null?tv.get(ComnConst.KEY_LANGUAGE).toString():"";
        String sectionId = tv.get("sectionId") != null ? tv.get("sectionId").toString() : "";
        String templateCode = tv.get("templateCode") != null ? tv.get("templateCode").toString() : "";
        String shelfId = tv.get("shelfId") != null ? tv.get("shelfId").toString() : "";
        String productType = tv.get("productType") != null ? tv.get("productType").toString():"";

        try {

            List<ShelfTemplateDetailRsp.DealList> dealLists = new ArrayList<ShelfTemplateDetailRsp.DealList>();

            List<ShelfTemplateDetailDto> shelfTemplateDetailDtos = null;

            if(!shelfId.equals("")){
                log.info("Fetching ShelfTemplateDetailDto by sectionId and shelfId.");
                shelfTemplateDetailDtos = rwdSectionDetailService.findShelfTemplateDetailDtoBySectionIdAndItemMapping(sectionId , shelfId);
            }else{
                log.info("Fetching ShelfTemplateDetailDto by sectionId.");
                shelfTemplateDetailDtos = rwdSectionDetailService.findShelfTemplateDetailDtoBySectionId(sectionId);
            }
            log.info("rwdSectionDetail size :: "+ shelfTemplateDetailDtos.size());
            Integer seqNo = 0;

            if(shelfTemplateDetailDtos != null && !shelfTemplateDetailDtos.isEmpty()){
                for(ShelfTemplateDetailDto dto:shelfTemplateDetailDtos){
                    String itemTypeCode = dto.getitem_type_code();
                    String itemMapping = dto.getitem_mapping();
                    if("MERCHANT".equals(itemTypeCode)){
                        log.info("Processing MERCHANT type.");
                        endpointResult = errorService.revertMapResult(fetchShelfContent(tv, dto.getitem_mapping(), lang.toLowerCase(), end_record));
                        List<ShelfContentDataApiRsp.ContentData.DataDetails> privilegeLists = (List<ShelfContentDataApiRsp.ContentData.DataDetails>) tv.get("GetContent");
                        dealLists = mapContentToShelfTemplateDetailListRsp(tv, privilegeLists);

                    }else if("SHELF".equals(itemTypeCode)){
                        log.info("Processing SHELF type.");
                        String shelfTypeCode = dto.getshelf_type_code();
                        if("NORMAL_TRUE".equals(shelfTypeCode)){
                            log.info("SHELF Type: NORMAL_TRUE");
                            endpointResult = errorService.revertMapResult(fetchTrueShelfInquiryShelfDetail(tv, dto.getitem_mapping(), lang, brand, end_record));

                            List<ShelfContentDataApiRsp.ContentData.DataDetails> shelfItemsList = (List<ShelfContentDataApiRsp.ContentData.DataDetails>) tv.get("GetShelfInquiryShelfDetail");
                            dealLists = mapInquiryShelfToShelfTemplateDetailListRsp(tv, shelfItemsList);
                        }else if("CATEGORY_TRUE".equals(shelfTypeCode)){
                            log.info("SHELF Type: CATEGORY_TRUE");
                            Optional<RwdSystemConfig> optional = rwdSystemConfigService.findConfigs("CONTENT_TYPE",shelfTypeCode);
                            log.info("CONTENT_TYPE : "+shelfTypeCode+" :RwdSystemConfig :: "+ optional);
                            if(!optional.isEmpty()){
                                RwdSystemConfig rwdSystemConfig = optional.get();
                                Map<String, Object> queryParams = mapQuerySpecialShelfContentCategory(tv, lang, paging, limit, rwdSystemConfig.getValue(), itemMapping.replace(" ",""));
                                endpointResult = errorService.revertMapResult(fetchShelfContentCategory(tv , queryParams));
                                ContentCategoryResponse contentCategory = (ContentCategoryResponse) tv.get(Constant.ENDPOINT_SERVICE_CONTENT_CATEGORY);
                                if(contentCategory != null){
                                    List<ShelfTemplateDetailRsp.DealList> dealList = mapContentCategoryToShelfTemplateDetailListRsp(tv , seqNo, contentCategory);
                                    if(dealList != null && !dealList.isEmpty()){
                                        seqNo = dealList.get(dealList.size()-1).getSeqNo();
                                        dealLists.addAll(dealList);
                                    }
                                }
                            }

                        }else if("CATEGORY_DTAC".equals(shelfTypeCode)){
                            log.info("SHELF Type: CATEGORY_DTAC");
                            String customerGrade = null;
                            String customerNumber = null;

//                            EndpointResult result2 = commonServiceProfileEndpoint.getCommonService(tv);
//                            EndpointResult resultProfile = commonServiceEndpoint.getCommonService(tv);
//                            CustomerProfileRsp commonProfileRsp = (CustomerProfileRsp) tv.get("commonProfileRspEndpoint");
//                            ServiceProfileRsp serviceProfileRsp = (ServiceProfileRsp) tv.get("serviceProfileRspEndpoint");

                            endpointResult = commonServiceProfileAndRewardEndpoint.getCommonServiceProfileAndReward(tv);
                            ProfileAndRewardRsp profileAndRewardRsp = (ProfileAndRewardRsp) tv.get("profileAndRewardRspEndpoint");

                            if (profileAndRewardRsp!=null && profileAndRewardRsp.getCustomerProfileRsp() != null && profileAndRewardRsp.getServiceProfileRsp() != null) {
                                log.info("commonProfileRsp data exist");
                                customerGrade = profileAndRewardRsp.getCustomerProfileRsp().getCardType();
                                log.info("customerGrade: " + customerGrade);
                                customerNumber = profileAndRewardRsp.getServiceProfileRsp().getCustomerNumber();
                                log.info("customerNumber: " + customerNumber);

                                Map<String, Object> queryParams = mapQueryDtacSpecialShelfInquiryShelfDetail(tv, lang, brand, productType, customerNumber, customerGrade);
                                endpointResult = errorService.revertMapResult(fetchDtacShelfInquiryShelfDetail(tv, queryParams));

                                List<RwdSystemConfig> rwdSystemConfigs = rwdSystemConfigService.findConfigGroupTemplateDefault(itemMapping);
                                log.info("itemMapping RwdSystemConfig :: "+ rwdSystemConfigs);
                                if(!rwdSystemConfigs.isEmpty()){
                                    for(RwdSystemConfig rwdSystemConfig : rwdSystemConfigs){
                                        String valueConfig = rwdSystemConfig.getValue();
                                        if(valueConfig != null && !valueConfig.equals("")){
                                            String[] valueConfigParts = valueConfig.split(",");
                                            String catg = "";
                                            String subCatg = "";
                                            String type = "";
                                            if(valueConfigParts.length == 2){
                                                if(!valueConfigParts[0].equals("-") && !valueConfigParts[1].equals("-")){
                                                    catg = valueConfigParts[0];
                                                    subCatg = valueConfigParts[1];
                                                    String[] valueParts = subCatg.split("_");
                                                    if(valueParts.length == 2){
                                                        subCatg = valueParts[0];
                                                        type = valueParts[1].equals("C") ? "2" : "";
                                                    }
                                                }else if(!valueConfigParts[0].equals("-") && valueConfigParts[1].equals("-")){
                                                    catg = valueConfigParts[0];
                                                }else if(valueConfigParts[0].equals("-") && !valueConfigParts[1].equals("-")){
                                                    subCatg = valueConfigParts[1];
                                                    String[] valueParts = subCatg.split("_");
                                                    if(valueParts.length == 2){
                                                        subCatg = valueParts[0];
                                                        type = valueParts[1].equals("C") ? "2" : "";//C = type 2
                                                    }
                                                }
                                            }

                                            ShelfSectionDetailDtacInquiryShelfDetailRsp detailRsp = (ShelfSectionDetailDtacInquiryShelfDetailRsp) tv.get("GetShelfDtacInquiryShelfDetail");
                                            if(detailRsp != null){
                                                List<ShelfTemplateDetailRsp.DealList> dealList = mapInquiryToShelfTemplateDetailListRsp(tv, seqNo, detailRsp, type, catg, subCatg);
                                                if(dealList != null && !dealList.isEmpty()){
                                                    seqNo = dealList.get(dealList.size()-1).getSeqNo();
                                                    dealLists.addAll(dealList);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }else{
                log.debug(Constant.ENDPOINT_SERVICE_GET_SHELF + " is failed");
                endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
                        tv.get("brand")!=null?tv.get("brand").toString():"",
                        Objects.requireNonNull(Constant.ERROR_DATA_NOT_FOUND),
                        tv.get(ComnConst.KEY_LANGUAGE)!=null?tv.get(ComnConst.KEY_LANGUAGE).toString():"",
                        "data not found",
                        Constant.N_A,
                        Constant.MESSAGE);
                endpointResult = errorService.revertMapResult(endpointResultRwd);
            }

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = formatter.format(date);
            ShelfTemplateDetailRsp templateDetailRsp = new ShelfTemplateDetailRsp();
            templateDetailRsp.setSectionId(sectionId);
            templateDetailRsp.setLang(lang);
            templateDetailRsp.setTemplateCode(templateCode);
            templateDetailRsp.setShelfId(shelfId);
            templateDetailRsp.setExpireDate(strDate);

            if (Optional.ofNullable(endpointResult)
                    .map(result -> result.getHttpStatus() == 200)
                    .orElse(false)) {
                log.info("Map paging");
                processPagingTemplateDetailList(tv, dealLists, paging, limit);
                dealLists = (List<ShelfTemplateDetailRsp.DealList>) tv.get("processPagingTemplateDetailList");

                templateDetailRsp.setSectionDetailItem(dealLists);


                if (dealLists == null || dealLists.isEmpty()) {
                    log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + " is failed");
//                    endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
//                            tv.get("brand").toString(),
//                            Objects.requireNonNull(Constant.ERROR_DATA_NOT_FOUND),
//                            tv.get(ComnConst.KEY_LANGUAGE).toString(),
//                            "data not found",
//                            Constant.N_A,
//                            Constant.MESSAGE);
//                    endpointResult = errorService.revertMapResult(endpointResultRwd);
                    endpointResultRwd = errorService.convertMapResult(resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC, ComnConst.STTS_CODE_SUCC));
                    endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC, ComnConst.STTS_CODE_SUCC);
                } else if (!dealLists.isEmpty()) {
                    log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + " is success");

                    endpointResultRwd = errorService.convertMapResult(resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA));
                    endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                }
            }


            tv.put(Constant.TRANSACTION_RESPONSE_KEY,templateDetailRsp);

            log.info("endpointResultRwd : "+endpointResultRwd);
        } catch (Exception e) {
            log.info("error is " + e.getMessage());
            endpointResult = errorService.mapErrorException(e,tv);
            return endpointResult;
        }

        return endpointResult;
    }

    private void processPagingTemplateDetailList(Map<String, Object> tv, List<ShelfTemplateDetailRsp.DealList> dataDetails, int paging, int limit) {
        PagedResult<ShelfTemplateDetailRsp.DealList> pagedTemplateDetailListRsp = apigwUtill.paginate(dataDetails, paging, limit);

        PageDTO pageReq = new PageDTO();
        pageReq.setPageNumber(paging);
        pageReq.setPageSize(limit);
        pageReq.setCount(pagedTemplateDetailListRsp.getTotalCount());
        pageReq.setTotalPage(pagedTemplateDetailListRsp.getTotalPages());

        tv.put("paging", pageReq);
        tv.put("processPagingTemplateDetailList", pagedTemplateDetailListRsp.getItems());
    }

    private List<ShelfTemplateDetailRsp.DealList> mapContentToShelfTemplateDetailListRsp (Map<String, Object> tv, List<ShelfContentDataApiRsp.ContentData.DataDetails> shelfItemsLists) throws ParseException {
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
        List<ShelfTemplateDetailRsp.DealList> itemLists = new ArrayList<ShelfTemplateDetailRsp.DealList>();
        Integer seqNo = 0;
        if(shelfItemsLists != null){
            for(ShelfContentDataApiRsp.ContentData.DataDetails shelfItemsList:shelfItemsLists){
                seqNo++;
                ShelfTemplateDetailRsp.DealList itemList = new ShelfTemplateDetailRsp.DealList();
                ShelfTemplateDetailRsp.DealList.ThumbnailList thumbnailList = new ShelfTemplateDetailRsp.DealList.ThumbnailList();

                thumbnailList.setThumbnail16x9(shelfItemsList.getThumb_list() != null && shelfItemsList.getThumb_list().getHighlight16x9().isEmpty() ? shelfItemsList.getThumb_list().getHighlight16x9():null);


                String contentType = null;
                if(shelfItemsList.getContent_type() != null){
                    if(("trueyoumerchant").equals(shelfItemsList.getContent_type())){
                        contentType = "MERCHANT";
                    }else if(("trueyouarticle").equals(shelfItemsList.getContent_type())){
                        if(shelfItemsList.getSetting() != null
                                && shelfItemsList.getSetting().getThematic_main_shelf_ids() != null
                                && !shelfItemsList.getSetting().getThematic_main_shelf_ids().isEmpty()){
                            contentType = "THEMATIC";
                        }else{
                            contentType = "ARTICLE";
                        }
                    }else if(shelfItemsList.getContent_type().equals("privilege")){
                        contentType = "DEAL";
                    }
                }

                itemList.setSeqNo(seqNo);
                itemList.setCampaignId(shelfItemsList.getId());
                itemList.setCampaignCode(shelfItemsList.getCampaign_code());
                itemList.setContentType(contentType);
                itemList.setTimeCounterFlag(shelfItemsList.getInfo() != null
                        && shelfItemsList.getInfo().getTime_counter_show() !=null
                        ?shelfItemsList.getInfo().getTime_counter_show() : null);
                itemList.setThumbnailList(thumbnailList);
                itemList.setCampaignName(
                        (shelfItemsList.getInfo() != null && lang != null)
                                ? (lang.equalsIgnoreCase(Constant.TH)
                                ? shelfItemsList.getInfo().getMerchant_name_th()
                                : shelfItemsList.getInfo().getMerchant_name_en())
                                : Constant.DEFAULT_NULL_EXCEPTION_VALUE
                );
                itemList.setCampaignDescription(shelfItemsList.getTitle());
                itemList.setCampaignExpireDate(shelfItemsList.getExpire_date());
                itemList.setCampaignType(shelfItemsList.getCampaign_type());
                itemList.setCardType(shelfItemsList.getCard_type());
                itemList.setRegularPoint(shelfItemsList.getRedeem_point());
                itemList.setOfferPoint(shelfItemsList.getRedeem_point());

                itemLists.add(itemList);
            }
        }
        return itemLists;
    }

    private List<ShelfTemplateDetailRsp.DealList> mapInquiryShelfToShelfTemplateDetailListRsp (Map<String, Object> tv, List<ShelfContentDataApiRsp.ContentData.DataDetails> privilegeLists) throws ParseException {
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
        List<ShelfTemplateDetailRsp.DealList> itemLists = new ArrayList<ShelfTemplateDetailRsp.DealList>();
        Integer seqNo = 0;
        if(privilegeLists != null){
            for(ShelfContentDataApiRsp.ContentData.DataDetails privilegeList:privilegeLists){
                seqNo++;
                ShelfTemplateDetailRsp.DealList itemList = new ShelfTemplateDetailRsp.DealList();
                ShelfTemplateDetailRsp.DealList.ThumbnailList thumbnailList = new ShelfTemplateDetailRsp.DealList.ThumbnailList();

                thumbnailList.setThumbnail16x9(privilegeList.getThumb_list() != null && privilegeList.getThumb_list().getHighlight16x9().isEmpty() ? privilegeList.getThumb_list().getHighlight16x9():null);

                String contentType = null;
                if(privilegeList.getContent_type() != null){
                    if(("trueyoumerchant").equals(privilegeList.getContent_type())){
                        contentType = "MERCHANT";
                    }else if(("trueyouarticle").equals(privilegeList.getContent_type())){
                        if(privilegeList.getSetting() != null
                                && privilegeList.getSetting().getThematic_main_shelf_ids() != null
                                && !privilegeList.getSetting().getThematic_main_shelf_ids().isEmpty()){
                            contentType = "THEMATIC";
                        }else{
                            contentType = "ARTICLE";
                        }
                    }else if(("privilege").equals(privilegeList.getContent_type())){
                        contentType = "DEAL";
                    }
                }

                itemList.setSeqNo(seqNo);
                itemList.setCampaignId(privilegeList.getId());
                itemList.setCampaignCode(privilegeList.getCampaign_code());
                itemList.setContentType(contentType);
                itemList.setTimeCounterFlag(privilegeList.getInfo() != null
                        && privilegeList.getInfo().getTime_counter_show() !=null
                        ? privilegeList.getInfo().getTime_counter_show() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                itemList.setThumbnailList(thumbnailList);
                itemList.setCampaignName(privilegeList.getInfo() != null && lang!=null
                        ? (lang.equalsIgnoreCase(Constant.TH)
                        ? privilegeList.getInfo().getMerchant_name_th()
                        : privilegeList.getInfo().getMerchant_name_en())
                        : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                itemList.setCampaignDescription(privilegeList.getTitle());
                itemList.setCampaignExpireDate(privilegeList.getExpire_date());
                itemList.setCampaignType(privilegeList.getCampaign_type());
                itemList.setCardType(privilegeList.getCard_type());
                itemList.setRegularPoint(privilegeList.getRedeem_point());
                itemList.setOfferPoint(privilegeList.getRedeem_point());

                itemLists.add(itemList);
            }
        }
        return itemLists;
    }

    private List<ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionPattern> filterTypeAndCategoryAndSubCategory (List<ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionPattern> promotionPattern, String type, String catg, String subCatg){
        log.info("Type And Category And SubCategory : type = "+type+", catg = "+catg+", subCatg = "+subCatg);

        if(!type.equals("") && !catg.equals("") && !subCatg.equals("")){
            log.info("filter : type = "+type+", catg = "+catg+", subCatg = "+subCatg);
            return promotionPattern.stream()
                    .filter(date -> date.getType().equals(type) && date.getCategory().equals(catg) && date.getSubCategory().equals(subCatg))
                    .collect(Collectors.toList());
        }else if(type.equals("") && !catg.equals("") && subCatg.equals("")){
            log.info("filter : catg = "+catg);
            return promotionPattern.stream()
                    .filter(date -> date.getCategory().equals(catg))
                    .collect(Collectors.toList());
        }else if(!type.equals("") && !catg.equals("") && subCatg.equals("")){
            log.info("filter : type = "+type+", catg = "+catg);
            return promotionPattern.stream()
                    .filter(date -> date.getType().equals(type) && date.getCategory().equals(catg))
                    .collect(Collectors.toList());
        }else if(type.equals("") && catg.equals("") && !subCatg.equals("")){
            log.info("filter : subCatg = "+subCatg);
            return promotionPattern.stream()
                    .filter(date -> date.getSubCategory().equals(subCatg))
                    .collect(Collectors.toList());
        }else if(!type.equals("") && catg.equals("") && !subCatg.equals("")){
            log.info("filter : type = "+type+", subCatg = "+subCatg);
            return promotionPattern.stream()
                    .filter(date -> date.getType().equals(type) && date.getSubCategory().equals(subCatg))
                    .collect(Collectors.toList());
        }else if(!type.equals("") && catg.equals("") && subCatg.equals("")){
            log.info("filter : type = "+type);
            return promotionPattern.stream()
                    .filter(date -> date.getType().equals(type))
                    .collect(Collectors.toList());
        }

        return promotionPattern;
    }

    private List<ShelfTemplateDetailRsp.DealList> mapContentCategoryToShelfTemplateDetailListRsp (Map<String, Object> tv, Integer seqNo, ContentCategoryResponse contentCategory) throws ParseException {
        log.info("map ContentCategory To ShelfTemplateDetailListRsp");
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
        List<ShelfTemplateDetailRsp.DealList> itemLists = new ArrayList<ShelfTemplateDetailRsp.DealList>();

        if(contentCategory != null &&contentCategory.getContent()!=null){
            for(ContentCategoryResponse.Content content:contentCategory.getContent()){
                seqNo++;
                ShelfTemplateDetailRsp.DealList itemList = new ShelfTemplateDetailRsp.DealList();
                ShelfTemplateDetailRsp.DealList.ThumbnailList thumbnailList = new ShelfTemplateDetailRsp.DealList.ThumbnailList();
                thumbnailList.setThumbnail16x9(content.getThumbList().getHighlight().getHighlight16x9() != null ? content.getThumbList().getHighlight().getHighlight16x9() : null);

                String contentType = null;
                if(content.getType() != null){
                    if(content.getType().equals("trueyoumerchant")){
                        contentType = "MERCHANT";
                    }else if(content.getType().equals("trueyouarticle")
                            && content.getContentSpecification() !=null
                            && content.getContentSpecification().getCharacteristic() !=null){
                        for(ContentCategoryResponse.Characteristic characteristic : content.getContentSpecification().getCharacteristic()){
                            if(characteristic.getName() != null && characteristic.getName().equals("thematic_main_shelf_ids")){
                                if(characteristic.getValue() != null && !characteristic.getValue().equals("")){
                                    contentType = "THEMATIC";
                                }else{
                                    contentType = "ARTICLE";
                                }
                            }
                        }
                    }else if(content.getType().equals("privilege")){
                        contentType = "DEAL";
                    }
                }

                itemList.setSeqNo(seqNo);
                itemList.setCampaignId(content.getId());

                itemList.setContentType(contentType);
                itemList.setTimeCounterFlag(content.getAddtionalInfo().getTimeCounterShow());
                itemList.setThumbnailList(thumbnailList);
                itemList.setCampaignName(lang.toUpperCase().equals(Constant.TH)
                        && content.getAddtionalInfo()!=null && content.getAddtionalInfo().getMerchant() != null
                        ? content.getAddtionalInfo().getMerchant().getNameTh()
                        : content.getAddtionalInfo().getMerchant().getNameEn());
                itemList.setCampaignDescription(content.getTitle());
                itemList.setCampaignExpireDate(apigwUtill.convertToBKKTimeReturnFormat(
                        content.getValidFor()!=null?content.getValidFor().getEndDate():null
                        , "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
                        , "yyyy-MM-dd HH:mm:ss") + "+07:00");
                itemList.setCampaignCode(content.getCampaign() !=null
                        ?content.getCampaign().getId()
                        :null);
                itemList.setCampaignType(content.getCampaign() !=null
                        ?content.getCampaign().getType()
                        :null);
                itemList.setCardType(content.getCardType() !=null
                        ?content.getCardType()
                        :null);
                itemList.setRegularPoint(content.getRedeemPoint());
                itemList.setOfferPoint(null);

                itemLists.add(itemList);
            }
        }
        return itemLists;
    }

    private List<ShelfTemplateDetailRsp.DealList> mapInquiryToShelfTemplateDetailListRsp(Map<String, Object> tv, Integer seqNo, ShelfSectionDetailDtacInquiryShelfDetailRsp detailRsp, String type, String catg, String subCatg){
        log.info("map Inquiry To ShelfTemplateDetailListRsp");
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
        List<ShelfTemplateDetailRsp.DealList> itemLists = null;

        if(detailRsp != null){
            List<ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionPattern> promotionPatterns = filterTypeAndCategoryAndSubCategory(detailRsp.getPromotionPattern(), type, catg, subCatg);
            log.info("promotionPatterns size : "+ promotionPatterns.size());
            if(promotionPatterns!=null && promotionPatterns.size() > 0) {
                log.info("promotionPatterns size > 0");
                itemLists = new ArrayList<ShelfTemplateDetailRsp.DealList>();
                for (ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionPattern promotionPattern : promotionPatterns) {
                    ShelfTemplateDetailRsp.DealList dealList = new ShelfTemplateDetailRsp.DealList();

                    seqNo++;

                    String contentType = mapContentTypeCATEGORY_DTAC(promotionPattern);

                    Map<String, String> typeMap = Map.of(
                            "1", Constant.DTAC_REWARD,
                            "2", Constant.COIN
                    );
                    String campaignType = typeMap.getOrDefault(promotionPattern.getType(), null);

                    List<String> cardType = null;
                    String relationTypeInGroup = promotionPattern.getPromotionCriteriaGroup() !=null
                            && !promotionPattern.getPromotionCriteriaGroup().isEmpty()
                            ?  promotionPattern.getPromotionCriteriaGroup().get(0).getRelationTypeInGroup() : "";
                    switch (relationTypeInGroup) {
                        case "Welcome":
                            cardType = Arrays.asList("welcome", "silver", "gold", "platinum_blue");
                            break;
                        case "Silver":
                            cardType = Arrays.asList("silver", "gold", "platinum_blue");
                            break;
                        case "Gold":
                            cardType = Arrays.asList("gold", "platinum_blue");
                            break;
                        case "Platinum Blue":
                            cardType = Arrays.asList("platinum_blue");
                            break;
                        case "On Going":
                            cardType = Arrays.asList("no card");
                            break;
                    }

                    String regularPoint = null;
                    boolean chkCriteria = promotionPattern.getPromotionCriteriaGroup()!=null
                            && !promotionPattern.getPromotionCriteriaGroup().isEmpty();
                    String offerPoint = chkCriteria?"0": null;
                    if ("On Going".equals(relationTypeInGroup)&&chkCriteria) {
                        // offerPoint = "0";
                        for (ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteria pc : promotionPattern.getPromotionCriteriaGroup().get(0).getPromotionCriteria()) {
                            if ("CoinsAmount".equals(pc.getCriteriaPara())) {
                                regularPoint = pc.getCriteriaValue();
                            }
                        }
                    } else if(chkCriteria) {
                        for (ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionCriteria pc : promotionPattern.getPromotionCriteriaGroup().get(0).getPromotionCriteria()) {
                            if ("OriginalCoinsAmount".equals(pc.getCriteriaPara())) {
                                regularPoint = pc.getCriteriaValue();
                            }
                            if ("CoinsAmount".equals(pc.getCriteriaPara())) {
                                offerPoint = pc.getCriteriaValue();
                            }
                        }
                    }

                    ShelfTemplateDetailRsp.DealList.ThumbnailList thumbnailList = new ShelfTemplateDetailRsp.DealList.ThumbnailList();
                    thumbnailList.setThumbnail3x2(chkCriteria
                            && !promotionPattern.getPromotionCriteriaGroup().get(0).getHref().isEmpty()
                            ? promotionPattern.getPromotionCriteriaGroup().get(0).getHref() : null);


                    dealList.setSeqNo(seqNo);
                    dealList.setCampaignId(promotionPattern.getId() != null
                            && !promotionPattern.getId().isEmpty()
                            ? promotionPattern.getId()
                            : promotionPattern.getBanner().getId());
                    dealList.setCampaignCode(chkCriteria?promotionPattern.getPromotionCriteriaGroup().get(0).getId():null);
                    dealList.setContentType(contentType);
                    dealList.setTimeCounterFlag(null);
                    dealList.setThumbnailList(thumbnailList);
                    dealList.setCampaignName(promotionPattern.getRelatedParty() != null
                            && !promotionPattern.getRelatedParty().isEmpty()
                            ?promotionPattern.getRelatedParty().get(0).getName():null);
                    dealList.setCampaignDescription(promotionPattern.getDescription());
                    dealList.setCampaignExpireDate(promotionPattern.getValidFor()!=null
                            ?promotionPattern.getValidFor().getEndDateTime():null);
                    dealList.setCampaignType(campaignType);
                    dealList.setCardType(cardType);
                    dealList.setRegularPoint(regularPoint);
                    dealList.setOfferPoint(offerPoint);

                    itemLists.add(dealList);
                }
            }
        }

        return itemLists;
    }

    private String mapContentTypeCATEGORY_DTAC(ShelfSectionDetailDtacInquiryShelfDetailRsp.PromotionPattern promotionPattern){
        String contentType = "";

        if (promotionPattern.getId() != null && !promotionPattern.getId().equals("")) {
            log.info("Promotion ID is not null or empty. Setting contentType to DEAL.");
            contentType = Constant.DEAL;
        } else {
            if(promotionPattern.getBanner()!=null){
                log.info("Promotion ID is null or empty. Banner type: " + promotionPattern.getBanner().getType());
                switch (promotionPattern.getBanner().getType()) {
                    case "2":
                        log.info("Banner type is '2'. Setting contentType to MAJOR.");
                        contentType = Constant.MAJOR;
                        break;
                    case "7":
                        log.info("Banner type is '7'. Setting contentType to GROUPING.");
                        contentType = Constant.GROUPING;
                        break;
                    case "8":
                        log.info("Banner type is '8'. Setting contentType to FESTIVE.");
                        contentType = Constant.FESTIVE;
                        break;
                }
            } else {
                log.info(" Banner type: empty " );
            }



        }

        return contentType;
    }

    private EndpointResultRWD fetchShelfContent(Map<String, Object> tv, String itemMapping, String lang, Integer maxRow) throws Exception {
        log.info("fetchShelfContent");
        tv.put("country", "th");
        tv.put("lang", lang);
        tv.put("expand", "privilege_list");
        tv.put("expand_limit", maxRow);
        tv.put("fields", "show_redeem_button,privilege_list,card_type,campaign_type,sub_campaign_type,campaign_code,detail,redeem_point,thumb_list,expire_date,info,allow_app,setting,term_and_condition");
        tv.put("cms_id", itemMapping);
        shelfContentEndpoint.getShelfContentApi(tv);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

    private EndpointResultRWD fetchTrueShelfInquiryShelfDetail(Map<String, Object> tv, String itemMapping, String lang, String brand, Integer maxRow) throws Exception {
        log.info("fetchTrueShelfInquiryShelfDetail");
        tv.put("country", "th");
        tv.put("lang", lang);
        tv.put("limit", maxRow);
        tv.put("fields", "how_redeem_button,thumb_list,card_type,campaign_type,sub_campaign_type,campaign_code,redeem_point,expire_date,detail,term_and_condition,info,allow_app,setting");
        tv.put("cms_id", itemMapping);
        tv.put("channel", apigwUtill.isDtac(brand)? "dtac":"true");
        shelfTrueInquiryShelfDetailEndpoint.getShelfInquiryShelfDetail(tv);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

    private Map<String, Object> mapQuerySpecialShelfContentCategory(Map<String, Object> tv, String lang, int paging, int limit, String type, String itemMapping) throws Exception {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("content.type", type);
        queryParams.put("country", "th");
        queryParams.put("language", Objects.equals(lang, Constant.TH) ? "th" : "en");
        queryParams.put("resource.limit", limit);
        queryParams.put("content.articleCategory",itemMapping);
        queryParams.put("fields", "show_redeem_button,privilege_list,card_type,campaign_type,sub_campaign_type,campaign_code,detail,redeem_point,thumb_list,expire_date,info,allow_app,setting,term_and_condition");
        queryParams.put("expand", "privilege_list");
        queryParams.put("expandlimit", paging);

        log.info("ContentCategory ::: "+gson.toJson(queryParams));
        return queryParams;
    }

    private EndpointResultRWD fetchShelfContentCategory(Map<String, Object> tv, Map<String, Object> queryParams) throws Exception {
        log.info("fetchShelfContentCategory");
        shelfContentCategoryEndpoint.getContentCategoryEndpoint(tv, queryParams);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

    private Map<String, Object> mapQueryDtacSpecialShelfInquiryShelfDetailCategoryAndSubCategory(Map<String, Object> tv, String lang, String brand, String productType, String customerNumber, String custGrade, String listType, String catg, String subCatg) throws Exception {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("lang", Objects.equals(lang, Constant.TH) ? "T" : "E");
        queryParams.put("id", apigwUtill.generateRewardBackendId());
        queryParams.put("phoneNumber", apigwUtill.encryptPhoneNoToApiGw(tv));
        queryParams.put("customerNumber", customerNumber);
        queryParams.put("type", custGrade.replace("_", "%20"));//segment decrypt from FE
        queryParams.put("shelfType", 1);
        queryParams.put("listType", listType.equals("C") ? 2 : 5);
        queryParams.put("relatedParty.type", rewardUtill.getShortProductTypeForDtac(productType));
        queryParams.put("channel", apigwUtill.isDtac(brand)? "dtac":"true");
        queryParams.put("category", catg);
        queryParams.put("subCatg", subCatg);

        log.info("CategoryAndSubCategory ::: "+gson.toJson(queryParams));
        return queryParams;
    }

    private Map<String, Object> mapQueryDtacSpecialShelfInquiryShelfDetailCategory(Map<String, Object> tv, String lang, String brand, String productType, String customerNumber, String custGrade, String listType, String catg) throws Exception {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("lang", Objects.equals(lang, Constant.TH) ? "T" : "E");
        queryParams.put("id", apigwUtill.generateRewardBackendId());
        queryParams.put("phoneNumber", apigwUtill.encryptPhoneNoToApiGw(tv));
        queryParams.put("customerNumber", customerNumber);
        queryParams.put("type", custGrade.replace("_", "%20"));//segment decrypt from FE
        queryParams.put("shelfType", 1);
        queryParams.put("listType", listType.equals("C") ? 2 : 5);
        queryParams.put("relatedParty.type", rewardUtill.getShortProductTypeForDtac(productType));
        queryParams.put("channel", apigwUtill.isDtac(brand)? "dtac":"true");
        queryParams.put("category", catg);

        log.info("Category ::: "+gson.toJson(queryParams));
        return queryParams;
    }

    private Map<String, Object> mapQueryDtacSpecialShelfInquiryShelfDetailSubCategory(Map<String, Object> tv, String lang, String brand, String productType, String customerNumber, String custGrade, String listType, String subCatg) throws Exception {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("lang", Objects.equals(lang, Constant.TH) ? "T" : "E");
        queryParams.put("id", apigwUtill.generateRewardBackendId());
        queryParams.put("phoneNumber", apigwUtill.encryptPhoneNoToApiGw(tv));
        queryParams.put("customerNumber", customerNumber);
        queryParams.put("type", custGrade.replace("_", "%20"));//segment decrypt from FE
        queryParams.put("shelfType", 1);
        queryParams.put("listType", listType.equals("C") ? 2 : 5);
        queryParams.put("relatedParty.type", rewardUtill.getShortProductTypeForDtac(productType));
        queryParams.put("channel", apigwUtill.isDtac(brand)? "dtac":"true");
        queryParams.put("subCatg", subCatg);

        log.info("SubCategory ::: "+gson.toJson(queryParams));
        return queryParams;
    }

    private Map<String, Object> mapQueryDtacSpecialShelfInquiryShelfDetail(Map<String, Object> tv, String lang, String brand, String productType, String customerNumber, String custGrade) throws Exception {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("lang", Objects.equals(lang, Constant.TH) ? "T" : "E");
        queryParams.put("id", apigwUtill.generateRewardBackendId());
        queryParams.put("phoneNumber", apigwUtill.encryptPhoneNoToApiGw(tv));
        queryParams.put("customerNumber", customerNumber);
        queryParams.put("type", custGrade.replace("_", "%20"));//segment decrypt from FE
        queryParams.put("shelfType", 1);
        queryParams.put("listType", 5);
        queryParams.put("relatedParty.type", rewardUtill.getShortProductTypeForDtac(productType));
        queryParams.put("channel", apigwUtill.isDtac(brand)? "dtac":"true");

        log.info("ShelfInquiryShelfDetail ::: "+gson.toJson(queryParams));
        return queryParams;
    }

    private EndpointResultRWD fetchDtacShelfInquiryShelfDetail(Map<String, Object> tv, Map<String, Object> queryParams) throws Exception {
        log.info("fetchDtacShelfInquiryShelfDetail");
        shelfSectionDetailDtacInquiryShelfDetailEndpoint.getShelfDtacInquiryShelfDetail(tv, queryParams);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

}
