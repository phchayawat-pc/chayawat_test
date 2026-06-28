package th.co.truecorp.commonapi.reward.service;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.service.*;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.dto.*;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import java.util.*;

@Service
public class ShelfSectionContentDetailService {

    private static Logger log = LoggerFactory.getLogger(ShelfSectionContentDetailService.class);

    Gson gson = new Gson();

    @Autowired
    private ResultService resultService;

    @Autowired
    private RewardUtill rewardUtill;

    @Autowired
    private RwdSectionDetailService rwdSectionDetailService;

    @Autowired
    private ErrorService errorService;

    @EndpointLog (name = "TRUEAPP.GetSectionContentDetail")
    public EndpointResult getSectionContentDetail(Map<String, Object> tv) throws Exception {

        EndpointResult endpointResult = null;
        try {
        tv.put(ComnConst.KEY_LANGUAGE, rewardUtill.handleLanguage(tv.get(ComnConst.KEY_LANGUAGE).toString()));
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();

        String sectionId = tv.get("sectionId") != null ? tv.get("sectionId").toString():"";
        String displayTypeCode = tv.get("displayTypeCode") != null ? tv.get("displayTypeCode").toString():"";


            List<ShelfSectionContentDetailRsp.SectionItem> sectionItems = new ArrayList<ShelfSectionContentDetailRsp.SectionItem>();
            List<ShelfSectionContentDetailDto> sectionContentDetailDto = rwdSectionDetailService.findDTOSectionId(sectionId, lang);
            sectionItems = mapSectionItem(sectionContentDetailDto);

            if(sectionItems!=null && !sectionItems.isEmpty()){
                log.info("get SectionContentDetail is success");
                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
            }else{
                log.info("get SectionContentDetail is not success");
                endpointResult = resultService.findEndpointResult(tv, ComnConst.ERR_CODE_RESULT_NOT_FOUND, ComnConst.ERR_CODE_RESULT_NOT_FOUND);
                endpointResult.setEndpointErrorMessage("Successfully processed without data, There is no data in the database.");
            }

            ShelfSectionContentDetailRsp sectionContentDetail = new ShelfSectionContentDetailRsp();
            sectionContentDetail.setSectionId(sectionId);
            sectionContentDetail.setLang(lang);
            sectionContentDetail.setDisplayTypeCode(displayTypeCode);
            sectionContentDetail.setSectionItem(sectionItems);
            log.info("set SectionContentDetail : "+gson.toJson(sectionContentDetail));
            tv.put(Constant.TRANSACTION_RESPONSE_KEY,sectionContentDetail);

            log.info("endpointResult : "+endpointResult);
        } catch (Exception e) {
            log.info("get SectionContentDetail error is " + e.getMessage());
            endpointResult = errorService.mapErrorException(e,tv);//resultService.getEndpointExceptionResult(tv, e);
            return endpointResult;
        }

        return endpointResult;
    }

    private List<ShelfSectionContentDetailRsp.SectionItem> mapSectionItem(List<ShelfSectionContentDetailDto> detailDtos){
        log.info("map SectionItem");
        List<ShelfSectionContentDetailRsp.SectionItem> sectionItems = new ArrayList<ShelfSectionContentDetailRsp.SectionItem>();
        Integer seqNo = 0;
        if(!detailDtos.isEmpty()){
            for(ShelfSectionContentDetailDto detail : detailDtos){
                ShelfSectionContentDetailRsp.SectionItem sectionItem = new ShelfSectionContentDetailRsp.SectionItem();
                sectionItem.setItemNo(seqNo.toString());
                sectionItem.setItemName(detail.getitem_name());
                sectionItem.setItemDisplayName(detail.getitem_name());
                sectionItem.setItemType(detail.getitem_type_code());
                sectionItem.setContentDetail(detail.getcontent_lang() != null ? detail.getcontent_en() : detail.getcontent_lang());

                ShelfSectionContentDetailRsp.SectionItem.ItemImageList itemImageList = new ShelfSectionContentDetailRsp.SectionItem.ItemImageList();
                itemImageList.setImageIcon(detail.getitem_icon() != null ? detail.getitem_icon() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                itemImageList.setImage1x1(detail.getitem_image1x1() != null ? detail.getitem_image1x1() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                itemImageList.setImage4x3(detail.getitem_image4x3() != null ? detail.getitem_image4x3() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                itemImageList.setImage16x9(detail.getitem_image16x9() != null ? detail.getitem_image16x9() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                itemImageList.setImage9x16(detail.getitem_image16x9() != null ? detail.getitem_image16x9() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
                sectionItem.setItemImageList(itemImageList);

                sectionItems.add(sectionItem);
                seqNo++;
            }
        }else{
            sectionItems = null;
        }

        return sectionItems;
    }

}
