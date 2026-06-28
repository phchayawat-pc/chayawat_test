package th.co.truecorp.commonapi.reward.redis.services.cache.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import th.co.truecorp.commonapi.reward.cache.reward.Interface.CacheKeyMapperAnnInterface;
import th.co.truecorp.commonapi.reward.cache.reward.model.CacheAnnModel;
import th.co.truecorp.commonapi.reward.cache.reward.model.GetCustomerProductDetailRequest;
import th.co.truecorp.commonlib.jpa.entity.CommonServiceEndpoint;
import th.co.truecorp.commonlib.jpa.service.ConfigService;

@Component
public class CustomerProductDetailKeyMapper implements CacheKeyMapperAnnInterface {

    @Autowired
    private RedisSrvNameMapper redisSrvNameMapper;
    @Autowired
    private ConfigService configService;

    @Override
    public String generateCacheKey(CacheAnnModel model)
            throws Exception {
        String layoutString = (model.getTv()).get("layoutId").toString();
        CommonServiceEndpoint data = configService.findEndpoint(model.getSrcSystemId(), model.getSrvName());
        String key = redisSrvNameMapper.convertEndpointAndDigitalIdToCacheKey(layoutString, data);
        GetCustomerProductDetailRequest req = (GetCustomerProductDetailRequest) model.getRequest();
        return key + ":limit_" + req.getLimit() + ":page_" + req.getPage() + ":type_" + req.getType() + ":channel_"
                + req.getChannel();

    }

}
