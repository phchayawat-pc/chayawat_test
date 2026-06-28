package th.co.truecorp.commonapi.reward.cms.jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
//import th.co.truecorp.commonapi.reward.dto.LayoutDetailDTO;

@Repository
public class LayoutDetailRepository {

//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @SuppressWarnings("unchecked")
//    public List<LayoutDetailDTO> getLayoutDetails(String lang, String brandCode, String productType, String chargeType) {
//        String sql = "SELECT a.layout_id, a.brand_code, a.product_type_code, a.charge_type_code, " +
//                "b.seq_no, b.section_id, b.display_header_flag, b.display_type_code, " +
//                "b.used_content_cms_flag, b.auto_slide, b.see_all_flag, b.goto_section_id, " +
//                "b.start_date, b.end_date, b.template_code, e.section_name, e.display_name_type, " +
//                "c.section_display_name AS section_display_name, c.display_image AS section_display_image, " +
//                "d.section_display_name AS section_display_name_en, d.display_image AS section_display_image_en " +
//                "FROM rwd_layout a " +
//                "INNER JOIN rwd_layout_detail b ON a.layout_id = b.layout_id " +
//                "INNER JOIN rwd_section e ON b.section_id = e.section_id " +
//                "LEFT OUTER JOIN (SELECT section_id, lang, section_display_name, display_image " +
//                "FROM rwd_section_display_name WHERE lang = :lang) c ON e.section_id = c.section_id " +
//                "LEFT OUTER JOIN (SELECT section_id, lang, section_display_name, display_image " +
//                "FROM rwd_section_display_name WHERE lang = 'EN') d ON e.section_id = d.section_id " +
//                "WHERE a.layout_status = 'PUBLISH' AND a.valid_flag = 'Y' " +
//                "AND a.brand_code = :brandCode AND a.product_type_code = :productType AND a.charge_type_code = :chargeType " +
//                "ORDER BY b.seq_no";
//
//        Query query = entityManager.createNativeQuery(sql, "LayoutDetailMapping");
//        query.setParameter("lang", lang);
//        query.setParameter("brandCode", brandCode);
//        query.setParameter("productType", productType);
//        query.setParameter("chargeType", chargeType);
//
//        return query.getResultList();
//    }
}
