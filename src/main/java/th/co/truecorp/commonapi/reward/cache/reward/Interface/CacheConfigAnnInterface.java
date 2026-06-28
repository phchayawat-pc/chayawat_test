package th.co.truecorp.commonapi.reward.cache.reward.Interface;

import th.co.truecorp.commonapi.reward.cache.reward.model.CacheAnnModel;

public interface CacheConfigAnnInterface {

    public boolean isEnabled(CacheAnnModel model) throws Exception;

    public Integer getTTl(CacheAnnModel model) throws Exception;
}
