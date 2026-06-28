package th.co.truecorp.commonapi.reward.cms.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import th.co.truecorp.commonapi.reward.dto.ShelfLayoutSectionMapperDto;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdLayout;

import java.util.List;

@Repository
public interface RwdLayoutRepository extends JpaRepository<RwdLayout, Integer>, JpaSpecificationExecutor<RwdLayout>, RwdLayoutRepositoryCustom {

    @Query(value = "select a.layout_id as layoutId " +
            "from rwd_layout a " +
            "where a.layout_status = :layoutStatus and a.valid_flag = :validFlag " +
            "and a.brand_code = :brandCode ", nativeQuery = true)
    String findRwdLayoutByBrand(
            @org.springframework.data.repository.query.Param("layoutStatus") String layoutStatus,
            @org.springframework.data.repository.query.Param("validFlag") String validFlag,
            @org.springframework.data.repository.query.Param("brandCode") String brandCode);

    @Query(value = "select a.layout_id as layoutId " +
            "from rwd_layout a " +
            "where a.layout_status = :layoutStatus and a.valid_flag = :validFlag " +
            "and a.brand_code = :brandCode " +
            "and a.charge_type_code = :chargeType ", nativeQuery = true)
    String findRwdLayoutByBrandAndChargeType(
            @org.springframework.data.repository.query.Param("layoutStatus") String layoutStatus,
            @org.springframework.data.repository.query.Param("validFlag") String validFlag,
            @org.springframework.data.repository.query.Param("brandCode") String brandCode,
            @org.springframework.data.repository.query.Param("chargeType") String chargeType);

    @Query(value = "select a.layout_id as layoutId " +
            "from rwd_layout a " +
            "where a.layout_status = :layoutStatus and a.valid_flag = :validFlag " +
            "and a.brand_code = :brandCode " +
            "and a.product_type_code = :productType ", nativeQuery = true)
    String findRwdLayoutByBrandAndProductType(
            @org.springframework.data.repository.query.Param("layoutStatus") String layoutStatus,
            @org.springframework.data.repository.query.Param("validFlag") String validFlag,
            @org.springframework.data.repository.query.Param("brandCode") String brandCode,
            @org.springframework.data.repository.query.Param("productType") String productType);

    @Query(value = "select a.layout_id as layoutId " +
            "from rwd_layout a " +
            "where a.layout_status = :layoutStatus and a.valid_flag = :validFlag " +
            "and a.brand_code = :brandCode " +
            "and a.product_type_code = :productType " +
            "and a.charge_type_code = :chargeType ", nativeQuery = true)
    String findRwdLayoutByBrandAndProductTypeAndChargeType(
            @org.springframework.data.repository.query.Param("layoutStatus") String layoutStatus,
            @org.springframework.data.repository.query.Param("validFlag") String validFlag,
            @org.springframework.data.repository.query.Param("brandCode") String brandCode,
            @org.springframework.data.repository.query.Param("productType") String productType,
            @org.springframework.data.repository.query.Param("chargeType") String chargeType);

    @Query(value = "select a.layout_id as layoutId, a.brand_code as brandCode" +
            ", a.product_type_code as productTypeCode, a.charge_type_code as chargeTypeCode" +
            ", b.seq_no as seqNo,b.section_id as sectionId, b.display_header_flag as displayHeaderFlag" +
            ", b.display_type_code as displayTypeCode, b.used_content_cms_flag as usedContentCmsFlag" +
            ", b.auto_slide as autoSlide, b.see_all_flag as seeAllFlag, b.goto_section_id as gotoSectionId" +
            ", b.start_date as startDate, b.end_date as endDate, b.template_code as templateCode" +
            ", e.section_name as sectionName, e.display_name_type as displayNameType" +
            ", c.section_display_name as sectionDisplayName" +
            ", c.display_image as sectionDisplayImage" +
            ", d.section_display_name as sectionDisplayNameEn" +
            ", d.display_image as sectionDisplayImageEn " +
            "from rwd_layout a " +
            "inner join rwd_layout_detail b on a.layout_id = b.layout_id " +
            "inner join rwd_section e on b.section_id = e.section_id " +
            "left outer join (select section_id,lang,section_display_name,display_image from rwd_section_display_name where lang = :lang) c on e.section_id= c.section_id " +
            "left outer join(select section_id,lang,section_display_name, display_image from rwd_section_display_name where lang = 'EN') d on e.section_id= d.section_id " +
            "where a.layout_id = :layoutId " + //L202407-002
            "order by b.priority", nativeQuery = true)
    List<ShelfLayoutSectionMapperDto> findRwdLayoutByLayoutId(
            @org.springframework.data.repository.query.Param("lang") String lang, @org.springframework.data.repository.query.Param("layoutId") String layoutId);
}
