package th.co.truecorp.commonapi.reward.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import th.co.truecorp.commonlib.jpa.entity.CommonRequestor;
import th.co.truecorp.commonlib.jpa.repository.CommonRequestorRepository;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;

@Service
public class CommonRequestorService {

    @Value("${app.domain}")
    private String appDomain;

    @Autowired
    private CommonRequestorRepository commonRequestorRepository;

    @Autowired
    private LogContextService logContextService;

    public CommonRequestor findRequestorByClientId(String clientId) {
        final EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        try {
            Optional<CommonRequestor> commonRequestorData = commonRequestorRepository.findByReqUsername(clientId);

            return commonRequestorData.orElse(null);
        } catch (Exception exception) {
            logContext.appendFieldsA(new EndpointResult(exception, appDomain));
            throw exception;
        }
    }

}
