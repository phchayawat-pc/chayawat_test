package th.co.truecorp.commonapi.reward.service;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cache.reward.model.layout.GetLayoutRequest;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSystemConfig;
import th.co.truecorp.commonapi.reward.cms.jpa.service.*;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.dto.*;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;

import java.sql.Timestamp;
import java.util.*;

@Service
public class ShelfLayoutListService {

    private static Logger log = LoggerFactory.getLogger(ShelfLayoutListService.class);

    Gson gson = new Gson();

    @Autowired
    private LogContextService logContextService;

    @Autowired
    private ResultService resultService;

    @Autowired
    private APIGWUtill apigwUtill;

    @Autowired
    private RwdSystemConfigService rwdSystemConfigService;

    @Autowired
    private RwdLayoutService rwdLayoutService;

    @Autowired
    private ErrorService errorService;

    public RwdSystemConfig PutSystemConfig(Map<String, Object> tv, RwdSystemConfig rwdSystemConfig) throws Exception {
        EndpointResult endpointResult = null;
        RwdSystemConfig systemConfig = new RwdSystemConfig();
        String brand = tv.get("brand") != null ? tv.get("brand").toString() :"";
        try {

            Optional<RwdSystemConfig> optional = rwdSystemConfigService.findConfigs(rwdSystemConfig.getConfigCode(),rwdSystemConfig.getConfigGroup());

            if(optional.isEmpty()){
                systemConfig = rwdSystemConfigService.saveRwdSystemConfig(rwdSystemConfig);
            }else{
                systemConfig = optional.get();
                systemConfig.setDescription(rwdSystemConfig.getDescription());
                systemConfig.setValue(rwdSystemConfig.getValue());
                systemConfig.setModifiedBy(Constant.TRUEAPP);
                systemConfig.setModifiedDate(new Timestamp(new Date().getTime()));
                systemConfig = rwdSystemConfigService.saveRwdSystemConfig(systemConfig);
            }
            log.info("Put SystemConfig is success");
            endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
        } catch (Exception e) {
            log.info("Error Put SystemConfig is " + e.getMessage());
            EndpointResultRWD endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
                    apigwUtill.isDtac(brand) ? "DTAC" : "TRUE",
                    Objects.requireNonNull(Constant.ERROR_DATA_NOT_FOUND),
                    tv.get(ComnConst.KEY_LANGUAGE).toString(),
                    "data not found",
                    Constant.N_A,
                    Constant.MESSAGE);
            endpointResult = errorService.revertMapResult(endpointResultRwd);
            return null;
        }
        tv.put("errput",endpointResult);
        return systemConfig;
    }

    public LayoutObj GetlayoutId(Map<String, Object> tv) throws Exception {

        LayoutObj layout = new LayoutObj();
        EndpointResultRWD endpointResultRwd = new EndpointResultRWD();
        String brand = tv.get("brand") != null ? tv.get("brand").toString() :"";
        String productType = tv.get("productType") != null ? tv.get("productType").toString() :"";
        String chargeType = tv.get("chargeType") != null ? tv.get("chargeType").toString() :"";

        try {

            String rwdLayoutId = null;
            try {
                if (!productType.equals("") && !chargeType.equals("")) {
                    rwdLayoutId = rwdLayoutService.findLayoutIdBrandAndProductTypeAndChargeType("PUBLISH", "Y", brand, productType, chargeType);
                } else if (!productType.equals("") && chargeType.equals("")) {
                    rwdLayoutId = rwdLayoutService.findLayoutIdBrandAndProductType("PUBLISH", "Y", brand, productType);
                } else if (productType.equals("") && !chargeType.equals("")) {
                    rwdLayoutId = rwdLayoutService.findLayoutIdBrandAndChargeType("PUBLISH", "Y", brand, chargeType);
                } else {
                    rwdLayoutId = rwdLayoutService.findLayoutIdBrandCode("PUBLISH", "Y", brand);
                }

            }catch (Exception e ){
                log.info("error get Layout " +brand+ "  is " + e.getMessage());
                layout.setLayoutId(null);
                layout.setMessage("Can’t find data Layout is " + e.getMessage());
                endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
                        apigwUtill.isDtac(brand) ? "DTAC" : "TRUE",
                        Objects.requireNonNull(Constant.ERROR_DATA_NOT_FOUND),
                        tv.get(ComnConst.KEY_LANGUAGE).toString(),
                        "data not found",
                        Constant.N_A,
                        Constant.MESSAGE);
                endpointResultRwd.setEndpointErrorMessage(layout.getMessage());
            }

            if(rwdLayoutId == null) {
                Optional<RwdSystemConfig> optional = rwdSystemConfigService.findConfigs("LAYOUT_DEFAULT",brand);
                RwdSystemConfig systemConfig = optional.get();
                layout.setLayoutId(systemConfig.getValue());
            }else{
                layout.setLayoutId(rwdLayoutId);
                Optional<RwdSystemConfig> optional = rwdSystemConfigService.findConfigs("LAYOUT_DEFAULT",brand);
                RwdSystemConfig systemConfig = optional.get();
                systemConfig.setValue(layout.getLayoutId());
                systemConfig.setModifiedBy(Constant.TRUEAPP);
                systemConfig.setModifiedDate(new Timestamp(new Date().getTime()));
                systemConfig = rwdSystemConfigService.saveRwdSystemConfig(systemConfig);
            }

            log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + "is success");
            endpointResultRwd = errorService.convertMapResult(resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA));

        } catch (Exception e) {
            log.info("error get SystemConfig " +brand+ " is " + e.getMessage());
            layout.setLayoutId(null);
            layout.setMessage("Can’t find data SystemConfig is " + e.getMessage());
            endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
                    apigwUtill.isDtac(brand) ? "DTAC" : "TRUE",
                    Objects.requireNonNull(Constant.ERROR_DATA_NOT_FOUND),
                    tv.get(ComnConst.KEY_LANGUAGE).toString(),
                    "data not found",
                    Constant.N_A,
                    Constant.MESSAGE);
            endpointResultRwd.setEndpointErrorMessage(layout.getMessage());
        }
        tv.put("err",endpointResultRwd);
        return layout;
    }

    @EndpointLog (name = "ALL_DB.Getlayout")
    public EndpointResultRWD GetlayoutListRequest(Map<String, Object> tv, GetLayoutRequest request) throws Exception {

        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();

        EndpointResultRWD endpointResultRwd = null;
        ShelfLoyoutRsp shelfLoyoutRsp = new ShelfLoyoutRsp();

        String brand = tv.get("brand") != null ? tv.get("brand").toString() : "";

        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
        try {

            List<ShelfLayoutSectionMapperDto> results = null;

            results = rwdLayoutService.findLayoutId(request.getId(),lang);

            shelfLoyoutRsp = mapShelfLoyoutRsp(tv, results);

            if(shelfLoyoutRsp.getItemList() != null && shelfLoyoutRsp.getItemList().size() > 0){
                log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + "is success");
                endpointResultRwd = errorService.convertMapResult(resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA));
            }else{
                log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + "is not success");
//                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC, ComnConst.STTS_CODE_SUCC);
                endpointResultRwd = errorService.mapErrorCode(Constant.QUERY_DATA,
                        apigwUtill.isDtac(brand) ? "DTAC" : "TRUE",
                        Objects.requireNonNull(Constant.ERROR_DATA_NOT_FOUND),
                        tv.get(ComnConst.KEY_LANGUAGE).toString(),
                        "data not found",
                        Constant.N_A,
                        Constant.MESSAGE);
            }
//            ---------------------test---------------------------------------------------------
//            shelfLoyoutRsp = shelfLayoutMockForUAT();
//            log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + "is success");
//            endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA);
//            ----------------------------------------------------------------------------------
//            tv.put(Constant.TRANSACTION_RESPONSE_KEY,shelfLoyoutRsp);
            tv.put(Constant.ENDPOINT_SERVICE_GET_LAYOUT,shelfLoyoutRsp);

            tv.put("err",endpointResultRwd);
            log.info("endpointResultRwd : "+endpointResultRwd);
        } catch (Exception e) {
            log.info("error is " + e.getMessage());
//            endpointResult = resultService.getEndpointExceptionResult(tv, e);
            endpointResultRwd =  errorService.convertMapResult(errorService.mapErrorException(e,tv));
            return endpointResultRwd;
        }

        return endpointResultRwd;
    }

    private ShelfLoyoutRsp mapShelfLoyoutRsp(Map<String, Object> tv, List<ShelfLayoutSectionMapperDto> results){
        ShelfLoyoutRsp shelfLoyoutRsp = new ShelfLoyoutRsp();
        List<ShelfLoyoutDetailRsp> details = new ArrayList<ShelfLoyoutDetailRsp>();

        shelfLoyoutRsp.setBrandCode(tv.get("brand").toString());
        shelfLoyoutRsp.setLang(tv.get(ComnConst.KEY_LANGUAGE).toString());
        shelfLoyoutRsp.setChargeType(tv.get("chargeType").toString());
        shelfLoyoutRsp.setProductType(tv.get("productType").toString());
        shelfLoyoutRsp.setLayOutId(tv.get(Constant.LAYOUT_ID).toString());
        if(results != null && results.size() > 0){
            for(ShelfLayoutSectionMapperDto result : results){
                details.add(mapShelfLoyoutDetailRsp(result));
            }
            shelfLoyoutRsp.setItemList(details);
        }else{
            shelfLoyoutRsp.setItemList(null);
        }

        return shelfLoyoutRsp;
    }

    private ShelfLoyoutDetailRsp mapShelfLoyoutDetailRsp(ShelfLayoutSectionMapperDto result){
        log.info("Map Shelf Loyout");
        ShelfLoyoutDetailRsp detail = new ShelfLoyoutDetailRsp();
        if(result != null){
            detail.setSeq(result.getseqNo());
            detail.setSectionId(result.getsectionId());
            detail.setSectionName(result.getsectionName());
            detail.setSectionDisplayType(result.getdisplayNameType());
            try {
                if (result.getsectionDisplayName() != null) {
                    detail.setSectionDisplayName(!result.getsectionDisplayName().equals("") ? result.getsectionDisplayName() : result.getsectionDisplayNameEn());
                    detail.setSectionDisplayImage(!result.getsectionDisplayName().equals("") ? result.getsectionDisplayImage() : result.getsectionDisplayImageEn());
                }else{
                    detail.setSectionDisplayName(Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                    detail.setSectionDisplayImage(Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                }
            }catch (Exception e){
                detail.setSectionDisplayName(Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                detail.setSectionDisplayImage(Constant.DEFAULT_NULL_EXCEPTION_VALUE);
            }
            detail.setShowSectionNameFlag(result.getdisplayHeaderFlag());
            detail.setDisplaySectionType(result.getdisplayTypeCode());
            detail.setAutoSlideFlag(result.getautoSlide());
            detail.setSeeAllFlag(result.getseeAllFlag());
            detail.setUseCmsContent(result.getusedContentCmsFlag());
            if(result.getgotoSectionId() == null){
                detail.setGoToSectionID(result.getsectionId());
            }else{
                detail.setGoToSectionID(result.getgotoSectionId());
            }
            detail.setTemplateCode(result.gettemplateCode());
        }
        return detail;
    }

//    private ShelfLoyoutRsp shelfLayoutMockForUAT() {
//        System.out.println("-------------------test----------------------");
//
//        List<ShelfLoyoutDetailRsp> itemList = new ArrayList<ShelfLoyoutDetailRsp>();
//        ShelfLoyoutDetailRsp item1 = new ShelfLoyoutDetailRsp();
//        item1.setSeq(1);
//        item1.setSectionId("S202407-015");
//        item1.setSectionName("True - Quice Menu");
//        item1.setSectionDisplayType("TEXT");
//        item1.setSectionDisplayName("Quick Menu");
//        item1.setSectionDisplayImage("");
//        item1.setShowSectionNameFlag("Y");
//        item1.setDisplaySectionType("QUICK_ICON");
//        item1.setAutoSlideFlag("N");
//        item1.setSeeAllFlag("N");
//        item1.setUseCmsContent("N");
//        item1.setGoToSectionID("S202407-015");
//        item1.setTemplateCode("NONE");
//        itemList.add(item1);
//
//        ShelfLoyoutDetailRsp item2 = new ShelfLoyoutDetailRsp();
//        item2.setSeq(2);
//        item2.setSectionId("S202407-016");
//        item2.setSectionName("True - Hero Banner");
//        item2.setSectionDisplayType("TEXT");
//        item2.setSectionDisplayName("Hero Banner");
//        item2.setSectionDisplayImage("");
//        item2.setShowSectionNameFlag("Y");
//        item2.setDisplaySectionType("AUTOSLIDE_BANNER");
//        item2.setAutoSlideFlag("Y");
//        item2.setSeeAllFlag("N");
//        item2.setUseCmsContent("N");
//        item2.setGoToSectionID("S202407-016");
//        item2.setTemplateCode("NONE");
//        itemList.add(item2);
//
//        ShelfLoyoutRsp data = new ShelfLoyoutRsp();
//        data.setBrandCode("TRUE");
//        data.setProductType("");
//        data.setChargeType("");
//        data.setLayOutId("L202407-014");
//        data.setLang("EN");
//        data.setItemList(itemList);
//
//        return data;
//    }

}
