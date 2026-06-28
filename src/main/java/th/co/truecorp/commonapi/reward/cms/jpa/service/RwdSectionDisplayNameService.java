package th.co.truecorp.commonapi.reward.cms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSectionDisplayName;
import th.co.truecorp.commonapi.reward.cms.jpa.repository.RwdSectionDisplayNameRepository;
import th.co.truecorp.commonapi.reward.cms.jpa.specification.RwdSectionDisplayNameSpecification;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;

import java.util.Optional;

@Service
public class RwdSectionDisplayNameService {

    @Autowired
    private LogContextService logContextService;

    @Autowired
    private RwdSectionDisplayNameRepository repository;


    @EndpointLog(name = "VENUS_DB.findSectionIdAndLang", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public Optional<RwdSectionDisplayName> findSectionIdAndLang(String sectionId, String lang) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return repository.findOne(RwdSectionDisplayNameSpecification.hasSectionIdAndLang(sectionId,lang));
    }

}

