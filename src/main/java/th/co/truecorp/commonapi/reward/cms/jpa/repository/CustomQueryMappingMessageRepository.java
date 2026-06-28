package th.co.truecorp.commonapi.reward.cms.jpa.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import th.co.truecorp.commonapi.reward.cms.jpa.dto.RwdCustomMappingMessageDTO;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdCustomMappingMessage;

import java.util.Optional;

@Repository
public interface CustomQueryMappingMessageRepository extends JpaRepository<RwdCustomMappingMessage, Integer> {

//    @Query("SELECT new th.co.truecorp.commonapi.reward.cms.jpa.dto.RwdCustomMappingMessageDTO(rcmm.custom_code, rcmm.lang, rcmm.display_type, rcmm.action, rcmm.message, rcmm.description) " +
//            "FROM RwdCustomMappingMessage rcmm " +
//            "WHERE rcmm.action = :action " +
//            "AND rcmm.custom_code = :customCode " +
//            "AND rcmm.lang = :lang " +
//            "AND rcmm.static_flag = :staticFlag " +
//            "AND rcmm.display_type = :displayType")
//    Optional<RwdCustomMappingMessageDTO> findFirstByParams(@Param("action") String action,
//                                                           @Param("customCode") String customCode,
//                                                           @Param("lang") String lang,
//                                                           @Param("staticFlag") String staticFlag,
//                                                           @Param("displayType") String displayType );




}

