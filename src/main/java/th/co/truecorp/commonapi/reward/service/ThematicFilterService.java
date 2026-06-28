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
import th.co.truecorp.commonapi.reward.model.ShelfThematicFilterRsp;
import th.co.truecorp.commonapi.reward.model.endpoint.ApiGwContentDetailThematicRsp;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.context.LogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ThematicFilterService {

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

    private static Logger log = LoggerFactory.getLogger(ThematicFilterService.class);

    public EndpointResultRWD GetThematicFilterService(Map<String, Object> tv, HttpServletRequest httpRequest) throws Exception {
        final LogContext logContext = logContextService.getCurrentContext();
        tv.put(ComnConst.KEY_LANGUAGE, rewardUtill.handleLanguage(tv.get(ComnConst.KEY_LANGUAGE).toString()));
        logContext.loadHttpRequest(tv, httpRequest);

        try {
//            tv.put("url","getContentFilter");
            contentDetailThematicServiceEndpoint.getContentDetailService(tv);
            EndpointResultRWD endpointResultRwdContentDetail = (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
            if (isSuccessful(endpointResultRwdContentDetail)) {
                ApiGwContentDetailThematicRsp contentDetailThematicRsp = (ApiGwContentDetailThematicRsp) tv.get(Constant.CONTENT_DETAIL_THEMATIC);
                if (isValidContentDetail(contentDetailThematicRsp)) {
                    log.info("content type is " + Constant.TRUE_YOU_ARTICLE + " and article category is " + Constant.THEMATIC);
                    return handleValidContentDetail(tv, contentDetailThematicRsp);
                } else {
                    return errorService.mapErrorCode(Constant.QUERY_DATA, tv.get("brand").toString().toUpperCase(), Constant.CONTENT_TYPE_MISMATCH, tv.get(ComnConst.KEY_LANGUAGE).toString(), Constant.INVALID_CONTENT_TYPE, Constant.N_A, Constant.MESSAGE);
                }
            } else {
                return endpointResultRwdContentDetail;
            }
        } catch (Exception e) {
            log.info("Error is " + e.getMessage());
            return errorService.convertMapResult(resultService.getEndpointExceptionResult(tv, e));
        }
    }

    private boolean isSuccessful(EndpointResultRWD result) {
        return Objects.equals(result.getEndpointStatusType(), "S");
    }

    private boolean isValidContentDetail(ApiGwContentDetailThematicRsp contentDetailThematicRsp) {
        if (contentDetailThematicRsp != null &&
                contentDetailThematicRsp.getContent() != null &&
                contentDetailThematicRsp.getContent().getData() != null) {

            var data = contentDetailThematicRsp.getContent().getData();
            var categoryList = data.getArticle_category();

            return Objects.equals(data.getContent_type(), Constant.TRUE_YOU_ARTICLE) &&
                    categoryList != null &&
                    !categoryList.isEmpty() &&
                    categoryList.get(0) != null &&
                    categoryList.get(0).equalsIgnoreCase(Constant.THEMETIC);
        }

        return false;

    }

    private EndpointResultRWD handleValidContentDetail(Map<String, Object> tv, ApiGwContentDetailThematicRsp contentDetailThematicRsp) throws Exception {
        EndpointResultRWD endpointResultProgressiveShelves = null;
        if (contentDetailThematicRsp != null
                && contentDetailThematicRsp.getContent() != null
                && contentDetailThematicRsp.getContent().getData() != null
                && contentDetailThematicRsp.getContent().getData().getSetting() != null
                && contentDetailThematicRsp.getContent().getData().getSetting().getThematic_main_shelf_ids() != null
                && !contentDetailThematicRsp.getContent().getData().getSetting().getThematic_main_shelf_ids().isEmpty()) {
            tv.put("shelfIds", contentDetailThematicRsp.getContent().getData().getSetting().getThematic_main_shelf_ids());
//            tv.put("url","getProgressiveShelvesFilter1");
            progressiveShelvesServiceEndpoint.getProgressiveShelvesService(tv);
            endpointResultProgressiveShelves = (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
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
//                throw new IllegalStateException("Failed to retrieve progressive shelves");
            }
        }
        return buildShelfThematicFilterNoDataRsp(tv);
    }

    private EndpointResultRWD handleProgressiveShelves(Map<String, Object> tv, ProgressiveShelvesRsp progressiveShelvesRsp, ApiGwContentDetailThematicRsp contentDetailThematicRsp) throws Exception {
        log.info("filter data with condition view_type is {} and item_type is {} from data is {}", Constant.THEMATIC_FILTER, Constant.PROGRESSIVE_SHELVE, new Gson().toJson(progressiveShelvesRsp));
        List<ProgressiveShelvesRsp.ShelfItem> shelfItemData = new ArrayList<>();

        if (progressiveShelvesRsp != null &&
                progressiveShelvesRsp.getProgressive_shelves() != null &&
                progressiveShelvesRsp.getProgressive_shelves().getData() != null &&
                progressiveShelvesRsp.getProgressive_shelves().getData().getShelf_items() != null) {

            shelfItemData = progressiveShelvesRsp.getProgressive_shelves().getData().getShelf_items().stream()
                    .filter(item -> item != null &&
                            item.getSetting() != null &&
                            Objects.equals(item.getSetting().getView_type(), Constant.THEMATIC_FILTER) &&
                            Objects.equals(item.getItem_type(), Constant.PROGRESSIVE_SHELVE))
                    .toList();
        }

        log.info("data from filtered is {}", new Gson().toJson(shelfItemData));
        if (!shelfItemData.isEmpty()) {
            tv.put("shelfIds", shelfItemData.get(0).getId());
//            tv.put("url","getProgressiveShelvesFilter2");
            progressiveShelvesServiceEndpoint.getProgressiveShelvesService(tv);
            EndpointResultRWD endpointResultProgressiveShelves = (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
            if (isSuccessful(endpointResultProgressiveShelves)) {
                ProgressiveShelvesRsp updatedProgressiveShelvesRsp = (ProgressiveShelvesRsp) tv.get(Constant.PROGRESSIVE_SHELVES);
                return buildShelfThematicFilterRsp(tv, contentDetailThematicRsp, updatedProgressiveShelvesRsp);
            } else {
                return endpointResultProgressiveShelves;
            }
        }
        return buildShelfThematicFilterNoDataRsp(tv);
    }

    private EndpointResultRWD buildShelfThematicFilterNoDataRsp(Map<String, Object> tv) throws Exception {
        log.info("build shelf thematic filter response with no data");
        ShelfThematicFilterRsp shelfThematicFilterRsp = new ShelfThematicFilterRsp();
        shelfThematicFilterRsp.setCampaignId(tv.get("campaignId").toString());
        shelfThematicFilterRsp.setLang(tv.get(ComnConst.KEY_LANGUAGE).toString());
        shelfThematicFilterRsp.setTemplateCode(tv.get("templateCode").toString());
        shelfThematicFilterRsp.setFilterItem(new ArrayList<>());
        tv.put(Constant.TRANSACTION_RESPONSE_KEY, shelfThematicFilterRsp);
        return errorService.convertMapResult(resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA));
    }

    private EndpointResultRWD buildShelfThematicFilterRsp(Map<String, Object> tv, ApiGwContentDetailThematicRsp contentDetailThematicRsp, ProgressiveShelvesRsp progressiveShelvesRsp) throws Exception {
        log.info("build shelf thematic filter response with contentDetailThematicRsp is {} and progressiveShelvesRsp is {}", new Gson().toJson(contentDetailThematicRsp), new Gson().toJson(progressiveShelvesRsp));
        ShelfThematicFilterRsp shelfThematicFilterRsp = new ShelfThematicFilterRsp();

        shelfThematicFilterRsp.setCampaignId(tv.get("campaignId").toString());
        shelfThematicFilterRsp.setLang(tv.get(ComnConst.KEY_LANGUAGE).toString());
        shelfThematicFilterRsp.setTemplateCode(tv.get("templateCode").toString());
        List<ShelfThematicFilterRsp.FilterItem> filterItemList = new ArrayList<>();

        if (progressiveShelvesRsp != null &&
                progressiveShelvesRsp.getProgressive_shelves() != null &&
                progressiveShelvesRsp.getProgressive_shelves().getData() != null &&
                progressiveShelvesRsp.getProgressive_shelves().getData().getShelf_items() != null) {

            List<ProgressiveShelvesRsp.ShelfItem> shelfItems =
                    progressiveShelvesRsp.getProgressive_shelves().getData().getShelf_items();

            for (int i = 0; i < shelfItems.size(); i++) {
                ProgressiveShelvesRsp.ShelfItem shelfItem = progressiveShelvesRsp.getProgressive_shelves().getData().getShelf_items().get(i);
                ShelfThematicFilterRsp.FilterItem filterItem = new ShelfThematicFilterRsp.FilterItem();

                filterItem.setItemNo(String.valueOf(i));
                filterItem.setItemName(shelfItem.getId());
                filterItem.setItemDisplayName(getItemDisplayName(tv, shelfItem));

                ShelfThematicFilterRsp.ItemImage itemImage = new ShelfThematicFilterRsp.ItemImage();
                itemImage.setImageIcon("");
                itemImage.setImage1x1("");
                itemImage.setImage3x2("");
                itemImage.setImage4x3("");
                itemImage.setImage9x16("");
                itemImage.setImage16x9("");

                filterItem.setItemImageList(itemImage);
                filterItem.setItemType(determineItemType(shelfItem));
                filterItem.setItemSubtype("");
                filterItem.setShelfType("");
                filterItem.setItemMapping(shelfItem.getId());
                filterItem.setItemMapping2("");
                filterItemList.add(filterItem);
            }
        }
        shelfThematicFilterRsp.setFilterItem(filterItemList);

        tv.put(Constant.TRANSACTION_RESPONSE_KEY, shelfThematicFilterRsp);
        return errorService.convertMapResult(resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA));
    }

    private String getItemDisplayName(Map<String, Object> tv, ProgressiveShelvesRsp.ShelfItem shelfItem) {
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
        if (shelfItem != null && shelfItem.getSetting() != null) {
            if (lang.equalsIgnoreCase(Constant.TH)) {
                return shelfItem.getSetting().getTitle_th();
            } else if (lang.equalsIgnoreCase(Constant.EN)) {
                return shelfItem.getSetting().getTitle_en();
            } else {
                return shelfItem.getSetting().getTitle_my();
            }
        }
        return ""; // หรือค่า default ที่เหมาะสม
    }

    private String determineItemType(ProgressiveShelvesRsp.ShelfItem shelfItem) {
        String ShelfItemType = shelfItem.getItem_type();
        if (Objects.equals(ShelfItemType, Constant.PROGRESSIVE_SHELVE)) {
            return Constant.SHELF;
        }
        return "";
    }
}

