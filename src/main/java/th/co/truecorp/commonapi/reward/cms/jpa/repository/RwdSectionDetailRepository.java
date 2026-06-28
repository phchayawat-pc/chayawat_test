package th.co.truecorp.commonapi.reward.cms.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSectionDetail;
import th.co.truecorp.commonapi.reward.dto.*;

import java.util.List;

@Repository
public interface RwdSectionDetailRepository extends JpaRepository<RwdSectionDetail, Integer>, JpaSpecificationExecutor<RwdSectionDetail> {

    @Query(value = "select a.section_id, a.priority, a.seq_no, " +
            "a.item_name, a.item_icon, a.item_image1x1, " +
            "a.item_image4x3, a.item_image16x9, " +
            "a.item_image9x16, a.item_type_code, " +
            "a.item_subtype, a.shelf_type_code, " +
            "a.item_mapping, a.item_mapping2, " +
            "b.content as content_en , c.content as content_lang " +
            "from rwd_section_detail a " +
            "left outer join (select section_id,seq_no,content from rwd_section_detail_content where lang = 'EN') b on a.section_id = b.section_id and a.seq_no = b.seq_no " +
            "left outer join (select section_id,seq_no,content from rwd_section_detail_content where lang = :lang) c on a.section_id = c.section_id and a.seq_no = c.seq_no " +
            "where a.section_id = :sectionId " +
            "order by a.priority", nativeQuery = true)
    List<ShelfSectionContentDetailDto> findRwdSectionDetailDTOBySectionId(
            @org.springframework.data.repository.query.Param("lang") String lang, @org.springframework.data.repository.query.Param("sectionId") String sectionId);

    @Query(value = "select  a.seq_no, a.item_type_code, a.shelf_type_code, " +
            "a.item_mapping, a.item_mapping2, a.item_name, a.item_subtype," +
            "a.item_icon, a.item_image1x1, a.item_image4x3, a.item_image16x9, a.item_image9x16, " +
            "(select item_display_name from rwd_section_detail_display_name s where  s.section_id = a.section_id and s.seq_no = a.seq_no and s.lang = :lang) as item_display_name, " +
            "(select item_display_name from rwd_section_detail_display_name s where  s.section_id = a.section_id and s.seq_no = a.seq_no and s.lang = 'EN') as item_display_name_en " +
            "from rwd_section_detail a " +
            "where a.section_id = :sectionId " +
            "order by a.priority", nativeQuery = true)
    List<ShelfSectionAllDataDto> findShelfSectionAllDataDtoBySectionId(
            @org.springframework.data.repository.query.Param("lang") String lang, @org.springframework.data.repository.query.Param("sectionId") String sectionId);

    @Query(value = "select  seq_no,item_type_code,shelf_type_code, " +
            "item_mapping,item_mapping2 " +
            "from rwd_section_detail " +
            "where section_id = :sectionId " +
            "and item_mapping = :itemMapping " +
            "order by seq_no", nativeQuery = true)
    List<ShelfTemplateDetailDto> findShelfTemplateDetailDtoBySectionIdAndItemMapping(
            @org.springframework.data.repository.query.Param("sectionId") String sectionId, @org.springframework.data.repository.query.Param("itemMapping") String itemMapping);

    @Query(value = "select  seq_no,item_type_code,shelf_type_code, " +
            "item_mapping,item_mapping2 " +
            "from rwd_section_detail " +
            "where section_id = :sectionId " +
            "and seq_no = (select min(seq_no) " +
            "from rwd_section_detail " +
            "where section_id = :sectionId " +
            "group by section_id)", nativeQuery = true)
    List<ShelfTemplateDetailDto> findShelfTemplateDetailDtoBySectionId(
            @org.springframework.data.repository.query.Param("sectionId") String sectionId);

    @Query(value = "select priority,item_type_code,shelf_type_code, " +
            "item_mapping,item_mapping2 " +
            "from rwd_section_detail " +
            "where section_id = :sectionId " +
            "order by priority", nativeQuery = true)
    List<ShelfSectionDetailDto> findShelfSectionDetailDtoBySectionId(
            @org.springframework.data.repository.query.Param("sectionId") String sectionId);

    @Query(value = "select row_number() OVER (order by rsd.priority) as r_no , rsd.priority, rsd.item_mapping " +
            "from ( select sd.priority, sd.item_mapping from rwd_section_detail sd " +
            "where sd.section_id = :sectionId order by sd.priority ) rsd " +
            "order by r_no", nativeQuery = true)
    List<ShelfSectionDetailDto2> findShelfSectionDetailDto2BySectionId(
            @org.springframework.data.repository.query.Param("sectionId") String sectionId);

}
