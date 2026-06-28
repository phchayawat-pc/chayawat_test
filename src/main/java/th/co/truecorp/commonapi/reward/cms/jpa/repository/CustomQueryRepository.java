package th.co.truecorp.commonapi.reward.cms.jpa.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import th.co.truecorp.commonapi.reward.cms.jpa.dto.RwdErrMappingDTO;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdErrMapping;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomQueryRepository extends JpaRepository<RwdErrMapping, Integer> {

    @Query("SELECT DISTINCT new th.co.truecorp.commonapi.reward.cms.jpa.dto.RwdErrMappingDTO(rem.system_service_name, rem.err_code, rem.custom_code, rcmm.message, rem.http_code, rem.brand_code, rcmm.lang, rcmm.description, rem.system_error_code, rcmm.display_type) " +
            "FROM RwdErrMapping rem " +
            "JOIN RwdCustomMappingMessage rcmm ON rem.custom_code = rcmm.custom_code " +
            "WHERE rcmm.action = :action " +
            "AND rem.brand_code = :brand " +
            "AND rem.err_code = :errCode " +
            "AND rem.system_error_code = :busErr " +
            "AND rcmm.lang = :lang " +
            "AND rcmm.display_type = :displayType")
    Optional<RwdErrMappingDTO> findFirstByParams(@Param("action") String action,
                                                 @Param("brand") String brand,
                                                 @Param("errCode") String errCode,
                                                 @Param("lang") String lang,
                                                 @Param("busErr") String busErr,
                                                 @Param("displayType") String displayType );

    @Query("SELECT new th.co.truecorp.commonapi.reward.cms.jpa.dto.RwdErrMappingDTO(rcmm.action, rem.err_code, rem.custom_code, rcmm.message, rem.http_code, rem.brand_code, rcmm.lang, rcmm.description, rem.system_error_code, rcmm.display_type) " +
            "FROM RwdErrMapping rem " +
            "JOIN RwdCustomMappingMessage rcmm ON rem.custom_code = rcmm.custom_code")
    List<RwdErrMappingDTO> findAllMappings();


}

