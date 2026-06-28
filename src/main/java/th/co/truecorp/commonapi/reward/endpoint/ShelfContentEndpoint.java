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
import th.co.truecorp.commonapi.reward.model.ShelfContentDataApiRsp;
import th.co.truecorp.commonapi.reward.model.endpoint.ApiGwContentDetailRedeemRsp;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.service.APIGWService;

import java.util.*;

@Component
public class ShelfContentEndpoint {

    private static final Logger log = LoggerFactory.getLogger(ShelfContentEndpoint.class);

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

    @EndpointLog(name = "SYSTEM.getShelfContentApi")
    public EndpointResult getShelfContentApi(Map<String, Object> tv) throws Exception {

        EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        EndpointResult endpointResult = null;
        EndpointResultRWD endpointResult2 = new EndpointResultRWD();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<ShelfContentDataApiRsp.ContentData.DataDetails> privilegeList = new ArrayList<>();

            log.info("Call GetContent");
            HttpHeaders headers = createHeaders(tv);
            Map<String, Object> pathParams = createPathParams(tv);
            Map<String, Object> queryParams = createQueryParams(tv);

            log.info("Calling API GetContent with endpoint");
            ResponseEntity<ApiGwContentDetailRedeemRsp> gwResponse = apigwService.execute(
                    logContext,
                    HttpMethod.GET,
                    Constant.ENDPOINT_SOURCE_SYSTEM_ID,
                    Constant.ENDPOINT_SERVICE_GET_SHELF_CONTENT,
                    ApiGwContentDetailRedeemRsp.class,
                    tv,
                    headers,
                    pathParams,
                    queryParams
            );

            if (gwResponse != null && !gwResponse.getStatusCode().is2xxSuccessful()) {
                log.info("api failed!");
                String errorCode = gwResponse.getBody().getCode();
                String descriptionApigw = gwResponse.getBody().getDescription();
                String businessErrorApigw = gwResponse.getBody().getBusinessError();
                String messageApigw = gwResponse.getBody().getMessage();
                String timestamp = gwResponse.getBody().getTimestamp() != null ? gwResponse.getBody().getTimestamp() : "";
                String errorApigw = gwResponse.getBody().getError();
                String errorMessage = rewardUtill.mapError(descriptionApigw,messageApigw,errorApigw,timestamp);

                endpointResult2 = errorService.mapErrorCode(Constant.QUERY_DATA,
                        tv.get("brand").toString().toUpperCase(),
                        errorCode,
                        tv.get(ComnConst.KEY_LANGUAGE).toString().toUpperCase(),
                        errorMessage,
                        null != businessErrorApigw && !businessErrorApigw.isEmpty() ? businessErrorApigw : Constant.N_A,
                        Constant.MESSAGE);
                privilegeList = null;
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
                privilegeList = parseShelfResponse(gwResponse.getBody().getContent().getData().getPrivilege_list(), tv);
            }

            tv.put("GetContent", privilegeList);

            return endpointResult;
        } catch (Exception exception) {
            log.error("Error during execute apigw: {}", Constant.ENDPOINT_SERVICE_GET_SHELF_CONTENT, exception);
            endpointResult = errorService.mapErrorException(exception,tv);
            endpointResult2 = errorService.convertMapResult(endpointResult);
            tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
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

    private Map<String, Object> createQueryParams(Map<String, Object> tv) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("country", tv.get("country"));
        queryParams.put("lang", tv.get("lang"));
        queryParams.put("expand", tv.get("expand"));
        queryParams.put("expand_limit", tv.get("expand_limit"));
        queryParams.put("fields", tv.get("fields"));
        queryParams.put("cms_id", tv.get("cms_id"));
        return queryParams;
    }

    private List<ShelfContentDataApiRsp.ContentData.DataDetails> parseShelfResponse(List<ApiGwContentDetailRedeemRsp.PrivilegeData> privilegeListNode, Map<String, Object> tv) throws Exception {
        List<ShelfContentDataApiRsp.ContentData.DataDetails> privilegeList = new ArrayList<>();

        log.debug("map response value from api...");
        if(privilegeListNode !=null) {
            for (ApiGwContentDetailRedeemRsp.PrivilegeData result : privilegeListNode) {
                ShelfContentDataApiRsp.ContentData.DataDetails details = new ShelfContentDataApiRsp.ContentData.DataDetails();
                details.setDisplay_country(result.getDisplay_country());
                details.setDisplay_lang(result.getDisplay_lang());
                details.setId(result.getId());
                details.setContent_type(result.getContent_type());
                details.setOriginal_id(result.getOriginal_id());
                details.setTitle(result.getTitle());
                details.setArticle_category(result.getArticle_category() != null ? mapListString(result.getArticle_category()) : null);
                details.setThumb(result.getThumb());
                details.setTags(result.getTags() != null ? mapListString(result.getTags()) : null);
                details.setStatus(result.getStatus());
                details.setCount_views(result.getCount_views());
                details.setPublish_date(result.getPublish_date());
                details.setCreate_date(result.getCreate_date());
                details.setUpdate_date(result.getUpdate_date());
                details.setSearchable(result.getSearchable());
                details.setCreate_by(result.getCreate_by());
                details.setCreate_by_ssoid(result.getCreate_by_ssoid());
                details.setUpdate_by(result.getUpdate_by());
                details.setUpdate_by_ssoid(result.getUpdate_by_ssoid());
                details.setSource_url(result.getSource_url());
                details.setCount_likes(result.getCount_likes());
                details.setCount_ratings(result.getCount_ratings());
                details.setSource_country(result.getSource_country());
                details.setDetail(result.getDetail());
                details.setThumb_list(result.getThumb_list() != null ? mapThumbList(result.getThumb_list()) : null);
                details.setExpire_date(result.getExpire_date());
                details.setInfo(result.getInfo() != null ? mapInfo(result.getInfo()) : null);
                details.setAllow_app(result.getAllow_app() != null ? mapListString(result.getAllow_app()) : null);
                details.setSetting(result.getSetting() != null ? mapSetting(result.getSetting()) : null);
                details.setTerm_and_condition(result.getTerm_and_condition());
                details.setCampaign_type(result.getCampaign_type());
                details.setSub_campaign_type(result.getSub_campaign_type());
                details.setCampaign_code(result.getCampaign_code());
                details.setRedeem_point(result.getRedeem_point());
                details.setCard_type(result.getCard_type() != null ? mapListString(result.getCard_type()) : null);

                privilegeList.add(details);
            }
        }
        log.debug("map response value from api success");
        return privilegeList;
    }

    private List<String> mapListString(List<String> jn) {
        List<String> listVal = new ArrayList<>();
        for (String j:jn) {
            listVal.add(j.replace("\"", ""));
        }
        return listVal;
    }

    private ShelfContentDataApiRsp.ThumbList mapThumbList(ApiGwContentDetailRedeemRsp.ThumbList jsonNode) {
        ShelfContentDataApiRsp.ThumbList thumbList = new ShelfContentDataApiRsp.ThumbList();
        thumbList.setBanner(jsonNode.getBanner() != null ? jsonNode.getBanner() : "");
        thumbList.setHighlight(jsonNode.getHighlight() != null ? jsonNode.getHighlight() : "");
        thumbList.setHighlight16x9(jsonNode.getHighlight16x9() != null ? jsonNode.getHighlight16x9() : "");
        thumbList.setThumbnail(jsonNode.getThumbnail() != null ? jsonNode.getThumbnail() : "");
        thumbList.setLogo_m(jsonNode.getLogo_m() != null ? jsonNode.getLogo_m() : "");
        thumbList.setLogo_s(jsonNode.getLogo_s() != null ? jsonNode.getLogo_s() : "");
        return thumbList;
    }

    private ShelfContentDataApiRsp.InfoData mapInfo(ApiGwContentDetailRedeemRsp.Info jsonNode) {
        ShelfContentDataApiRsp.InfoData infoData = new ShelfContentDataApiRsp.InfoData();
        infoData.setBudget_save_amount(jsonNode.getBudget_save_amount() != null ? jsonNode.getBudget_save_amount() : "");
        infoData.setBudget_save_currency_en(jsonNode.getBudget_save_currency_en() != null ? jsonNode.getBudget_save_currency_en() : "");
        infoData.setBudget_save_currency_th(jsonNode.getBudget_save_currency_th() != null ? jsonNode.getBudget_save_currency_th() : "");
        infoData.setBudget_save_show(jsonNode.getBudget_save_show() != null ? jsonNode.getBudget_save_show() : "");
        infoData.setBudget_save_text_en(jsonNode.getBudget_save_text_en() != null ? jsonNode.getBudget_save_text_en() : "");
        infoData.setBudget_save_text_th(jsonNode.getBudget_save_text_th() != null ? jsonNode.getBudget_save_text_th() : "");
        infoData.setMerchant_id(jsonNode.getMerchant_id() != null ? jsonNode.getMerchant_id() : "");
        infoData.setMerchant_name_th(jsonNode.getMerchant_name_th() != null ? jsonNode.getMerchant_name_th() : "");
        infoData.setMerchant_name_en(jsonNode.getMerchant_name_en() != null ? jsonNode.getMerchant_name_en() : "");
        infoData.setDefault_code_format(jsonNode.getDefault_code_format() != null ? jsonNode.getDefault_code_format() : "");
        infoData.setEx_link(jsonNode.getEx_link() != null ? jsonNode.getEx_link() : "");
        infoData.setPrivilege_version(jsonNode.getPrivilege_version() != null ? jsonNode.getPrivilege_version() : "");
        infoData.setRequireLocation(jsonNode.getRequireLocation() != null ? jsonNode.getRequireLocation() : "");
        infoData.setText_redeem_btn(jsonNode.getText_redeem_btn() != null ? jsonNode.getText_redeem_btn() : "");
        infoData.setTime_counter_show(jsonNode.getTime_counter_show() != null ? jsonNode.getTime_counter_show() : "");
        return infoData;
    }

    private ShelfContentDataApiRsp.SettingData mapSetting(ApiGwContentDetailRedeemRsp.Setting jsonNode) {
        ShelfContentDataApiRsp.SettingData settingData = new ShelfContentDataApiRsp.SettingData();
        if(jsonNode != null){
            settingData.setDeeplink(jsonNode.getDeeplink() != null ? jsonNode.getDeeplink() : "");
            settingData.setThumb_en(jsonNode.getThumb_en() != null ? jsonNode.getThumb_en() : "");
            settingData.setThumb_th(jsonNode.getThumb_th() != null ? jsonNode.getThumb_th() : "");
            settingData.setTitle_en(jsonNode.getTitle_en() != null ? jsonNode.getTitle_en() : "");
            settingData.setTitle_th(jsonNode.getTitle_th() != null ? jsonNode.getTitle_th() : "");
            settingData.setTruecard_type(jsonNode.getTruecard_type() != null ? jsonNode.getTruecard_type() : "");
            settingData.setTime_counter_show(jsonNode.getTime_counter_show() != null ? jsonNode.getTime_counter_show() : "");
            settingData.setThematic_main_shelf_ids(jsonNode.getThematic_main_shelf_ids() != null ? jsonNode.getThematic_main_shelf_ids() : "");
        }else{
            settingData = null;
        }

        return settingData;
    }

}
