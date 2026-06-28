package th.co.truecorp.commonapi.reward.cms.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.dto.RwdCustomMappingMessageDTO;
import th.co.truecorp.commonapi.reward.cms.jpa.repository.CustomQueryMappingMessageRepository;

import java.util.Optional;

@Service
public class CustomMappingMessageService {

    @Autowired
    private CustomQueryMappingMessageRepository customQueryMappingMessageRepository;


    private static Logger log = LoggerFactory.getLogger(CustomMappingMessageService.class);

    public RwdCustomMappingMessageDTO mapCustomMessage(String customCode, String displayType, String lang, String action){
        RwdCustomMappingMessageDTO rwdCustomMappingMessage = new RwdCustomMappingMessageDTO();
        try {
            Optional<RwdCustomMappingMessageDTO> optional = customQueryMappingMessageRepository.findFirstByParams(action, customCode, lang, "Y", displayType);
            if(!optional.isEmpty()) {
                rwdCustomMappingMessage = optional.get();
            }

            log.info("get RwdCustomMappingMessage is success");
        }catch (Exception e){
            log.info("get RwdCustomMappingMessage is not success");
        }
        return rwdCustomMappingMessage;
    }

    public String getMappingMessage(String customCode, String displayType, String lang, String action){
        String description = null;
        try {
            Optional<RwdCustomMappingMessageDTO> optional = customQueryMappingMessageRepository.findFirstByParams(action, customCode, lang, "Y", displayType);
            if(!optional.isEmpty()){
                RwdCustomMappingMessageDTO rwdCustomMappingMessage = optional.get();
                description = rwdCustomMappingMessage.getDescription();
            }
//            log.info("get RwdCustomMappingMessage is success");
        }catch (Exception e){
            log.info("get RwdCustomMappingMessage is not success");
        }
        return description;
    }


}
