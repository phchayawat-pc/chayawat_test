package th.co.truecorp.commonapi.reward.service.layout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cache.reward.model.layout.GetLayoutRequest;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSystemConfig;
import th.co.truecorp.commonapi.reward.cms.jpa.service.ErrorService;
import th.co.truecorp.commonapi.reward.cms.jpa.service.RwdSystemConfigService;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.model.EndpointResultRWD;
import th.co.truecorp.commonapi.reward.model.ShelfLoyoutRsp;
import th.co.truecorp.commonapi.reward.service.ShelfLayoutListService;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonapi.reward.redis.RedisCacheService;

import java.util.Map;
import java.util.Optional;

@Service
public class LayoutService {
    private static final Logger log = LoggerFactory.getLogger(LayoutService.class);


    @Autowired
    private ShelfLayoutListService shelfLayoutListService;

    @Autowired
    private RwdSystemConfigService rwdSystemConfigService;

    @Autowired
    private ResultService resultService;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private ErrorService errorService;

    @Autowired
    private RewardUtill rewardUtill;

//    @CacheAnn(request = Constant.LAYOUT_ID, srvName = Constant.ENDPOINT_SERVICE_GET_LAYOUT, responseType = ShelfLoyoutRsp.class)
    public ResponseEntity<ShelfLoyoutRsp> getList(Map<String, Object> tv, String layoutId)
            throws Exception {

        tv.put(ComnConst.KEY_LANGUAGE, rewardUtill.handleLanguage(tv.get(ComnConst.KEY_LANGUAGE).toString()));
        String lang = tv.get(ComnConst.KEY_LANGUAGE).toString();

        String redisKey = layoutId+":getlayout:"+lang;
        ResponseEntity<ShelfLoyoutRsp> response = null;
        ShelfLoyoutRsp shelfLoyoutRsp = redisCacheService.get(redisKey,ShelfLoyoutRsp.class);
        String brand = tv.get("brand").toString();

        if(shelfLoyoutRsp == null){
            log.info("Get shelf layout Service. : "+layoutId);
            GetLayoutRequest request = new GetLayoutRequest(layoutId);

            EndpointResultRWD endpointResult = shelfLayoutListService.GetlayoutListRequest(tv, request);

            response = new ResponseEntity<>((ShelfLoyoutRsp) tv.get(Constant.ENDPOINT_SERVICE_GET_LAYOUT)
                    , HttpStatusCode.valueOf(endpointResult.getHttpStatus()));
            if(endpointResult.getHttpStatus() == 200){

                Optional<RwdSystemConfig> configSessin = rwdSystemConfigService.findConfigs("SESSION_TIME",Constant.REDIS);
                if(!configSessin.isEmpty()){
                    RwdSystemConfig systemConfigSessin = configSessin.get();
                    redisCacheService.putExpireRedis(redisKey, (ShelfLoyoutRsp) tv.get(Constant.ENDPOINT_SERVICE_GET_LAYOUT), Long.valueOf(systemConfigSessin.getValue()));
                }

                Optional<RwdSystemConfig> configRedis = rwdSystemConfigService.findConfigs(brand,Constant.REDIS);
                if (!configRedis.isEmpty()){
                    RwdSystemConfig systemConfigRedis = configRedis.get();
                    systemConfigRedis.setValue(layoutId);
                    shelfLayoutListService.PutSystemConfig(tv, systemConfigRedis);
                }
            }
            tv.put("err",endpointResult);
        }else{
            log.info("Get shelf layout redis. : "+layoutId);
            response = new ResponseEntity<>(shelfLoyoutRsp, HttpStatusCode.valueOf(200));
            tv.put("err", errorService.convertMapResult(resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA)));
        }

        return response;
    }
}
