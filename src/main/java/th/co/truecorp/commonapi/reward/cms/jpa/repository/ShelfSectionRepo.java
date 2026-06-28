package th.co.truecorp.commonapi.reward.cms.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSection;
import th.co.truecorp.commonapi.reward.dto.ShelfSectionHeaderMapperDto;

import java.util.List;

@Repository
public interface ShelfSectionRepo extends JpaRepository<RwdSection, Integer> {
    @Query(value = "select  b.section_id,b.priority,b.seq_no,b.item_name," +
            "        b.item_icon,b.item_image1x1," +
            "        b.item_image4x3,b.item_image16x9," +
            "        b.item_image9x16,b.item_type_code," +
            "        b.item_subtype,b.shelf_type_code," +
            "        b.item_mapping,b.item_mapping2,b.dummy_flag," +
            "        (select item_display_name" +
            "         from   rwd_section_detail_display_name s" +
            "         where  s.section_id = b.section_id" +
            "                and s.seq_no = b.seq_no" +
            "                and s.lang = :lang) as item_display_name," +
            "        (select item_display_name" +
            "         from   rwd_section_detail_display_name s" +
            "         where  s.section_id = b.section_id" +
            "                and s.seq_no = b.seq_no" +
            "                and s.lang = 'EN') as item_display_name_en " +
            " from    rwd_section a inner join" +
            "        rwd_section_detail b on a.section_id = b.section_id" +
            " where   a.section_id = :sectionId" +
            " order by b.priority", nativeQuery = true)
    List<ShelfSectionHeaderMapperDto> findRwdSectionEntitiesById(
            @Param("sectionId") String sectionId,
            @Param("lang") String lang);

    @Query(value = "select distinct rs.section_id from rwd_layout rl " +
            " inner join rwd_layout_detail rld on rl.layout_id = rld.layout_id " +
            " inner join rwd_section rs on rld.section_id = rs.section_id " +
            " where rl.layout_id = :layoutId", nativeQuery = true)
    List<String> findSectionIdByLayoutId(@Param("layoutId") String layoutId);

}
