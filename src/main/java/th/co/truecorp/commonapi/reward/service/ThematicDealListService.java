package th.co.truecorp.commonapi.reward.service;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.service.ErrorService;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.endpoint.ContentDetailThematicServiceEndpoint;
import th.co.truecorp.commonapi.reward.endpoint.ProgressiveShelvesServiceEndpoint;
import th.co.truecorp.commonapi.reward.model.EndpointResultRWD;
import th.co.truecorp.commonapi.reward.model.ProgressiveShelvesRsp;
import th.co.truecorp.commonapi.reward.model.ShelfThematicDealListRsp;
import th.co.truecorp.commonapi.reward.model.endpoint.ApiGwContentDetailThematicRsp;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.context.LogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ThematicDealListService {

    @Autowired
    private ContentDetailThematicServiceEndpoint contentDetailThematicServiceEndpoint;

    @Autowired
    private ProgressiveShelvesServiceEndpoint progressiveShelvesServiceEndpoint;

    @Autowired
    private ResultService resultService;

    @Autowired
    private RewardUtill rewardUtill;

    @Autowired
    private LogContextService logContextService;

    @Autowired
    private ErrorService errorService;

    private static Logger log = LoggerFactory.getLogger(ThematicDealListService.class);

    public EndpointResultRWD GetThematicDealListService(Map<String, Object> tv, HttpServletRequest httpRequest) throws Exception {
        final LogContext logContext = logContextService.getCurrentContext();
        tv.put(ComnConst.KEY_LANGUAGE, rewardUtill.handleLanguage(tv.get(ComnConst.KEY_LANGUAGE).toString()));
        logContext.loadHttpRequest(tv, httpRequest);

        try {
            //1. call API Content Detail
            log.info("step 1 : call API Content Detail");
//            tv.put("url","getContentDealList");
            EndpointResult endpointResultContentDetail = contentDetailThematicServiceEndpoint.getContentDetailService(tv);
            if (isSuccessful(endpointResultContentDetail)) {
                ApiGwContentDetailThematicRsp contentDetailThematicRsp = (ApiGwContentDetailThematicRsp) tv.get(Constant.CONTENT_DETAIL_THEMATIC);
                //2. verify response data
                //IF "content/data/content_type" = "trueyouarticle" ELSE raise error content type mismatch END IF;
                if (contentDetailThematicRsp != null
                        && contentDetailThematicRsp.getContent() != null
                        && contentDetailThematicRsp.getContent().getData() != null
                        && Objects.equals(contentDetailThematicRsp.getContent().getData().getContent_type(), Constant.TRUE_YOU_ARTICLE)) {
                    //IF "content/data/article_category[]" = "themetic" THEN go to Next Step
                    //ELSE raise error content type mismatch END IF;
                    log.info("step 2 : data from step 1 found content_type is {}", Constant.TRUE_YOU_ARTICLE);
                    if (contentDetailThematicRsp != null
                            && contentDetailThematicRsp.getContent() != null
                            && contentDetailThematicRsp.getContent().getData() != null
                            && contentDetailThematicRsp.getContent().getData().getArticle_category() != null
                            && !contentDetailThematicRsp.getContent().getData().getArticle_category().isEmpty()
                            && contentDetailThematicRsp.getContent().getData().getArticle_category().get(0) != null
                            && contentDetailThematicRsp.getContent().getData().getArticle_category().get(0).equalsIgnoreCase(Constant.THEMETIC)) {

                        log.info("step 2 : data from step 1 found article_category is not empty and value is {}", Constant.THEMETIC);
                        return errorService.convertMapResult(handleValidContentDetail(tv, contentDetailThematicRsp));
                    } else {
                        return errorService.mapErrorCode(Constant.QUERY_DATA, tv.get("brand").toString().toUpperCase(), Constant.CONTENT_TYPE_MISMATCH, tv.get(ComnConst.KEY_LANGUAGE).toString(), Constant.INVALID_CONTENT_TYPE, Constant.N_A, Constant.MESSAGE);
                    }

                } else {
                    return errorService.mapErrorCode(Constant.QUERY_DATA, tv.get("brand").toString().toUpperCase(), Constant.CONTENT_TYPE_MISMATCH, tv.get(ComnConst.KEY_LANGUAGE).toString(), Constant.INVALID_CONTENT_TYPE, Constant.N_A, Constant.MESSAGE);
                }
            } else {
                return errorService.convertMapResult(endpointResultContentDetail);
            }
        } catch (Exception e) {
            log.info("error is " + e.getMessage());
            return errorService.convertMapResult(errorService.mapErrorException(e, tv));
        }
    }

    private boolean isSuccessful(EndpointResult result) {
        return Objects.equals(result.getEndpointStatusType(), "S");
    }

    private EndpointResult handleValidContentDetail(Map<String, Object> tv, ApiGwContentDetailThematicRsp contentDetailThematicRsp) throws Exception {
        //3. find value from "content/data/setting/thematic_main_shelf_ids"
        log.info("step 3 : find value from content/data/setting/thematic_main_shelf_ids from step 1");
        EndpointResult endpointResultProgressiveShelves = null;
        if (contentDetailThematicRsp != null
                && contentDetailThematicRsp.getContent() != null
                && contentDetailThematicRsp.getContent().getData() != null
                && contentDetailThematicRsp.getContent().getData().getSetting() != null
                && contentDetailThematicRsp.getContent().getData().getSetting().getThematic_main_shelf_ids() != null
                && !contentDetailThematicRsp.getContent().getData().getSetting().getThematic_main_shelf_ids().isEmpty()) {
//4. call API Progressive Shelves **ใช้ value จากข้อ 3) มาเป็น key “cms_id“ ใน API
            log.info("step 4 : call API Progressive Shelves");
            tv.put("shelfIds", contentDetailThematicRsp.getContent().getData().getSetting().getThematic_main_shelf_ids());
//            tv.put("url","getProgressiveShelvesDealList1");
            endpointResultProgressiveShelves = progressiveShelvesServiceEndpoint.getProgressiveShelvesService(tv);
            if (isSuccessful(endpointResultProgressiveShelves)) {
                ProgressiveShelvesRsp progressiveShelvesRsp = (ProgressiveShelvesRsp) tv.get(Constant.PROGRESSIVE_SHELVES);
                if (progressiveShelvesRsp != null &&
                        progressiveShelvesRsp.getProgressive_shelves() != null &&
                        progressiveShelvesRsp.getProgressive_shelves().getData() != null &&
                        progressiveShelvesRsp.getProgressive_shelves().getData().getShelf_items() != null &&
                        !progressiveShelvesRsp.getProgressive_shelves().getData().getShelf_items().isEmpty()) {

                    return handleProgressiveShelves(tv, progressiveShelvesRsp, contentDetailThematicRsp);
                }

            } else {
                return endpointResultProgressiveShelves;
            }
        } else {
            log.info("Thematic main shelf ids is null");
        }
        return buildShelfThematicDealListNoDataRsp(tv);
    }

    private EndpointResult handleProgressiveShelves(Map<String, Object> tv, ProgressiveShelvesRsp progressiveShelvesRsp, ApiGwContentDetailThematicRsp contentDetailThematicRsp) throws Exception {
        //5. fine array from "progressive_shelves/data/shelf_items/setting/view_type" = "thematic_main"
        // IF "progressive_shelves/data/shelf_items/item_type" = "progressive_shelve" THEN
        log.info("step 5 : filter data from step 4 with condition view_type is {} and item_type is {} from data is {}", Constant.THEMATIC_MAIN, Constant.PROGRESSIVE_SHELVE, new Gson().toJson(progressiveShelvesRsp));
        List<ProgressiveShelvesRsp.ShelfItem> shelfItemData = new ArrayList<>();

        if (progressiveShelvesRsp != null &&
                progressiveShelvesRsp.getProgressive_shelves() != null &&
                progressiveShelvesRsp.getProgressive_shelves().getData() != null &&
                progressiveShelvesRsp.getProgressive_shelves().getData().getShelf_items() != null) {

            shelfItemData = progressiveShelvesRsp.getProgressive_shelves().getData().getShelf_items().stream()
                    .filter(item -> item != null &&
                            item.getSetting() != null &&
                            Objects.equals(item.getSetting().getView_type(), Constant.THEMATIC_MAIN) &&
                            Objects.equals(item.getItem_type(), Constant.PROGRESSIVE_SHELVE))
                    .toList();
        }

        log.info("data from filtered is {}", new Gson().toJson(shelfItemData));
        if (!shelfItemData.isEmpty()) {
            //call API Progressive Shelves ** ใช้ "progressive_shelves/data/shelf_items/id" มาเป็น key “cms_id“ ใน API
            tv.put("shelfIds", shelfItemData.get(0).getId());
//            tv.put("url","getProgressiveShelvesDealList2");
            EndpointResult endpointResultProgressiveShelves = progressiveShelvesServiceEndpoint.getProgressiveShelvesService(tv);
            if (isSuccessful(endpointResultProgressiveShelves)) {
                progressiveShelvesRsp = (ProgressiveShelvesRsp) tv.get(Constant.PROGRESSIVE_SHELVES);
                if (!progressiveShelvesRsp.getProgressive_shelves().getData().getShelf_items().isEmpty()) {
                    //6. Loop data จากข้อ 5.)  ใน object array  "progressive_shelves/data/shelf_items/id"
                    //IF "progressive_shelves/data/shelf_items/item_type" = "progressive_shelve" THEN
                    log.info("step 6 : loop data from step 5 for filter with condition item_type is {} from data is {}", Constant.PROGRESSIVE_SHELVE, new Gson().toJson(progressiveShelvesRsp));
                    shelfItemData = progressiveShelvesRsp.getProgressive_shelves().getData().getShelf_items()
                            .stream()
                            .filter(item -> (Objects.equals(item.getItem_type(), Constant.PROGRESSIVE_SHELVE)))
                            .toList();
                    log.info("data from filtered is {}", new Gson().toJson(shelfItemData));
                    if (!shelfItemData.isEmpty()) {
                        List<ProgressiveShelvesRsp> progressiveShelvesRspList = new ArrayList<>();
                        for (int i = 0; i < shelfItemData.size(); i++) {
                            tv.put("shelfIds", shelfItemData.get(i).getId());
//                            tv.put("url","getProgressiveShelvesDealList3");
                            endpointResultProgressiveShelves = progressiveShelvesServiceEndpoint.getProgressiveShelvesService(tv);
                            if (isSuccessful(endpointResultProgressiveShelves)) {
                                ProgressiveShelvesRsp updatedProgressiveShelvesRsp = (ProgressiveShelvesRsp) tv.get(Constant.PROGRESSIVE_SHELVES);
                                progressiveShelvesRspList.add(updatedProgressiveShelvesRsp);
                            }
                        }
                        return buildShelfThematicDealListRsp(tv, contentDetailThematicRsp, progressiveShelvesRspList);
                    }
                }
            } else {
                return endpointResultProgressiveShelves;
            }
        }
        return buildShelfThematicDealListNoDataRsp(tv);
    }

    private EndpointResult buildShelfThematicDealListNoDataRsp(Map<String, Object> tv) throws Exception {
        log.info("build shelf thematic deal list response with no data");
        ShelfThematicDealListRsp shelfThematicDealListRsp = new ShelfThematicDealListRsp();
        shelfThematicDealListRsp.setCampaignId(tv.get("campaignId").toString());
        shelfThematicDealListRsp.setLang(tv.get(ComnConst.KEY_LANGUAGE).toString());
        shelfThematicDealListRsp.setTemplateCode(tv.get("templateCode").toString());
        shelfThematicDealListRsp.setShelfList(null);
        tv.put(Constant.TRANSACTION_RESPONSE_KEY, shelfThematicDealListRsp);
        return resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC, ComnConst.STTS_MSSG_SUCC);
    }

    private EndpointResult buildShelfThematicDealListRsp(Map<String, Object> tv, ApiGwContentDetailThematicRsp contentDetailThematicRsp, List<ProgressiveShelvesRsp> progressiveShelvesRspList) throws Exception {
        log.info("build shelf thematic deal list response with contentDetailThematicRsp is {} and progressiveShelvesRsp is {}", new Gson().toJson(contentDetailThematicRsp), new Gson().toJson(progressiveShelvesRspList));

        ShelfThematicDealListRsp shelfThematicDealListRsp = new ShelfThematicDealListRsp();
        List<ShelfThematicDealListRsp.shelfList> shelfLists = new ArrayList<>();

        for (int i = 0; i < progressiveShelvesRspList.size(); i++) {
            ProgressiveShelvesRsp progressiveShelvesRsp = progressiveShelvesRspList.get(i);
            ProgressiveShelvesRsp.Data data = progressiveShelvesRsp.getProgressive_shelves().getData();
            ProgressiveShelvesRsp.Setting setting = data != null ? data.getSetting() : null;

            ShelfThematicDealListRsp.shelfList shelfList = new ShelfThematicDealListRsp.shelfList();
            shelfList.setSeqNo(i);
            shelfList.setShelfId(data != null ? data.getId() : "");
            shelfList.setShelfName(setting != null ? getItemDisplayName(tv, setting) : "");
            shelfLists.add(shelfList);

            List<ProgressiveShelvesRsp.ShelfItem> shelfItems = data != null ? data.getShelf_items() : new ArrayList<>();
            log.info("SeqNo is {} and ShelfName is {} and shelfItems size is {}", shelfList.getSeqNo(), shelfList.getShelfName(), shelfItems.size());
            List<ShelfThematicDealListRsp.dealList> dealLists = new ArrayList<>();
            for (int j = 0; j < shelfItems.size(); j++) {
                ProgressiveShelvesRsp.ShelfItem shelfItem = shelfItems.get(j);
                ShelfThematicDealListRsp.dealList dealList = new ShelfThematicDealListRsp.dealList();
                dealList.setSeqNo(j);
                dealList.setCampaignId(shelfItem != null ? shelfItem.getId() : "");
                dealList.setCampaignCode(shelfItem != null ? shelfItem.getCampaign_code() : "");
                dealList.setContentType(shelfItem != null ? determineContentType(shelfItem) : "");
                dealList.setTimeCounterFlag(shelfItem != null && shelfItem.getInfo() != null ? shelfItem.getInfo().getTime_counter_show() : "");

                ShelfThematicDealListRsp.thumbnailList thumbnailList = new ShelfThematicDealListRsp.thumbnailList();
                thumbnailList.setThumbnail4x3("");  // Assuming this is always empty as per original code
                thumbnailList.setThumbnail16x9(shelfItem != null && shelfItem.getThumb_list() != null ? shelfItem.getThumb_list().getHighlight16x9() : "");
                dealList.setThumbnailList(thumbnailList);

                if (shelfItem != null && shelfItem.getInfo() != null) {
                    dealList.setCampaignName(Objects.equals(tv.get(ComnConst.KEY_LANGUAGE).toString(), Constant.EN) ? shelfItem.getInfo().getMerchant_name_en() : shelfItem.getInfo().getMerchant_name_th());
                } else {
                    dealList.setCampaignName("");
                }

                dealList.setCampaignDescription(shelfItem != null ? shelfItem.getTitle() : "");
                dealList.setCampaignExpireDate(shelfItem != null ? shelfItem.getExpire_date() : "");
                dealList.setCardType(shelfItem != null ? shelfItem.getCard_type() : new ArrayList<>());

                // Handle potential null value for redeem_point
                Integer redeemPoint = shelfItem != null ? shelfItem.getRedeem_point() : null;
                if (redeemPoint != null) {
                    dealList.setRegularPoint(redeemPoint);
                    dealList.setOfferPoint(redeemPoint);
                }
                dealLists.add(dealList);
            }
            shelfList.setDealList(dealLists);
        }

        shelfThematicDealListRsp.setCampaignId(tv.get("campaignId") != null ? tv.get("campaignId").toString() : "");
        shelfThematicDealListRsp.setLang(tv.get(ComnConst.KEY_LANGUAGE) != null ? tv.get(ComnConst.KEY_LANGUAGE).toString() : "");
        shelfThematicDealListRsp.setTemplateCode(tv.get("templateCode") != null ? tv.get("templateCode").toString() : "");
        shelfThematicDealListRsp.setShelfList(shelfLists);

        tv.put(Constant.TRANSACTION_RESPONSE_KEY, shelfThematicDealListRsp);
        return resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
    }

    private String getItemDisplayName(Map<String, Object> tv, ProgressiveShelvesRsp.Setting setting) {
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
        return lang.equalsIgnoreCase(Constant.TH) ? setting.getTitle_th() :
                lang.equalsIgnoreCase(Constant.EN) ? setting.getTitle_en() :
                        lang.equalsIgnoreCase(Constant.MY) ? setting.getTitle_my() :
                                setting.getTitle_th();
    }

    private String determineContentType(ProgressiveShelvesRsp.ShelfItem shelfItem) {
        String ShelfItemType = shelfItem.getContent_type();
        if (Objects.equals(ShelfItemType, Constant.PRIVILEGE)) {
            return Constant.DEAL;
        } else if (Objects.equals(ShelfItemType, Constant.TRUE_YOU_MERCHANT)) {
            return Constant.MERCHANT;
        } else if (Objects.equals(ShelfItemType, Constant.TRUE_YOU_ARTICLE)) {
            if (shelfItem.getSetting().getThematic_main_shelf_ids() != null) {
                return Constant.THEMATIC.toUpperCase();
            } else {
                return Constant.ARTICLE;
            }
        }
        return "";
    }
}

