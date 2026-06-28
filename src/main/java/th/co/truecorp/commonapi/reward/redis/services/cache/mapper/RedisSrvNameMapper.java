package th.co.truecorp.commonapi.reward.redis.services.cache.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import th.co.truecorp.commonapi.reward.cache.reward.Interface.CacheKeyMapperAnnInterface;
import th.co.truecorp.commonapi.reward.cache.reward.model.CacheAnnModel;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonlib.jpa.entity.CommonServiceEndpoint;
import th.co.truecorp.commonlib.jpa.service.ConfigService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RedisSrvNameMapper implements CacheKeyMapperAnnInterface {

    @Autowired
    private ConfigService configService;

    public String convertEndpointToCacheKey(CommonServiceEndpoint endpoint) {

        String path = endpoint.getSrvEndpointUrl();
        Optional<List<String>> segments = Optional.ofNullable(
                UriComponentsBuilder.fromHttpUrl(path.trim()).build()
                        .getPathSegments().stream()
                        .map(v -> UriUtils.encodePathSegment(v, "UTF-8"))
                        .collect(Collectors.toList()));

        return segments.map(v -> String.join("_", v))
                .orElseThrow(() -> new IllegalArgumentException("Unable to convert the endpoint to a CacheKey"));
    }

    public String convertEndpointAndDigitalIdToCacheKey(String layoutId, CommonServiceEndpoint endpoint) {
        String serviceName = convertEndpointToCacheKey(endpoint);
        return layoutId + ":" + serviceName;
    }

    @Override
    public String generateCacheKey(CacheAnnModel model) throws Exception {
        String layoutString = (model.getTv()).get(Constant.LAYOUT_ID).toString();
        CommonServiceEndpoint data = configService.findEndpoint(model.getSrcSystemId(), model.getSrvName());
        return convertEndpointAndDigitalIdToCacheKey(layoutString, data);
    }

}
