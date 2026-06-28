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
import th.co.truecorp.commonapi.reward.model.ShelfThematicHeaderRsp;
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
public class ThematicHeaderService {

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

    private static Logger log = LoggerFactory.getLogger(ThematicHeaderService.class);

    public EndpointResultRWD GetThematicHeaderService(Map<String, Object> tv, HttpServletRequest httpRequest) throws Exception {
        final LogContext logContext = logContextService.getCurrentContext();
        tv.put(ComnConst.KEY_LANGUAGE, rewardUtill.handleLanguage(tv.get(ComnConst.KEY_LANGUAGE).toString()));
        logContext.loadHttpRequest(tv, httpRequest);

        try {
            tv.put("url", "getContentHeader");
            contentDetailThematicServiceEndpoint.getContentDetailService(tv);
            EndpointResultRWD endpointResultRWDContentDetail = (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
            if (isSuccessful(endpointResultRWDContentDetail)) {
                ApiGwContentDetailThematicRsp contentDetailThematicRsp = (ApiGwContentDetailThematicRsp) tv.get(Constant.CONTENT_DETAIL_THEMATIC);
                if (isValidContentDetail(contentDetailThematicRsp)) {
                    log.info("content type is " + Constant.TRUE_YOU_ARTICLE + " and article category is " + Constant.THEMATIC);
                    return handleValidContentDetail(tv, contentDetailThematicRsp);
                } else {
                    return errorService.mapErrorCode(Constant.QUERY_DATA, tv.get("brand").toString().toUpperCase(), Constant.CONTENT_TYPE_MISMATCH, tv.get(ComnConst.KEY_LANGUAGE).toString(), Constant.INVALID_CONTENT_TYPE, Constant.N_A, Constant.MESSAGE);
                }
            } else {
                return endpointResultRWDContentDetail;
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
        ApiGwContentDetailThematicRsp.DataDetail data = contentDetailThematicRsp != null &&
                contentDetailThematicRsp.getContent() != null
                ? contentDetailThematicRsp.getContent().getData()
                : null;

        return data != null &&
                Objects.equals(data.getContent_type(), Constant.TRUE_YOU_ARTICLE) &&
                data.getArticle_category() != null &&
                !data.getArticle_category().isEmpty() &&
                data.getArticle_category().get(0) != null &&
                data.getArticle_category().get(0).equalsIgnoreCase(Constant.THEMETIC);
    }

    private EndpointResultRWD handleValidContentDetail(Map<String, Object> tv, ApiGwContentDetailThematicRsp contentDetailThematicRsp) throws Exception {
        EndpointResultRWD endpointResultProgressiveShelves = null;
        if (contentDetailThematicRsp != null &&
                contentDetailThematicRsp.getContent() != null &&
                contentDetailThematicRsp.getContent().getData() != null &&
                contentDetailThematicRsp.getContent().getData().getSetting() != null &&
                contentDetailThematicRsp.getContent().getData().getSetting().getThematic_main_shelf_ids() != null &&
                !contentDetailThematicRsp.getContent().getData().getSetting().getThematic_main_shelf_ids().isEmpty()) {
            tv.put("shelfIds", contentDetailThematicRsp.getContent().getData().getSetting().getThematic_main_shelf_ids());
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
//        throw new IllegalArgumentException("No thematic main shelf ids found");
//        return resultService.mapEndpointResult(tv, Constant.ENDPOINT_SOURCE_SYSTEM_ID, Constant.ENDPOINT_SERVICE_GET_CONTENT, Constant.NO_THEMATIC_MAIN_SHELF_IDS_FOUND);
        return buildShelfThematicHeaderRsp(tv, contentDetailThematicRsp, null);
    }

    private EndpointResultRWD handleProgressiveShelves(Map<String, Object> tv, ProgressiveShelvesRsp progressiveShelvesRsp, ApiGwContentDetailThematicRsp contentDetailThematicRsp) throws Exception {
        log.info("filter data wit condition view_type = {} and item_type = {} from shelf_items = {}", Constant.SHELF_BANNER_AUTOSLIDE, Constant.PROGRESSIVE_SHELVE, progressiveShelvesRsp.getProgressive_shelves().getData().getShelf_items());
        List<ProgressiveShelvesRsp.ShelfItem> shelfItemData = new ArrayList<>();

        if (progressiveShelvesRsp != null &&
                progressiveShelvesRsp.getProgressive_shelves() != null &&
                progressiveShelvesRsp.getProgressive_shelves().getData() != null &&
                progressiveShelvesRsp.getProgressive_shelves().getData().getShelf_items() != null) {

            shelfItemData = progressiveShelvesRsp.getProgressive_shelves().getData().getShelf_items().stream()
                    .filter(item -> item != null &&
                            item.getSetting() != null &&
                            Objects.equals(item.getSetting().getView_type(), Constant.SHELF_BANNER_AUTOSLIDE) &&
                            Objects.equals(item.getItem_type(), Constant.PROGRESSIVE_SHELVE))
                    .toList();
        }
        log.info("data filtered is " + new Gson().toJson(shelfItemData));
        if (!shelfItemData.isEmpty()) {
            tv.put("shelfIds", shelfItemData.get(0).getId());
            progressiveShelvesServiceEndpoint.getProgressiveShelvesService(tv);
            EndpointResultRWD endpointResultProgressiveShelves = (EndpointResultRWD) tv.get(Constant.ENDPOINT_RESULT_RWD);
            if (isSuccessful(endpointResultProgressiveShelves)) {
                ProgressiveShelvesRsp updatedProgressiveShelvesRsp = (ProgressiveShelvesRsp) tv.get(Constant.PROGRESSIVE_SHELVES);
                return buildShelfThematicHeaderRsp(tv, contentDetailThematicRsp, updatedProgressiveShelvesRsp);
            } else {
                return endpointResultProgressiveShelves;
                // throw new IllegalStateException("Failed to retrieve updated progressive shelves");
            }
        } else {
            return buildShelfThematicHeaderRsp(tv, contentDetailThematicRsp, null);
        }
//        throw new IllegalArgumentException("No valid shelf items found");
//        return resultService.mapEndpointResult(tv, Constant.ENDPOINT_SOURCE_SYSTEM_ID, Constant.ENDPOINT_SERVICE_GET_CONTENT, Constant.NO_VALID_SHELF_ITEMS_FOUND);
    }

    private EndpointResultRWD buildShelfThematicHeaderRsp(Map<String, Object> tv, ApiGwContentDetailThematicRsp contentDetailThematicRsp, ProgressiveShelvesRsp progressiveShelvesRsp) throws Exception {
        log.info("build shelf thematic header response with contentDetailThematicRsp is {} and progressiveShelvesRsp is {}", new Gson().toJson(contentDetailThematicRsp), new Gson().toJson(progressiveShelvesRsp));
        ShelfThematicHeaderRsp shelfThematicHeaderRsp = new ShelfThematicHeaderRsp();

        shelfThematicHeaderRsp.setCampaignId(tv.get("campaignId").toString());
        shelfThematicHeaderRsp.setTemplateCode(tv.get("templateCode").toString());
        shelfThematicHeaderRsp.setSectionId(tv.get("sectionId").toString());

        ShelfThematicHeaderRsp.thumbnail thumbnail = new ShelfThematicHeaderRsp.thumbnail();
        if (contentDetailThematicRsp != null &&
                contentDetailThematicRsp.getContent() != null &&
                contentDetailThematicRsp.getContent().getData() != null &&
                contentDetailThematicRsp.getContent().getData().getThumb_list() != null) {

            thumbnail.setThumbnail16x9(contentDetailThematicRsp.getContent().getData().getThumb_list().getHighlight16x9());
        } else {
            thumbnail.setThumbnail16x9(null);
        }
        shelfThematicHeaderRsp.setThumbnailList(thumbnail);

        if (contentDetailThematicRsp != null &&
                contentDetailThematicRsp.getContent() != null &&
                contentDetailThematicRsp.getContent().getData() != null) {

            shelfThematicHeaderRsp.setCampaignName(contentDetailThematicRsp.getContent().getData().getTitle());
            shelfThematicHeaderRsp.setCampaignDescription(contentDetailThematicRsp.getContent().getData().getDetail());
            shelfThematicHeaderRsp.setCampaignExpireDate(contentDetailThematicRsp.getContent().getData().getExpire_date());
        } else {
            // สามารถเซ็ต default value หรือไม่เซ็ตเลยก็ได้ แล้วแต่กรณี
            shelfThematicHeaderRsp.setCampaignName(null);
            shelfThematicHeaderRsp.setCampaignDescription(null);
            shelfThematicHeaderRsp.setCampaignExpireDate(null);
        }


        List<ShelfThematicHeaderRsp.autoSlide> autoSlideList = new ArrayList<>();
        if (progressiveShelvesRsp != null &&
                progressiveShelvesRsp.getProgressive_shelves() != null &&
                progressiveShelvesRsp.getProgressive_shelves().getData() != null &&
                progressiveShelvesRsp.getProgressive_shelves().getData().getShelf_items() != null) {

            for (int i = 0; i < progressiveShelvesRsp.getProgressive_shelves().getData().getShelf_items().size(); i++) {
                ProgressiveShelvesRsp.ShelfItem shelfItem = progressiveShelvesRsp.getProgressive_shelves().getData().getShelf_items().get(i);
                ShelfThematicHeaderRsp.autoSlide autoSlide = new ShelfThematicHeaderRsp.autoSlide();

                autoSlide.setItemNo(String.valueOf(i));
                autoSlide.setItemName(shelfItem.getId());
                autoSlide.setItemDisplayName(getItemDisplayName(tv, shelfItem));

                ShelfThematicHeaderRsp.itemImage itemImage = new ShelfThematicHeaderRsp.itemImage();
                itemImage.setImage16x9(shelfItem.getThumb_list() != null ? shelfItem.getThumb_list().getHighlight16x9() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);

                autoSlide.setItemImageList(itemImage);
                autoSlide.setItemType(determineItemType(shelfItem));
                autoSlide.setItemMapping(shelfItem.getId());
                autoSlideList.add(autoSlide);
            }
            shelfThematicHeaderRsp.setAutoSlideList(autoSlideList);
        } else {
            shelfThematicHeaderRsp.setAutoSlideList(new ArrayList<>());
        }

        tv.put(Constant.TRANSACTION_RESPONSE_KEY, shelfThematicHeaderRsp);
        return errorService.convertMapResult(resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA));
    }

    private String getItemDisplayName(Map<String, Object> tv, ProgressiveShelvesRsp.ShelfItem shelfItem) {
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();
        if (null != shelfItem.getInfo()) {
            return lang.equalsIgnoreCase(Constant.TH) ? shelfItem.getInfo().getMerchant_name_th() :
                    lang.equalsIgnoreCase(Constant.EN) ? shelfItem.getInfo().getMerchant_name_en() : Constant.DEFAULT_NULL_EXCEPTION_VALUE;
        } else {
            return "";
        }

    }

    private String determineItemType(ProgressiveShelvesRsp.ShelfItem shelfItem) {
        String ShelfContentType = shelfItem.getContent_type();
        if (Objects.equals(ShelfContentType, Constant.PRIVILEGE)) {
            return Constant.DEAL;
        } else if (Objects.equals(ShelfContentType, Constant.TRUE_YOU_MERCHANT)) {
            return Constant.MERCHANT;
        } else if (Objects.equals(ShelfContentType, Constant.TRUE_YOU_ARTICLE)) {
            return shelfItem != null
                    && shelfItem.getSetting() != null
                    && shelfItem.getSetting().getThematic_main_shelf_ids() != null
                    ? Constant.THEMETIC
                    : Constant.ARTICLE;
        }
        return "";
    }
}

