package th.co.truecorp.commonapi.reward.cms.jpa.endpointCacheConfig;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.EndpointCacheConfigEntity;
import th.co.truecorp.commonapi.reward.cms.jpa.repository.EndpointCacheConfigRepository;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class EndpointConfigService {

    @Autowired
    private LogContextService logContextService;

    @Autowired
    private EndpointCacheConfigRepository endpointCacheConfigRepository;

    @Value("${app.domain}")
    private String domain;

    List<String> domains;

    @PostConstruct
    void domainList() {
        domains = Arrays.asList("ALL", domain);
    }

    @EndpointLog(name = "Profile.findConfigByEndpoint", logResponse = false)
    public Optional<EndpointCacheConfigEntity> findConfigByEndpoint(Integer Id) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        try {
            Optional<EndpointCacheConfigEntity> config = endpointCacheConfigRepository
                    .findFirstBySrvId(Id);
            return config;
        } catch (Exception exception) {
            logContext.appendFieldsA(new EndpointResult(exception, domain));
            throw exception;
        }
    }
}
