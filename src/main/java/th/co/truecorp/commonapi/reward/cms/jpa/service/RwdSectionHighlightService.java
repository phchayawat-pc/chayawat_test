package th.co.truecorp.commonapi.reward.cms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSectionHighlight;
import th.co.truecorp.commonapi.reward.cms.jpa.repository.RwdSectionHighlightRepository;
import th.co.truecorp.commonapi.reward.cms.jpa.specification.RwdSectionHighlightSpecification;
import th.co.truecorp.commonapi.reward.dto.ShelfSectionHighlightDto;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;

import java.util.List;

@Service
public class RwdSectionHighlightService {

    @Autowired
    private LogContextService logContextService;

    @Autowired
    private RwdSectionHighlightRepository repository;

    @EndpointLog(name = "VENUS_DB.findSectionIdDefault", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public List<RwdSectionHighlight> findSectionIdDefault(String sectionId) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return repository.findAll(RwdSectionHighlightSpecification.hasSectionId(sectionId));
    }

    @EndpointLog(name = "VENUS_DB.findShelfSectionHighlightDtoBySectionId", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public  List<ShelfSectionHighlightDto> findShelfSectionHighlightDtoBySectionId(String sectionId) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return repository.findBySectionId(sectionId);
    }

    @EndpointLog(name = "VENUS_DB.findShelfSectionHighlightDtoBySectionIdAndItemMapping", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public  List<ShelfSectionHighlightDto> findShelfSectionHighlightDtoBySectionIdAndItemMapping(String sectionId, String itemMapping) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return repository.findBySectionIdAndItemMapping(sectionId, itemMapping);
    }

    @EndpointLog(name = "VENUS_DB.findShelfSectionHighlightDtoBySectionIdAndRowNum", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public  List<ShelfSectionHighlightDto> findShelfSectionHighlightDtoBySectionIdAndRowNum(String sectionId, Integer rowNum) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return repository.findBySectionIdAndRowNum(sectionId, rowNum);
    }
}

