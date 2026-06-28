package th.co.truecorp.commonapi.reward.cms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSectionDetail;
import th.co.truecorp.commonapi.reward.cms.jpa.repository.RwdSectionDetailRepository;
import th.co.truecorp.commonapi.reward.cms.jpa.specification.RwdSectionDetailSpecification;
import th.co.truecorp.commonapi.reward.dto.*;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;

import java.util.List;

@Service
public class RwdSectionDetailService {

    @Autowired
    private LogContextService logContextService;

    @Autowired
    private RwdSectionDetailRepository repository;


    @EndpointLog(name = "VENUS_DB.findSectionId", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public List<RwdSectionDetail> findSectionId(String SectionId) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return repository.findAll(RwdSectionDetailSpecification.hasSectionId(SectionId));
    }

    @EndpointLog(name = "VENUS_DB.findDTOSectionId", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public  List<ShelfSectionContentDetailDto> findDTOSectionId(String sectionId , String lang) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return repository.findRwdSectionDetailDTOBySectionId(lang, sectionId);
    }

    @EndpointLog(name = "VENUS_DB.findSectionAllDTOBySectionId", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public  List<ShelfSectionAllDataDto> findSectionAllDTOBySectionId(String sectionId , String lang) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return repository.findShelfSectionAllDataDtoBySectionId(lang, sectionId);
    }

    @EndpointLog(name = "VENUS_DB.findShelfTemplateDetailDtoBySectionIdAndItemMapping", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public  List<ShelfTemplateDetailDto> findShelfTemplateDetailDtoBySectionIdAndItemMapping(String sectionId , String itemMapping) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return repository.findShelfTemplateDetailDtoBySectionIdAndItemMapping(sectionId, itemMapping);
    }
    @EndpointLog(name = "VENUS_DB.findShelfTemplateDetailDtoBySectionId", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public  List<ShelfTemplateDetailDto> findShelfTemplateDetailDtoBySectionId(String sectionId) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return repository.findShelfTemplateDetailDtoBySectionId(sectionId);
    }

    @EndpointLog(name = "VENUS_DB.findShelfSectionDetailDtoBySectionId", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public  List<ShelfSectionDetailDto> findShelfSectionDetailDtoBySectionId(String sectionId) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return repository.findShelfSectionDetailDtoBySectionId(sectionId);
    }

    @EndpointLog(name = "VENUS_DB.findShelfSectionDetailDto2BySectionId", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public  List<ShelfSectionDetailDto2> findShelfSectionDetailDto2BySectionId(String sectionId) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return repository.findShelfSectionDetailDto2BySectionId(sectionId);
    }
}

