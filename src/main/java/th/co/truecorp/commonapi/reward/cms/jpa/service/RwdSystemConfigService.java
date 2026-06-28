package th.co.truecorp.commonapi.reward.cms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSystemConfig;
import th.co.truecorp.commonapi.reward.cms.jpa.repository.RwdSystemConfigRepository;
import th.co.truecorp.commonapi.reward.cms.jpa.specification.RwdSpecification;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;

import java.util.List;
import java.util.Optional;

import static th.co.truecorp.commonlib.log.annotation.EndpointLog.Type.Database_postgresql;

@Service
public class RwdSystemConfigService {

    @Autowired
    private LogContextService logContextService;

    @Autowired
    private RwdSystemConfigRepository repository;


    @EndpointLog(name = "VENUS_DB.findConfigs", type = Database_postgresql, logResponse = false)
    public Optional<RwdSystemConfig> findConfigs(String configCode, String configGroup) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return repository.findOne(RwdSpecification.hasConfigCodeGroup(configCode, configGroup));
    }

    @EndpointLog(name = "VENUS_DB.findConfigCode", type = Database_postgresql, logResponse = false)
    public Optional<RwdSystemConfig> findConfigCode(String configCode) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return repository.findOne(RwdSpecification.hasConfigCode(configCode));
    }

    @EndpointLog(name = "VENUS_DB.findConfigTemplateDefault", type = Database_postgresql, logResponse = false)
    public List<RwdSystemConfig> findConfigTemplateDefault(String configCode) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return repository.findAllByConfigCode(configCode);
    }

    @EndpointLog(name = "VENUS_DB.findConfigGroupTemplateDefault", type = Database_postgresql, logResponse = false)
    public List<RwdSystemConfig> findConfigGroupTemplateDefault(String configGroup) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return repository.findAllByConfigGroup(configGroup);
    }

    @EndpointLog(name = "VENUS_DB.findConfigTemplateDefault", type = Database_postgresql, logResponse = false)
    public RwdSystemConfig saveRwdSystemConfig(RwdSystemConfig entity) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return repository.save(entity);
    }

}

