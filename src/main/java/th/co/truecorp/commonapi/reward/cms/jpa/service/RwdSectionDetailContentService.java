package th.co.truecorp.commonapi.reward.cms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSectionDetail;
import th.co.truecorp.commonapi.reward.cms.jpa.repository.RwdSectionDetailContentRepository;
import th.co.truecorp.commonapi.reward.cms.jpa.repository.RwdSectionDetailRepository;
import th.co.truecorp.commonapi.reward.cms.jpa.specification.RwdSectionDetailSpecification;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;

import java.util.List;

@Service
public class RwdSectionDetailContentService {

    @Autowired
    private LogContextService logContextService;

    @Autowired
    private RwdSectionDetailContentRepository repository;


}

