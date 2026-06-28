package th.co.truecorp.commonapi.reward.cache.reward.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class CacheAnnModel {
    private Map<String, Object> tv;
    private Object request;
    private String srvName;
    private String srcSystemId;
    private Class<?> responseType;
}
