package th.co.truecorp.commonapi.reward.cms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSection;
import th.co.truecorp.commonapi.reward.cms.jpa.repository.RwdSectionRepository;
import th.co.truecorp.commonapi.reward.cms.jpa.specification.RwdSectionSpecification;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;

import java.util.List;

@Service
public class RwdSectionService {

    @Autowired
    private LogContextService logContextService;

    @Autowired
    private RwdSectionRepository repository;

    @EndpointLog(name = "VENUS_DB.findSectionId", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public List<RwdSection> findSectionId(String SectionId) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return repository.findAll(RwdSectionSpecification.hasSectionId(SectionId));
    }

}

