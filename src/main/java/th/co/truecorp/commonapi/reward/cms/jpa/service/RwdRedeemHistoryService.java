package th.co.truecorp.commonapi.reward.cms.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdRedeemHistory;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSection;
import th.co.truecorp.commonapi.reward.cms.jpa.repository.RwdRedeemHistoryRepository;
import th.co.truecorp.commonapi.reward.cms.jpa.repository.RwdSectionRepository;
import th.co.truecorp.commonapi.reward.cms.jpa.specification.RwdSectionSpecification;
import th.co.truecorp.commonapi.reward.dto.HistoryPointDto;
import th.co.truecorp.commonapi.reward.service.HistoryListService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;

import java.util.List;

@Service
public class RwdRedeemHistoryService {

    @Autowired
    private LogContextService logContextService;

    @Autowired
    private RwdRedeemHistoryRepository repository;

    private static Logger log = LoggerFactory.getLogger(RwdRedeemHistoryService.class);

    @EndpointLog(name = "VENUS_DB.findRedeemHistoryAll", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public List<RwdRedeemHistory> findRedeemHistoryAll() {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return repository.findAll();
    }

    @EndpointLog(name = "VENUS_DB.findRwdRedeemHistoryToHistoryPointDto", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public List<RwdRedeemHistory> findRwdRedeemHistoryToHistoryPointDto(String redeemStatus,
                                                                        String brandCode, String digitalId,
                                                                        String startDate, String endDate) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();

        // Log the parameters
        log.info("Executing findRwdRedeemHistory with params: redeemStatus={}, brandCode={}, digitalId={}, startDate={}, endDate={}",
                redeemStatus, brandCode, digitalId, startDate, endDate);

        return repository.findRwdRedeemHistoryByRedeemStatusAndBrandCodeAndDigitalIdAndActionDateStartEnd(redeemStatus, brandCode, digitalId, startDate, endDate);
    }

    @EndpointLog(name = "VENUS_DB.findCampaignIdToHistoryPointDto", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public List<String> findCampaignIdToHistoryPointDto(String redeemStatus,
                                                                        String brandCode, String digitalId,
                                                                        String startDate, String endDate) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();

        // Log the parameters
        log.info("Executing findCampaignIdToHistoryPointDto with params: redeemStatus={}, brandCode={}, digitalId={}, startDate={}, endDate={}",
                redeemStatus, brandCode, digitalId, startDate, endDate);

        return repository.findCampaignIdByRedeemStatusAndBrandCodeAndDigitalIdAndActionDateStartEnd(redeemStatus, brandCode, digitalId, startDate, endDate);
    }

}

