package th.co.truecorp.commonapi.reward.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.repository.ShelfSectionRepo;
import th.co.truecorp.commonapi.reward.cms.jpa.service.RwdSectionDetailService;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.dto.ShelfSectionDetailDto;
import th.co.truecorp.commonapi.reward.redis.RedisCacheService;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.log.context.LogContextService;

import java.util.*;

@Service
public class ClearCacheService {
    private static final Logger log = LoggerFactory.getLogger(ClearCacheService.class);

    @Autowired
    private LogContextService logContextService;

    @Autowired
    private ShelfSectionRepo shelfSectionRepo;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private RwdSectionDetailService rwdSectionDetailService;

    @Autowired
    private RewardUtill rewardUtill;

    public ResponseEntity<String> clearCacheShelf(Map<String, Object> tv, String layoutId)
            throws Exception {
        ResponseEntity<String> response = null;
        try {
            String lang = rewardUtill.handleLanguage(tv.get(ComnConst.KEY_LANGUAGE).toString());

            log.info("clear cache");
            log.info("layoutId= " + layoutId);

            List<String> sectionIdList = shelfSectionRepo.findSectionIdByLayoutId(layoutId);
            log.info("sectionIdList= " + sectionIdList.size());

            String redisLayoutKey = layoutId+ ":getlayout:"+lang;
            redisCacheService.delete(redisLayoutKey);
            for(String sectionId: sectionIdList){
                String redisSectionHeaderKey = sectionId+":getSectionHeader:"+lang;
                redisCacheService.delete(redisSectionHeaderKey);
                String redisRawDataSectionHeaderKey = sectionId+":rawdata:getSectionHeader:"+lang;
                redisCacheService.delete(redisRawDataSectionHeaderKey);
                List<ShelfSectionDetailDto>sectionDetailDtoList = rwdSectionDetailService.findShelfSectionDetailDtoBySectionId(sectionId);
                if (!sectionDetailDtoList.isEmpty()) {
                    for (ShelfSectionDetailDto sectionDetail : sectionDetailDtoList) {
                        String itemMapping = sectionDetail.getitem_mapping();
                        String redisRawDataSectionDetailKey = sectionId+":rawdata:getSectionDetail:"+itemMapping+":"+lang;;
                        redisCacheService.delete(redisRawDataSectionDetailKey);
                    }
                }

            }

            tv.put(Constant.TRANSACTION_RESPONSE_KEY,"done");
        } catch (Exception e){
            tv.put(Constant.TRANSACTION_RESPONSE_KEY,"failed");
        }

        return response;
    }
}
