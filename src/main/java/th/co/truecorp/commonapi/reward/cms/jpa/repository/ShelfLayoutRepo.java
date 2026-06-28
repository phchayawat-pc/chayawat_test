package th.co.truecorp.commonapi.reward.cms.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th.co.truecorp.commonapi.reward.dto.ShelfLayoutSectionMapperDto;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdLayout;

import java.util.List;

@Repository
public interface ShelfLayoutRepo extends JpaRepository<RwdLayout, Integer> {

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
            "where a.layout_status = 'PUBLISH' and a.valid_flag = 'Y' " +
            "and a.brand_code = :brandCode " +
            "order by b.seq_no", nativeQuery = true)
    List<ShelfLayoutSectionMapperDto> findRwdLayoutByBrand(
            @Param("lang") String lang, @Param("brandCode") String brandCode);

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
            "where a.layout_status = 'PUBLISH' and a.valid_flag = 'Y' " +
            "and a.brand_code = :brandCode " +
            "and a.productTypeCode = :productType " +
            "order by b.seq_no", nativeQuery = true)
    List<ShelfLayoutSectionMapperDto> findRwdLayoutByBrandAndProductType(
            @Param("lang") String lang, @Param("brandCode") String brandCode, @Param("productType") String productType);

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
            "where a.layout_status = 'PUBLISH' and a.valid_flag = 'Y' " +
            "and a.brand_code = :brandCode " +
            "and a.chargeTypeCode = :chargeType " +
            "order by b.seq_no", nativeQuery = true)
    List<ShelfLayoutSectionMapperDto> findRwdLayoutByBrandAndChargeType(
            @Param("lang") String lang, @Param("brandCode") String brandCode, @Param("chargeType") String chargeType);

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
            "where a.layout_status = 'PUBLISH' and a.valid_flag = 'Y' " +
            "and a.brand_code = :brandCode " +
            "and a.productTypeCode = :productType " +
            "and a.chargeTypeCode = :chargeType " +
            "order by b.seq_no", nativeQuery = true)
    List<ShelfLayoutSectionMapperDto> findRwdLayoutByBrandAndProductTypeAndChargeType(
            @Param("lang") String lang, @Param("brandCode") String brandCode, @Param("productType") String productType, @Param("chargeType") String chargeType);

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
            "where a.layout_id in (select value from rwd_system_config where config_code = 'LAYOUT_DEFAULT' and config_group = :brandCode)" +
            "order by b.seq_no", nativeQuery = true)
    List<ShelfLayoutSectionMapperDto> findRwdLayoutByInBrand(
            @Param("lang") String lang, @Param("brandCode") String brandCode);

}
