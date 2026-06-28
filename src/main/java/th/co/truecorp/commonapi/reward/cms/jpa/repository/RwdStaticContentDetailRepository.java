package th.co.truecorp.commonapi.reward.cms.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdStaticContentDetail;
import th.co.truecorp.commonapi.reward.dto.ProfileRwdStaticContentDetailDto;

import java.util.List;

@Repository
public interface RwdStaticContentDetailRepository extends JpaRepository<RwdStaticContentDetail, Integer>, JpaSpecificationExecutor<RwdStaticContentDetail> {

    @Query(value = "SELECT content_id, seq_no, lang, customer_grade, topic, description, image " +
            "FROM rwd_static_content_detail " +
            "where content_id = :contentId and lang = :lang " +
            "order by seq_no", nativeQuery = true)
    List<ProfileRwdStaticContentDetailDto> findContentIdAndLang(
            @org.springframework.data.repository.query.Param("contentId") String contentId,
            @org.springframework.data.repository.query.Param("lang") String lang);
}
