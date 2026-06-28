package th.co.truecorp.commonapi.reward.cache.reward.Interface;

public interface CacheAnnServiceInterface {

    public <T> T getFromCache(String cacheKey, Class<T> response) throws Exception;

    public boolean hasCache(String cacheKey) throws Exception;

    public void putInCache(String cacheKey, Object response, Long seconds) throws Exception;
}
