package th.co.truecorp.commonapi.reward.service;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSectionDetail;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSystemConfig;
import th.co.truecorp.commonapi.reward.cms.jpa.service.*;
import th.co.truecorp.commonapi.reward.common.endpoint.CommonServiceEndpoint;
import th.co.truecorp.commonapi.reward.common.endpoint.CommonServiceProfileEndpoint;
import th.co.truecorp.commonapi.reward.common.model.GetDigitalByDigitalIdResponse;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.dto.*;
import th.co.truecorp.commonapi.reward.endpoint.*;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonapi.reward.model.mapper.ShelfSectionHeaderMapperModel;
import th.co.truecorp.commonapi.reward.redis.RedisCacheService;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.context.LogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.exception.EndpointServiceException;
import th.co.truecorp.commonlib.log.model.EndpointResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ShelfSectionHeaderService {

    private static Logger log = LoggerFactory.getLogger(ShelfSectionHeaderService.class);

    Gson gson = new Gson();

    @Autowired
    private LogContextService logContextService;

    @Autowired
    private ResultService resultService;

    @Autowired
    private APIGWUtill apigwUtill;

    @Autowired
    private RewardUtill rewardUtill;

    @Autowired
    private RwdSystemConfigService rwdSystemConfigService;

    @Autowired
    private ShelfCmsService shelfCmsService;

    @Autowired
    private ShelfContentEndpoint shelfContentEndpoint;

    @Autowired
    private ShelfTrueInquiryShelfDetailEndpoint shelfTrueInquiryShelfDetailEndpoint;

    @Autowired
    private ShelfDtacInquiryShelfDetailEndpoint shelfDtacInquiryShelfDetailEndpoint;

    @Autowired
    private CommonServiceEndpoint commonServiceEndpoint;

    @Autowired
    private ErrorService errorService;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private CommonServiceProfileEndpoint commonServiceProfileEndpoint;

//    @EndpointLog(name = "TRUEAPP.getSectionHeader")
    public EndpointResult getSectionHeader(Map<String, Object> tv, String lang, String brand,
                                           String layoutId, String sectionId, String displayTypeCode, String useCmsContent) throws Exception {
        final LogContext logContext = logContextService.getCurrentContext();
        EndpointResult endpointResult = null;

        try {
            //1.Get maximun campaign per shelf on priviledge landing page from table RWD_System_Config
            Optional<RwdSystemConfig> rwdSystemConfig = rwdSystemConfigService.findConfigs("MAX_SLIDE", "FIX");
            int maxRow = Integer.valueOf(rwdSystemConfig.get().getValue());
            log.info("maxRow = " + maxRow);
            //2.Get default template code for each Deal or Campaign
            List<RwdSystemConfig> rwdTemplateDefault = rwdSystemConfigService.findConfigTemplateDefault("TEMPLATE_DEFAULT");

            ShelfSectionHeaderRsp sectionHeaderRsp = new ShelfSectionHeaderRsp();
            sectionHeaderRsp = null;
            //4. IF useCmsContent = 'N'

            log.info("usecmscontent = " + useCmsContent);
            if (useCmsContent.equals("N") || useCmsContent.isEmpty()) {
                endpointResult = getSectionHeaderDataFromDatabase(tv, sectionId, lang);
                List<ShelfSectionHeaderMapperModel> shelfSectionHeaderList = (List<ShelfSectionHeaderMapperModel>) tv.get("GetShelfHeaderFromDB");
                sectionHeaderRsp = mapSectionHeaderDataFromDatabase(shelfSectionHeaderList, sectionId, lang, displayTypeCode, maxRow);
            } else {
                //3. IF useCmsContent = 'Y'
                // a. ตรวจสอบจำนวน detail ที่ config ใน RWD_Section_Detail
                Integer totalSectionDetail = shelfCmsService.findTotalSectionDetailCount(sectionId);
                log.info("total_section_detail = " + totalSectionDetail);

                if (totalSectionDetail != 1) {
                    // b. IF total_section_detail <> 1 ให้ข้ามไปทำ ข้อ 4.
                    endpointResult = getSectionHeaderDataFromDatabase(tv, sectionId, lang);
                    List<ShelfSectionHeaderMapperModel> shelfSectionHeaderList = (List<ShelfSectionHeaderMapperModel>) tv.get("GetShelfHeaderFromDB");
                    sectionHeaderRsp = mapSectionHeaderDataFromDatabase(shelfSectionHeaderList, sectionId, lang, displayTypeCode, maxRow);
                } else {
                    log.info("getSectionHeaderFromAPI");
                    // c. IF total_section_detail = 1 ให้ excute sql
                    List<String> itemList = new ArrayList<>();
                    itemList.add("SHELF");
                    itemList.add("MERCHANT");
                    List<RwdSectionDetail> rwdSectionDetails = shelfCmsService.findSectionDetailBySectionIdAndItemTypeCode(sectionId, itemList);
                    log.info("rwdSectionDetails list = " + rwdSectionDetails.size() + " items");
                    rwdSectionDetails.stream().forEach(s -> log.info("sectionId = " + s.getSectionId() + ", type = " + s.getItemTypeCode() + ", itemMapping = " + s.getItemMapping()));
                    //d. IF no data found ในข้อ 3. - c.  ให้ข้ามไปทำ ข้อ 4.
                    if (rwdSectionDetails.size() == 0) {
                        endpointResult = getSectionHeaderDataFromDatabase(tv, sectionId, lang);
                        List<ShelfSectionHeaderMapperModel> shelfSectionHeaderList = (List<ShelfSectionHeaderMapperModel>) tv.get("GetShelfHeaderFromDB");
                        sectionHeaderRsp = mapSectionHeaderDataFromDatabase(shelfSectionHeaderList, sectionId, lang, displayTypeCode, maxRow);
                    } else {
                        //e. IF have data THEN Loop in data ในข้อ 3.-c. -> call API
                        for (RwdSectionDetail d : rwdSectionDetails) {
                            if (d.getItemTypeCode().equals("MERCHANT")) {
                                log.info("MERCHANT");
                                endpointResult = errorService.revertMapResult(fetchShelfContent(tv, d.getItemMapping(), lang, -1));
                                List<ShelfContentDataApiRsp.ContentData.DataDetails> dataDetails = (List<ShelfContentDataApiRsp.ContentData.DataDetails>) tv.get("GetContent");
                                sectionHeaderRsp = mapSectionHeaderDataFromAPI(dataDetails, sectionId, lang, displayTypeCode, rwdTemplateDefault, maxRow);
                            } else if (d.getItemTypeCode().equals("SHELF")) {
                                if (d.getShelfTypeCode() != null && d.getShelfTypeCode().equals("NORMAL_TRUE")) {
                                    log.info("SHELF: NORMAL_TRUE");
                                    endpointResult = errorService.revertMapResult(fetchTrueShelfInquiryShelfDetail(tv, d.getItemMapping(), lang, brand, maxRow));
                                    List<ShelfContentDataApiRsp.ContentData.DataDetails> dataDetails = (List<ShelfContentDataApiRsp.ContentData.DataDetails>) tv.get("GetShelfInquiryShelfDetail");
                                    sectionHeaderRsp = mapSectionHeaderDataFromAPI(dataDetails, sectionId, lang, displayTypeCode, rwdTemplateDefault, maxRow);
                                } else if (d.getShelfTypeCode() != null && d.getShelfTypeCode().equals("BANNER")) {
                                    log.info("SHELF: BANNER");
                                    String productType = (String) tv.get("productType");
                                    String customerGrade = null;
                                    String customerNumber = null;
//                                    customerGrade = apigwUtill.getCustomerGrading(tv);
                                    log.info("call api profile get segment");

                                    EndpointResult result = commonServiceProfileEndpoint.getCommonService(tv);
                                    EndpointResult resultProfile = commonServiceEndpoint.getCommonService(tv);
                                    CustomerProfileRsp commonProfileRsp = (CustomerProfileRsp) tv.get("commonProfileRspEndpoint");
                                    GetDigitalByDigitalIdResponse.Profile serviceProfileRsp = (GetDigitalByDigitalIdResponse.Profile)tv.get("serviceProfileRspEndpoint");

                                    if(commonProfileRsp != null && serviceProfileRsp != null){
                                        log.info("commonProfileRsp data exist");
                                        customerGrade = commonProfileRsp.getCardType();
                                        log.info("customerGrade: " + customerGrade);
                                        customerNumber = serviceProfileRsp.getCustomerNumber();
                                        log.info("customerNumber: " + customerNumber);

                                        if(apigwUtill.isDtac(brand)){
                                            log.info("DTAC");
                                            endpointResult = errorService.revertMapResult(redisShelfDtacInquiryShelfDetail(tv, sectionId, lang, brand, productType, customerNumber, customerGrade));
                                            ShelfDtacInquiryShelfDetailApiRsp dataDetails = (ShelfDtacInquiryShelfDetailApiRsp) tv.get("GetShelfDtacInquiryShelfDetail");
//                                            System.out.println("DTAC dataDetails :" +gson.toJson(dataDetails));
                                            sectionHeaderRsp = mapSectionHeaderDataFromDtacAPI(dataDetails, sectionId, lang, displayTypeCode, rwdTemplateDefault, maxRow, brand);
                                        }else {
                                            log.info("TRUE");
                                            endpointResult = errorService.revertMapResult(fetchTrueShelfInquiryShelfDetail(tv, d.getItemMapping(), lang, brand, maxRow));
//                                            ShelfDtacInquiryShelfDetailApiRsp dataDetails = (ShelfDtacInquiryShelfDetailApiRsp) tv.get("GetShelfDtacInquiryShelfDetail");
//                                            System.out.println("TRUE dataDetails :" +gson.toJson(dataDetails));
//                                            sectionHeaderRsp = mapSectionHeaderDataFromDtacAPI(dataDetails, sectionId, lang, displayTypeCode, rwdTemplateDefault, maxRow, brand);
                                            List<ShelfContentDataApiRsp.ContentData.DataDetails> dataDetails = (List<ShelfContentDataApiRsp.ContentData.DataDetails>) tv.get("GetShelfInquiryShelfDetail");
                                            sectionHeaderRsp = mapSectionHeaderDataFromAPI(dataDetails, sectionId, lang, displayTypeCode, rwdTemplateDefault, maxRow);

                                        }
                                    }
                                }
                            }
                        }

                    }
                }

            }

            tv.put(Constant.ENDPOINT_SERVICE_GET_SECTION_HEADER, sectionHeaderRsp);
            tv.put(Constant.TRANSACTION_RESPONSE_KEY, sectionHeaderRsp);

//            if (sectionHeaderRsp == null && endpointResult.getEndpointResponseCode() == null) {
//                log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + " is failed");
//                endpointResult = resultService.findEndpointResult(tv, ComnConst.ERR_CODE_RESULT_NOT_FOUND);
//            } else if (sectionHeaderRsp != null && endpointResult.getEndpointResponseCode() == null) {
//                log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + " is success");
//                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA);
//            }
            if (sectionHeaderRsp != null && endpointResult.getEndpointResponseCode() == null) {
                log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + " is success");
                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
            }
            tv.put("err", endpointResult);
        } catch (Exception e) {
            log.info("error is " + e.getMessage());
//            endpointResult = resultService.getEndpointExceptionResult(tv, e);
//            return endpointResult;
            return errorService.mapErrorException(e,tv);
        }

        return endpointResult;
    }

    //List<ShelfSectionHeaderMapperModel>
    private EndpointResult getSectionHeaderDataFromDatabase(Map<String, Object> tv, String sectionId, String lang) throws NoSuchFieldException, ClassNotFoundException, InstantiationException, IllegalAccessException, EndpointServiceException {
        log.info("getSectionHeaderDataFromDatabase");
        EndpointResult endpointResult = null;

        try {
            List<ShelfSectionHeaderMapperDto> shelfDetail = shelfCmsService.getSectionHeader(sectionId, lang);
            List<ShelfSectionHeaderMapperModel> shelfDetailMapperList = new ArrayList<>();
//        TODO: mock for uat
//        List<ShelfSectionHeaderMapperModel> shelfDetail = shelfDetailMockForUAT();
//        log.info("using mock data for VENUS Database...");
            if (shelfDetail.size() > 0) {
                log.info("get shelf info from db = " + shelfDetail.size() + " item(s)");
                for (ShelfSectionHeaderMapperDto shelf : shelfDetail) {
                    ShelfSectionHeaderMapperModel shelfModel = new ShelfSectionHeaderMapperModel(
                            shelf.getSection_id(),
                            shelf.getPriority(),
                            shelf.getSeq_no(),
                            shelf.getItem_name(),
                            shelf.getItem_icon(),
                            shelf.getItem_image1x1(),
                            shelf.getItem_image4x3(),
                            shelf.getItem_image16x9(),
                            shelf.getItem_image9x16(),
                            shelf.getItem_type_code(),
                            shelf.getItem_subtype(),
                            shelf.getShelf_type_code(),
                            shelf.getItem_mapping(),
                            shelf.getItem_mapping2(),
                            shelf.getDummy_flag(),
                            shelf.getItem_display_name(),
                            shelf.getItem_display_name_en());

                    shelfDetailMapperList.add(shelfModel);
                }
               endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
            } else {
                shelfDetailMapperList = null;
//                endpointResult = resultService.findEndpointResult(tv, ComnConst.ERR_CODE_RESULT_NOT_FOUND, ComnConst.ERR_CODE_RESULT_NOT_FOUND);
                EndpointResultRWD endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
                        tv.get("brand").toString(),
                        Objects.requireNonNull(Constant.ERROR_DATA_NOT_FOUND),
                        tv.get(ComnConst.KEY_LANGUAGE).toString(),
                        "data not found",
                        Constant.N_A,
                        Constant.MESSAGE);
                endpointResult = errorService.revertMapResult(endpointResultRwd);
            }
            tv.put("GetShelfHeaderFromDB", shelfDetailMapperList);
            return endpointResult;
        } catch (Exception e) {
            throw new EndpointServiceException("", endpointResult);
        }
    }

    private ShelfSectionHeaderRsp mapSectionHeaderDataFromDatabase(List<ShelfSectionHeaderMapperModel> shelfDetail, String sectionId, String lang, String displayTypeCode, Integer maxRow) throws NoSuchFieldException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        log.info("mapSectionHeaderDataFromDatabase");
        ShelfSectionHeaderRsp shelfSectionHeaderRsp = new ShelfSectionHeaderRsp();
        if (shelfDetail != null) {
            if (shelfDetail.size() > 0) {
                shelfSectionHeaderRsp.setSectionId(sectionId);
                shelfSectionHeaderRsp.setLang(lang);
                shelfSectionHeaderRsp.setDisplayTypeCode(displayTypeCode);
                List<SectionItemHeaderRsp> sectionItemsList = new ArrayList<>();
                int countD = 1;
                log.info("ShelfSectionHeaderMapperDto : "+ gson.toJson(shelfDetail));
                for (ShelfSectionHeaderMapperDto sd : shelfDetail) {
                    if (countD <= maxRow) {
                        SectionItemHeaderRsp sectionItem = new SectionItemHeaderRsp();
                        sectionItem.setItemNo(sd.getPriority() != null ? sd.getPriority().toString() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                        sectionItem.setItemName(sd.getItem_name());
                        sectionItem.setItemDisplayName(sd.getItem_display_name() != null ? sd.getItem_display_name() : sd.getItem_display_name_en());

                        ShelfSectionImageHeaderRsp imageHeaderRsp = new ShelfSectionImageHeaderRsp();
                        imageHeaderRsp.setImageIcon(sd.getItem_icon() != null ? sd.getItem_icon() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                        imageHeaderRsp.setImage1x1(sd.getItem_image1x1() != null ? sd.getItem_image1x1() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                        imageHeaderRsp.setImage4x3(sd.getItem_image4x3() != null ? sd.getItem_image4x3() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                        imageHeaderRsp.setImage16x9(sd.getItem_image16x9() != null ? sd.getItem_image16x9() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                        imageHeaderRsp.setImage9x16(sd.getItem_image9x16() != null ? sd.getItem_image9x16() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);

                        sectionItem.setItemImageList(imageHeaderRsp);
                        sectionItem.setItemType(sd.getItem_type_code());
                        sectionItem.setItemSubtype(sd.getItem_subtype());
                        sectionItem.setShelfType(sd.getShelf_type_code());
                        sectionItem.setItemMapping(sd.getItem_mapping());
                        sectionItem.setItemMapping2(sd.getItem_mapping2());
                        sectionItem.setDummyFlag(sd.getDummy_flag());
                        sectionItemsList.add(sectionItem);
                        countD++;
                    }
                    shelfSectionHeaderRsp.setSectionItem(sectionItemsList);
                }
            } else {
                shelfSectionHeaderRsp = null;
            }
        } else {
            shelfSectionHeaderRsp = null;
        }
        return shelfSectionHeaderRsp;
    }

    private ShelfSectionHeaderRsp mapSectionHeaderDataFromAPI(List<ShelfContentDataApiRsp.ContentData.DataDetails> dataDetailsList, String sectionId, String lang, String
            displayTypeCode, List<RwdSystemConfig> rwdTemplateDefault, Integer maxRow) throws NoSuchFieldException {
        log.info("map SectionHeader Data From API");

        ShelfSectionHeaderRsp shelfSectionHeaderRsp = new ShelfSectionHeaderRsp();
        if (dataDetailsList != null) { //dataDetailsList.size() > 0
            shelfSectionHeaderRsp.setSectionId(sectionId);
            shelfSectionHeaderRsp.setLang(lang);
            shelfSectionHeaderRsp.setDisplayTypeCode(displayTypeCode);
            List<SectionItemHeaderRsp> sectionItemsList = new ArrayList<>();
            int countD = 1;
            int itemNo = 0;
            log.info("ShelfContentDataApiRsp is DataDetails : "+gson.toJson(dataDetailsList));
            for (ShelfContentDataApiRsp.ContentData.DataDetails sd : dataDetailsList) {
                if (countD <= maxRow) {
                    SectionItemHeaderRsp sectionItem = new SectionItemHeaderRsp();
                    sectionItem.setItemNo(String.valueOf(itemNo));
                    sectionItem.setItemName(sd.getId());

                    String displayName = Constant.DEFAULT_NULL_EXCEPTION_VALUE;

                    if (sd.getInfo() != null) {
                        if ("TH".equals(lang) && sd.getInfo().getMerchant_name_th() != null) {
                            displayName = sd.getInfo().getMerchant_name_th();
                        } else if (sd.getInfo().getMerchant_name_en() != null) {
                            displayName = sd.getInfo().getMerchant_name_en();
                        }
                    }

                    sectionItem.setItemDisplayName(displayName);

                    ShelfSectionImageHeaderRsp imageHeaderRsp =  new ShelfSectionImageHeaderRsp();
                    imageHeaderRsp.setImage16x9(sd.getThumb_list() != null ? sd.getThumb_list().getHighlight16x9() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                    sectionItem.setItemImageList(imageHeaderRsp);

//                    sectionItem.setItemImageList(new ShelfSectionImageHeaderRsp("","","","",sd.getThumb_list().getHighlight16x9(),""));
                    sectionItem.setItemType(sd.getContent_type().equals("privilege") ? "DEAL" :
                            sd.getContent_type().equals("trueyoumerchant") ? "MERCHANT" :
                                    sd.getContent_type().equals("trueyouarticle") && (sd.getSetting().getThematic_main_shelf_ids() != null && !sd.getSetting().getThematic_main_shelf_ids().equals("")) ? "THEMATIC" : "ARTICLE");
                    sectionItem.setItemSubtype(rwdTemplateDefault
                            .stream()
                            .anyMatch(r -> r.getConfigGroup().equals(sectionItem.getItemType())) ?
                            rwdTemplateDefault.stream().filter(r -> r.getConfigGroup().equals(sectionItem.getItemType())).findFirst().get().getValue() : Constant.DEFAULT_NULL_EXCEPTION_VALUE
                    );
                    sectionItem.setItemMapping(sd.getId());
                    sectionItem.setDummyFlag("N");
                    sectionItemsList.add(sectionItem);
                    countD++;
                    itemNo++;
                }
            }
            shelfSectionHeaderRsp.setSectionItem(sectionItemsList);
        } else {
            shelfSectionHeaderRsp = null;
        }
        return shelfSectionHeaderRsp;
    }

    private ShelfSectionHeaderRsp mapSectionHeaderDataFromDtacAPI(ShelfDtacInquiryShelfDetailApiRsp dataDetailsList, String sectionId, String lang, String
            displayTypeCode, List<RwdSystemConfig> rwdTemplateDefault, Integer maxRow, String brand) throws NoSuchFieldException, ParseException {
        log.info("map SectionHeader Data From API");

        ShelfSectionHeaderRsp shelfSectionHeaderRsp = new ShelfSectionHeaderRsp();
        if (dataDetailsList != null) { //dataDetailsList.size() > 0

            shelfSectionHeaderRsp.setSectionId(sectionId);
            shelfSectionHeaderRsp.setLang(lang);
            shelfSectionHeaderRsp.setDisplayTypeCode(displayTypeCode);

            List<ShelfDtacInquiryShelfDetailApiRsp.Pattern.PromotionPattern> promotionPatterns = new ArrayList<>();
            promotionPatterns = dataDetailsList.getPattern().get(0).getPromotionPattern();
            List<SectionItemHeaderRsp> sectionItemsList = new ArrayList<>();
            int countD = 1;
            int itemNo = 0;
            if(apigwUtill.isDtac(brand)){
                for (ShelfDtacInquiryShelfDetailApiRsp.Pattern.PromotionPattern sd : promotionPatterns) {

                    if(sd.getId() == null || sd.getId().equals("")){
                        if (countD <= maxRow) {
                            log.info("it does meet the conditions for display grouping. : " + gson.toJson(sd));
                            String itemMapping = Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                            String itemType = Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                            if (sd.getId() != null && !sd.getId().equals("")) {
                                itemType = Constant.DEAL;
                                itemMapping = sd.getId() != null ? sd.getId() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                            } else {
                                switch (sd.getBanner().getType()) {
                                    case "2":
                                        itemType = Constant.MAJOR;
                                        itemMapping = sd.getBanner().getId();
                                        break;
                                    case "7":
                                        itemType = Constant.GROUPING;
                                        itemMapping = sd.getBanner().getId();
                                        break;
                                    case "8":
                                        itemType = Constant.FESTIVE;
                                        itemMapping = sd.getBanner().getId();
                                        break;
                                }
                            }

                            SectionItemHeaderRsp sectionItem = new SectionItemHeaderRsp();
                            sectionItem.setItemNo(String.valueOf(itemNo));
                            sectionItem.setItemName(sd.getId() != null ? sd.getId() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                            sectionItem.setItemDisplayName(sd.getRelatedParty() != null ? sd.getRelatedParty().getName() != null ? sd.getRelatedParty().getName() : Constant.DEFAULT_NULL_EXCEPTION_VALUE : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                            ShelfSectionImageHeaderRsp imageHeaderRsp = new ShelfSectionImageHeaderRsp();
                            imageHeaderRsp.setImage3x2(sd.getBanner() != null ? sd.getBanner().getHref() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                            sectionItem.setItemImageList(imageHeaderRsp);
//                    sectionItem.setItemImageList(new ShelfSectionImageHeaderRsp("","",sd.getBanner().getHref(),"","",""));
                            sectionItem.setItemType(itemType);
                            sectionItem.setItemMapping(itemMapping);
                            sectionItem.setDummyFlag("N");
                            sectionItemsList.add(sectionItem);
                            countD++;
                            itemNo++;
                        }
                    }else {
                        if(sd.getValidFor() != null && sd.getValidFor().getStartDateTime() != null && sd.getValidFor().getEndDateTime() != null) {
                            log.info("validFor is not null");

                            Date date = new Date();
                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date startDateTime = inputFormat.parse(sd.getValidFor().getStartDateTime());
                            Date endDateTime = inputFormat.parse(sd.getValidFor().getEndDateTime());

                            if ((startDateTime.compareTo(date) <= 0) && (endDateTime.compareTo(date) >= 0)) {
                                if (countD <= maxRow) {
                                    log.info("it does meet the conditions for display. : " + gson.toJson(sd));

                                    String itemMapping = Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                                    String itemType = Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                                    if (sd.getId() != null && !sd.getId().equals("")) {
                                        itemType = Constant.DEAL;
                                        itemMapping = sd.getId() != null ? sd.getId() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
                                    } else if (sd.getBanner() != null) {
                                        switch (sd.getBanner().getType()) {
                                            case "2":
                                                itemType = Constant.MAJOR;
                                                itemMapping = sd.getBanner().getId();
                                                break;
                                            case "7":
                                                itemType = Constant.GROUPING;
                                                itemMapping = sd.getBanner().getId();
                                                break;
                                            case "8":
                                                itemType = Constant.FESTIVE;
                                                itemMapping = sd.getBanner().getId();
                                                break;
                                        }
                                    }

                                    SectionItemHeaderRsp sectionItem = new SectionItemHeaderRsp();
                                    sectionItem.setItemNo(String.valueOf(itemNo));
                                    sectionItem.setItemName(sd.getId() != null ? sd.getId() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                                    sectionItem.setItemDisplayName(sd.getRelatedParty() != null ? sd.getRelatedParty().getName() != null ? sd.getRelatedParty().getName() : Constant.DEFAULT_NULL_EXCEPTION_VALUE : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                                    ShelfSectionImageHeaderRsp imageHeaderRsp = new ShelfSectionImageHeaderRsp();
                                    imageHeaderRsp.setImage3x2(sd.getBanner() != null ? sd.getBanner().getHref() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                                    sectionItem.setItemImageList(imageHeaderRsp);
                                    sectionItem.setItemType(itemType);
                                    sectionItem.setItemMapping(itemMapping);
                                    sectionItem.setDummyFlag("N");
                                    sectionItemsList.add(sectionItem);
                                    countD++;
                                    itemNo++;
                                }
                            } else {
                                log.info("startDateTime > Date.Now or endDateTime < Date.Now, it does not meet the conditions for display. : " + gson.toJson(sd));
                            }
                        }
                    }
                }
            }else{
                for (ShelfDtacInquiryShelfDetailApiRsp.Pattern.PromotionPattern sd : promotionPatterns) {
                    if (countD <= maxRow) {
                        SectionItemHeaderRsp sectionItem = new SectionItemHeaderRsp();
                        sectionItem.setItemNo(String.valueOf(itemNo));
                        sectionItem.setItemName(sd.getId() != null? sd.getId():Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                        sectionItem.setItemDisplayName(sd.getRelatedParty() != null ? sd.getRelatedParty().getName() != null ? sd.getRelatedParty().getName():Constant.DEFAULT_NULL_EXCEPTION_VALUE : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                        ShelfSectionImageHeaderRsp imageHeaderRsp =  new ShelfSectionImageHeaderRsp();
                        imageHeaderRsp.setImage3x2(sd.getBanner() != null ? sd.getBanner().getHref() != null ? sd.getBanner().getHref() : Constant.DEFAULT_NULL_EXCEPTION_VALUE : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                        sectionItem.setItemImageList(imageHeaderRsp);
                        sectionItem.setItemType(Constant.DEAL);
                        sectionItem.setItemMapping(sd.getId());
                        sectionItem.setDummyFlag("N");
                        sectionItemsList.add(sectionItem);
                        countD++;
                        itemNo++;
                    }
                }
            }

            shelfSectionHeaderRsp.setSectionItem(sectionItemsList);
        } else {
            shelfSectionHeaderRsp = null;
        }
        return shelfSectionHeaderRsp;
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

    private EndpointResultRWD fetchDtacShelfInquiryShelfDetail(Map<String, Object> tv, String lang, String brand, String productType, String customerNumber, String custGrade) throws Exception {
        log.info("fetchDtacShelfInquiryShelfDetail");
        tv.put("lang", lang);
        tv.put("id", apigwUtill.generateRewardBackendId());
        tv.put("phoneNumber", apigwUtill.encryptPhoneNoToApiGw(tv));
        tv.put("customerNumber", customerNumber);
        tv.put("type", custGrade.replace("_", "%20"));
        tv.put("shelfType", 2);
        tv.put("bannerType", 1);
//        tv.put("relatedParty.type", "T");//test
        tv.put("relatedParty.type", rewardUtill.getShortProductTypeForDtac(productType));
        tv.put("relatedParty.description", 1);
        tv.put("channel", apigwUtill.isDtac(brand)? "dtac":"true");
        shelfDtacInquiryShelfDetailEndpoint.getShelfDtacInquiryShelfDetail(tv);
        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

    private EndpointResultRWD redisShelfDtacInquiryShelfDetail(Map<String, Object> tv, String sectionId, String lang, String brand, String productType, String customerNumber, String custGrade) throws Exception {
        log.info("redisShelfDtacInquiryShelfDetail");
        EndpointResult endpointResult = null;
        EndpointResultRWD endpointResult2 = new EndpointResultRWD();

        String redisKey = sectionId + ":rawdata:getSectionHeader:"+lang;

//        ShelfDtacInquiryShelfDetailApiRsp dataDetails = redisCacheService.get(redisKey,ShelfDtacInquiryShelfDetailApiRsp.class);

//        if(dataDetails == null){
            log.info("Get InquiryShelfDetail Service. : "+ sectionId);
            endpointResult2 = fetchDtacShelfInquiryShelfDetail(tv, lang, brand, productType, customerNumber, custGrade);

            if(endpointResult2.getHttpStatus() == 200){
                redisCacheService.putExpireRedis(redisKey, (ShelfDtacInquiryShelfDetailApiRsp) tv.get("GetShelfDtacInquiryShelfDetail"), Long.valueOf("3600"));// ใช้จริง 3600s
            }

//        }else{
//            log.info("Get InquiryShelfDetail redis. : "+ sectionId);
//            endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
//            endpointResult2 = errorService.convertMapResult(endpointResult);
//            tv.put("GetShelfDtacInquiryShelfDetail", dataDetails);
//        }
        tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);

        return (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
    }

}
