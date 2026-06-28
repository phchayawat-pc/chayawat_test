package th.co.truecorp.commonapi.reward.cms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSectionDetail;
import th.co.truecorp.commonapi.reward.cms.jpa.repository.RwdSectionDetailRepository;
import th.co.truecorp.commonapi.reward.cms.jpa.repository.ShelfSectionDetailRepo;
import th.co.truecorp.commonapi.reward.cms.jpa.repository.ShelfSectionRepo;
import th.co.truecorp.commonapi.reward.cms.jpa.specification.RwdSpecification;
import th.co.truecorp.commonapi.reward.dto.ShelfSectionHeaderMapperDto;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;

import java.util.List;

@Service
public class ShelfCmsService {

    @Autowired
    private LogContextService logContextService;

    @Autowired
    private ShelfSectionRepo shelfSectionRepo;

    @Autowired
    private RwdSectionDetailRepository rwdSectionDetailRepository;

    @Autowired
    private ShelfSectionDetailRepo shelfSectionDetailRepo;

    @EndpointLog(name = "VENUS_DB.getSectionHeader", type = EndpointLog.Type.Database_postgresql, logResponse = false) //logResponse = false will show statusCode
    public List<ShelfSectionHeaderMapperDto> getSectionHeader(String sectionId, String lang) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        try {
            List<ShelfSectionHeaderMapperDto> result = shelfSectionRepo.findRwdSectionEntitiesById(sectionId, lang);
            return result;
        } catch (Exception exception) {
            throw exception;
        }
    }

    @EndpointLog(name = "VENUS_DB.findTotalSectionDetailCount", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public Integer findTotalSectionDetailCount(String sectionId) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return Math.toIntExact(rwdSectionDetailRepository.count(RwdSpecification.countTotalSectionDetail(sectionId)));
    }

    @EndpointLog(name = "VENUS_DB.findSectionDetailBySectionIdAndItemTypeCode", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public List<RwdSectionDetail> findSectionDetailBySectionIdAndItemTypeCode(String sectionId, List<String> itemList) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return rwdSectionDetailRepository.findAll(RwdSpecification.findSectionDetailBySectionIdAndItemTypeCode(sectionId, itemList));
    }

    @EndpointLog(name = "VENUS_DB.findSectionDetailBySectionIdAndLang", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public List<RwdSectionDetail> findSectionDetailBySectionIdAndLang(String sectionId, String lang) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return rwdSectionDetailRepository.findAll(RwdSpecification.findSectionDetailBySectionIdAndLang(sectionId, lang));
    }
}

