package th.co.truecorp.commonapi.reward.cms.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSectionHighlight;
import th.co.truecorp.commonapi.reward.dto.ShelfSectionHighlightDto;

import java.util.List;

@Repository
public interface RwdSectionHighlightRepository extends JpaRepository<RwdSectionHighlight, Integer>, JpaSpecificationExecutor<RwdSectionHighlight> {

    @Query(value = "SELECT id, section_id, seq_no, item_image1x1, " +
            "item_image4x3, item_image16x9, item_image9x16, item_type_code, " +
            "item_subtype, shelf_type_code, item_mapping, item_mapping2 " +
            "FROM rwd_section_highlight " +
            "WHERE section_id = :sectionId", nativeQuery = true)
    List<ShelfSectionHighlightDto> findBySectionId(@org.springframework.data.repository.query.Param("sectionId") String sectionId);

    @Query(value = "SELECT id, section_id, seq_no, item_image1x1, " +
            "item_image4x3, item_image16x9, item_image9x16, item_type_code, " +
            "item_subtype, shelf_type_code, item_mapping, item_mapping2 " +
            "FROM rwd_section_highlight " +
            "WHERE section_id = :sectionId " +
            "AND item_mapping = :itemMapping "+
            "order by seq_no", nativeQuery = true)
    List<ShelfSectionHighlightDto> findBySectionIdAndItemMapping(@org.springframework.data.repository.query.Param("sectionId") String sectionId,
                                                                 @org.springframework.data.repository.query.Param("itemMapping") String itemMapping);

    @Query(value = "SELECT rsh.id, rsh.section_id, rsh.seq_no, rsh.item_image1x1, " +
            "rsh.item_image4x3, rsh.item_image16x9, rsh.item_image9x16, rsh.item_type_code, " +
            "rsh.item_subtype, rsh.shelf_type_code, rsh.item_mapping, rsh.item_mapping2 " +
            "FROM ( SELECT row_number() over (order by sh.seq_no) as r_no, sh.id, sh.section_id, sh.seq_no, sh.item_image1x1, " +
            "sh.item_image4x3, sh.item_image16x9, sh.item_image9x16, sh.item_type_code, " +
            "sh.item_subtype, sh.shelf_type_code, sh.item_mapping, sh.item_mapping2 " +
            "FROM public.rwd_section_highlight sh " +
            "where sh.section_id = :sectionId ORDER BY sh.seq_no ) rsh WHERE rsh.r_no = :rowNum " +
            "order by rsh.seq_no", nativeQuery = true)
    List<ShelfSectionHighlightDto> findBySectionIdAndRowNum(@org.springframework.data.repository.query.Param("sectionId") String sectionId,
                                                            @org.springframework.data.repository.query.Param("rowNum") Integer rowNum);
}
