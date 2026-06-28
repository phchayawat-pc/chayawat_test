package th.co.truecorp.commonapi.reward.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.common.endpoint.CommonServiceEndpoint;
//import th.co.truecorp.commonapi.reward.jpa.repository.RwdLayoutRepository;
//import th.co.truecorp.commonapi.reward.jpa.repository.RwdSystemConfigRepository;


//import static th.co.truecorp.commonapi.reward.jpa.specification.RwdLayoutSpecification.hasLayoutWithDetails;

@Service
public class SectionDetailService {

    private static final Logger log = LoggerFactory.getLogger(CommonServiceEndpoint.class);
//
//    @Autowired
//    private LogContextService logContextService;
//
//    @Autowired
//    private ResultService resultService;
//
//    @Autowired
//    private RwdSystemConfigRepository rwdSystemConfigRepository;
//
//    @Autowired
//    private RwdLayoutRepository rwdLayoutRepository;
//
//    @Autowired
//    private EntityManager entityManager;
//
//    @EndpointLog(name = "SYSTEM.getSectionDetailService")
//    public EndpointResult getSectionDetailService(Map<String, Object> tv) {
//        EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
//        EndpointResult endpointResult = new EndpointResult();
//        RwdSystemConfig rwdSystemConfig;
//        try {
//            rwdSystemConfig = rwdSystemConfigRepository.findByConfigCodeAndConfigGroup(Constant.MAX_SLIDE, Constant.FIX);
//            System.out.println("rwdSystemConfig is "+rwdSystemConfig.getValue());
//            return endpointResult;
//
//        } catch (Exception exception) {
//            return resultService.getEndpointExceptionResult(tv, exception);
//        }
//    }
//
//    public List<LayoutDetailDTO> getLayoutDetails(String lang, String brandCode, String productType, String chargeType) {
//        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
//        Root<RwdLayout> root = query.from(RwdLayout.class);
//
//        // Join related entities
//        Join<RwdLayout, RwdLayoutDetail> layoutDetailJoin = root.join("rwdLayoutDetails");
//        Join<RwdLayoutDetail, RwdSection> sectionJoin = layoutDetailJoin.join("rwdSection", JoinType.INNER);
//        Join<RwdSection, RwdSectionDisplayName> displayNameJoinLang = sectionJoin.join("rwdSectionDisplayNames", JoinType.LEFT);
//        displayNameJoinLang.on(criteriaBuilder.equal(displayNameJoinLang.get("lang"), lang));
//
//        Join<RwdSection, RwdSectionDisplayName> displayNameJoinEn = sectionJoin.join("rwdSectionDisplayNames", JoinType.LEFT);
//        displayNameJoinEn.on(criteriaBuilder.equal(displayNameJoinEn.get("lang"), "EN"));
//
//        // Add predicates
//        List<Predicate> predicates = new ArrayList<>();
//        predicates.add(criteriaBuilder.equal(root.get("layoutStatus"), "PUBLISH"));
//        predicates.add(criteriaBuilder.equal(root.get("validFlag"), "Y"));
//        predicates.add(criteriaBuilder.equal(root.get("brandCode"), brandCode));
//
//        if (productType != null) {
//            predicates.add(criteriaBuilder.equal(root.get("productTypeCode"), productType));
//        } else {
//            predicates.add(criteriaBuilder.isNull(root.get("productTypeCode")));
//        }
//
//        if (chargeType != null) {
//            predicates.add(criteriaBuilder.equal(root.get("chargeTypeCode"), chargeType));
//        } else {
//            predicates.add(criteriaBuilder.isNull(root.get("chargeTypeCode")));
//        }
//
//        // Apply predicates
//        query.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
//
//        // Order by sequence number
//        query.orderBy(criteriaBuilder.asc(layoutDetailJoin.get("seqNo")));
//
//        // Select the fields you need
//        query.multiselect(
//                root.get("layoutId"),                // Long
//                root.get("brandCode"),               // String
//                root.get("productTypeCode"),         // String
//                root.get("chargeTypeCode"),          // String
//                layoutDetailJoin.get("seqNo"),       // Integer
//                layoutDetailJoin.get("sectionId"),   // Long
//                layoutDetailJoin.get("displayHeaderFlag"), // Boolean
//                layoutDetailJoin.get("displayTypeCode"),   // String
//                layoutDetailJoin.get("usedContentCmsFlag"),// Boolean
//                layoutDetailJoin.get("autoSlide"),         // Boolean
//                layoutDetailJoin.get("seeAllFlag"),        // Boolean
//                layoutDetailJoin.get("gotoSectionId"),     // Long
//                layoutDetailJoin.get("startDate"),         // Date
//                layoutDetailJoin.get("endDate"),           // Date
//                layoutDetailJoin.get("templateCode"),      // String
//                sectionJoin.get("sectionName"),            // String
//                sectionJoin.get("displayNameType"),        // String
//                displayNameJoinLang.get("sectionDisplayName"), // String
//                displayNameJoinLang.get("displayImage"),       // String
//                displayNameJoinEn.get("sectionDisplayName"),   // String
//                displayNameJoinEn.get("displayImage")          // String
//        );
//
//        List<Tuple> result = entityManager.createQuery(query).getResultList();
//
//        List<LayoutDetailDTO> layoutDetails = new ArrayList<>();
//        for (Tuple tuple : result) {
//            LayoutDetailDTO layoutDetail = new LayoutDetailDTO();
//            layoutDetail.setLayoutId(tuple.get(0, String.class));
//            layoutDetail.setBrandCode(tuple.get(1, String.class));
//            layoutDetail.setProductTypeCode(tuple.get(2, String.class));
//            layoutDetail.setChargeTypeCode(tuple.get(3, String.class));
//            layoutDetail.setSeqNo(tuple.get(4, Integer.class));
//            layoutDetail.setSectionId(tuple.get(5, String.class));
//            layoutDetail.setDisplayHeaderFlag(tuple.get(6, String.class));
//            layoutDetail.setDisplayTypeCode(tuple.get(7, String.class));
//            layoutDetail.setUsedContentCmsFlag(tuple.get(8, String.class));
//            layoutDetail.setAutoSlide(tuple.get(9, String.class));
//            layoutDetail.setSeeAllFlag(tuple.get(10, String.class));
//            layoutDetail.setGotoSectionId(tuple.get(11, String.class));
//            layoutDetail.setStartDate(tuple.get(12, Timestamp.class));
//            layoutDetail.setEndDate(tuple.get(13, Timestamp.class));
//            layoutDetail.setTemplateCode(tuple.get(14, String.class));
//            layoutDetail.setSectionName(tuple.get(15, String.class));
//            layoutDetail.setDisplayNameType(tuple.get(16, String.class));
//            layoutDetail.setSectionDisplayNameLang(tuple.get(17, String.class));
//            layoutDetail.setDisplayImageLang(tuple.get(18, String.class));
//            layoutDetail.setSectionDisplayNameEn(tuple.get(19, String.class));
//            layoutDetail.setDisplayImageEn(tuple.get(20, String.class));
//
//            layoutDetails.add(layoutDetail);
//        }
//
//        return layoutDetails;
//    }

}
