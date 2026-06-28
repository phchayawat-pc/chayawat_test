package th.co.truecorp.commonapi.reward.cms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.repository.RwdStaticContentDetailRepository;
import th.co.truecorp.commonapi.reward.dto.ProfileRwdStaticContentDetailDto;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;

import java.util.List;

@Service
public class RwdStaticContentDetailService {

    @Autowired
    private RwdStaticContentDetailRepository repository;


    @EndpointLog(name = "VENUS_DB.findContentIdAndLang", type = EndpointLog.Type.Database_postgresql, logResponse = false)
    public List<ProfileRwdStaticContentDetailDto> findContentIdAndLang(String contentId, String lang) {
        return repository.findContentIdAndLang(contentId, lang);
    }
}

