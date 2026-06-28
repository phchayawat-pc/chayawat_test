package th.co.truecorp.commonapi.reward.cache.reward.Interface;

import th.co.truecorp.commonapi.reward.cache.reward.model.CacheAnnModel;

public interface CacheKeyMapperAnnInterface {
    public String generateCacheKey(CacheAnnModel model) throws Exception;
}
