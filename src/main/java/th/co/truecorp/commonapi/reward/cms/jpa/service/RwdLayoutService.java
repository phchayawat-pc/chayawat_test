package th.co.truecorp.commonapi.reward.cms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdLayout;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSystemConfig;
import th.co.truecorp.commonapi.reward.cms.jpa.specification.RwdLayoutSpecification;
import th.co.truecorp.commonapi.reward.dto.ShelfLayoutSectionMapperDto;
import th.co.truecorp.commonapi.reward.cms.jpa.repository.RwdLayoutRepository;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;

import java.util.List;
import java.util.Optional;

@Service
public class RwdLayoutService {

    @Autowired
    private LogContextService logContextService;

    @Autowired
    private RwdLayoutRepository layoutRepo;

    @EndpointLog(name = "VENUS_DB.findBrandCode", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public Optional<RwdLayout> findBrandCode(String layoutStatus, String validFlag, String brandCode) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return layoutRepo.findOne(RwdLayoutSpecification.hasByBrand(layoutStatus, validFlag, brandCode));
    }

    @EndpointLog(name = "VENUS_DB.findBrandAndProductType", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public Optional<RwdLayout> findBrandAndProductType(String layoutStatus, String validFlag, String brandCode, String productType) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return layoutRepo.findOne(RwdLayoutSpecification.hasByBrandAndProductType(layoutStatus, validFlag, brandCode, productType));
    }

    @EndpointLog(name = "VENUS_DB.findBrandAndChargeType", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public Optional<RwdLayout> findBrandAndChargeType(String layoutStatus, String validFlag, String brandCode, String chargeType) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return layoutRepo.findOne(RwdLayoutSpecification.hasByBrandAndChargeType(layoutStatus, validFlag, brandCode, chargeType));
    }

    @EndpointLog(name = "VENUS_DB.findBrandAndProductTypeAndChargeType", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public Optional<RwdLayout> findBrandAndProductTypeAndChargeType(String layoutStatus, String validFlag, String brandCode, String productType, String chargeType) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return layoutRepo.findOne(RwdLayoutSpecification.hasByBrandAndProductTypeAndChargeType(layoutStatus, validFlag, brandCode, productType, chargeType));
    }


    @EndpointLog(name = "VENUS_DB.findBrandCode", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public String findLayoutIdBrandCode(String layoutStatus, String validFlag, String brandCode) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return layoutRepo.findRwdLayoutByBrand(layoutStatus, validFlag, brandCode);
    }

    @EndpointLog(name = "VENUS_DB.findBrandAndProductType", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public String findLayoutIdBrandAndProductType(String layoutStatus, String validFlag, String brandCode, String productType) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return layoutRepo.findRwdLayoutByBrandAndProductType(layoutStatus, validFlag, brandCode, productType);
    }

    @EndpointLog(name = "VENUS_DB.findBrandAndChargeType", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public String findLayoutIdBrandAndChargeType(String layoutStatus, String validFlag, String brandCode, String chargeType) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return layoutRepo.findRwdLayoutByBrandAndChargeType(layoutStatus, validFlag, brandCode, chargeType);
    }

    @EndpointLog(name = "VENUS_DB.findBrandAndProductTypeAndChargeType", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public String findLayoutIdBrandAndProductTypeAndChargeType(String layoutStatus, String validFlag, String brandCode, String productType, String chargeType) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return layoutRepo.findRwdLayoutByBrandAndProductTypeAndChargeType(layoutStatus, validFlag, brandCode, productType, chargeType);
    }


    @EndpointLog(name = "VENUS_DB.findBrandCode", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public  List<ShelfLayoutSectionMapperDto> findLayoutId(String LayoutId , String lang) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        return layoutRepo.findRwdLayoutByLayoutId(lang, LayoutId);
    }

}

