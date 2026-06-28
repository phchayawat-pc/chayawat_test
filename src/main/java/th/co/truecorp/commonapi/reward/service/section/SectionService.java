package th.co.truecorp.commonapi.reward.service.section;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSystemConfig;
import th.co.truecorp.commonapi.reward.cms.jpa.service.RwdSystemConfigService;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.model.ShelfSectionHeaderRsp;
import th.co.truecorp.commonapi.reward.redis.RedisCacheService;
import th.co.truecorp.commonapi.reward.service.ShelfSectionHeaderService;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;

import java.util.Map;
import java.util.Optional;

@Service
public class SectionService {
    private static final Logger log = LoggerFactory.getLogger(SectionService.class);

    @Autowired
    private ShelfSectionHeaderService shelfSectionHeaderService;

    @Autowired
    private RwdSystemConfigService rwdSystemConfigService;

    @Autowired
    private ResultService resultService;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private RewardUtill rewardUtill;

//    @CacheAnn(request = Constant.LAYOUT_ID, srvName = Constant.ENDPOINT_SERVICE_GET_LAYOUT, responseType = ShelfLoyoutRsp.class)
    public ResponseEntity<ShelfSectionHeaderRsp> getList(Map<String, Object> tv, String lang, String brand,
                                                         String layoutId, String sectionId, String displayTypeCode, String useCmsContent)
            throws Exception {

        lang = rewardUtill.handleLanguage(lang);
        tv.put(ComnConst.KEY_LANGUAGE, lang);

        ResponseEntity<ShelfSectionHeaderRsp> response = null;
        String redisKey = sectionId+":getSectionHeader:"+lang;
        ShelfSectionHeaderRsp shelfSectionHeaderRspRsp = redisCacheService.get(redisKey,ShelfSectionHeaderRsp.class);

        if(shelfSectionHeaderRspRsp == null){
            log.info("Get shelf Service. : "+ sectionId);

            EndpointResult shelfGetSectionHeaderRsp = shelfSectionHeaderService.getSectionHeader(tv, lang, brand, layoutId, sectionId, displayTypeCode, useCmsContent);
            response = new ResponseEntity<>((ShelfSectionHeaderRsp) tv.get(Constant.ENDPOINT_SERVICE_GET_SECTION_HEADER)
                    , HttpStatusCode.valueOf(shelfGetSectionHeaderRsp.getHttpStatus()));

            if(shelfGetSectionHeaderRsp.getHttpStatus() == 200){
                Optional<RwdSystemConfig> configSessin = rwdSystemConfigService.findConfigs("SESSION_TIME",Constant.REDIS);
                if(!configSessin.isEmpty()){
                    RwdSystemConfig systemConfigSessin = configSessin.get();
                    redisCacheService.putExpireRedis(redisKey, (ShelfSectionHeaderRsp) tv.get(Constant.ENDPOINT_SERVICE_GET_SECTION_HEADER), Long.valueOf(systemConfigSessin.getValue()));
                }
            }
        }else{
            log.info("Get shelf redis. : "+ sectionId);
            response = new ResponseEntity<>(shelfSectionHeaderRspRsp, HttpStatusCode.valueOf(200));
            tv.put("err", resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA));
        }

        return response;
    }
}
