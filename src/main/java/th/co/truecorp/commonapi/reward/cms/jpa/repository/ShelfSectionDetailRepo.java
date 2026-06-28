package th.co.truecorp.commonapi.reward.cms.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSectionDetail;
import th.co.truecorp.commonapi.reward.dto.ShelfSectionAllDataMapperDto;

import java.util.List;

@Repository
public interface ShelfSectionDetailRepo extends JpaRepository<RwdSectionDetail, Integer> {
    @Query(value = "select  a.seq_no,a.item_type_code,a.shelf_type_code," +
            "        a.item_mapping,a.item_mapping2,a.item_name," +
            "        (select item_display_name" +
            "         from   rwd_section_detail_display_name s" +
            "         where  s.section_id = a.section_id" +
            "                and s.seq_no = a.seq_no" +
            "                and s.lang = :lang) as item_display_name," +
            "        (select item_display_name" +
            "         from   rwd_section_detail_display_name s" +
            "         where  s.section_id = a.section_id" +
            "                and s.seq_no = a.seq_no" +
            "                and s.lang = 'EN') as item_display_name_en" +
            " from    rwd_section_detail a " +
            " where   a.section_id = :sectionId" +
            " order by a.seq_no;", nativeQuery = true)
    List<ShelfSectionAllDataMapperDto> findRwdSectionDetailBySectionId(
            @Param("sectionId") String sectionId,
            @Param("lang") String lang);
}
