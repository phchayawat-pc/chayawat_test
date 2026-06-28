package th.co.truecorp.commonapi.reward.dto.builder;

import th.co.truecorp.commonapi.reward.dto.LayoutSectionDTO;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdLayout;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdLayoutDetail;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSectionDisplayName;
import th.co.truecorp.commonlib.constant.ComnConst;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class LayoutSectionDTOBuilder {

    private LayoutSectionDTO layoutSectionDTO;

    public LayoutSectionDTOBuilder() {
        this.layoutSectionDTO = new LayoutSectionDTO();
    }

    public LayoutSectionDTOBuilder(Map<String, Object> tv, RwdLayout entity, RwdLayoutDetail entityDetail) {

        this.layoutSectionDTO = new LayoutSectionDTO();
        setLayoutId(entity.getLayoutId())
                .setBrandCode(entity.getBrandCode())
                .setProductTypeCode(entity.getProductTypeCode())
                .setChargeTypeCode(entity.getChargeTypeCode())
                .setSeqNo(entityDetail.getSeqNo())
                .setDisplayHeaderFlag(entityDetail.getDisplayHeaderFlag())
                .setDisplayTypeCode(entityDetail.getDisplayTypeCode())
                .setUsedContentCmsFlag(entityDetail.getUsedContentCmsFlag())
                .setAutoSlide(entityDetail.getAutoSlide())
                .setSeeAllFlag(entityDetail.getSeeAllFlag())
                .setGotoSectionId(entityDetail.getGotoSectionId())
                .setStartDate(entityDetail.getStartDate())
                .setEndDate(entityDetail.getEndDate())
                .setTemplateCode(entityDetail.getTemplateCode())
                .setSectionName(entityDetail.getRwdSection().getSectionName())
                .setDisplayNameType(entityDetail.getRwdSection().getDisplayNameType())
                .setSectionDisplayName(tv.get(ComnConst.KEY_LANGUAGE).toString(), entityDetail.getRwdSection().getRwdSectionDisplayNames())
                .setSectionDisplayImage(tv.get(ComnConst.KEY_LANGUAGE).toString(), entityDetail.getRwdSection().getRwdSectionDisplayNames())
                .setSectionDisplayNameEn(entityDetail.getRwdSection().getRwdSectionDisplayNames())
                .setSectionDisplayImageEn(entityDetail.getRwdSection().getRwdSectionDisplayNames())
        ;
    }

    public static LayoutSectionDTOBuilder getLayoutSectionDTOBuilder() {
        return new LayoutSectionDTOBuilder();
    }

    public static LayoutSectionDTOBuilder getLayoutSectionDTOBuilder(Map<String, Object> tv, RwdLayout entity, RwdLayoutDetail layoutDetail) {
        return new LayoutSectionDTOBuilder(tv, entity, layoutDetail);
    }


    public static LayoutSectionDTO getLayoutSectionDTO(Map<String, Object> tv, RwdLayout layout, RwdLayoutDetail layoutDetail) {
        if (layout == null) {
            return null;
        }

        return new LayoutSectionDTOBuilder(tv, layout, layoutDetail).build();
    }

    public static List<LayoutSectionDTO> getLayoutSectionDTO(Map<String, Object> tv, Collection<RwdLayout> layouts) {
        if (layouts == null) {
            return null;
        }

        List<LayoutSectionDTO> dtos = new ArrayList<>();
        for(RwdLayout r : layouts){
            for(RwdLayoutDetail r2 : r.getRwdLayoutDetails()){
                dtos.add(LayoutSectionDTOBuilder.getLayoutSectionDTO(tv,r,r2));
            }
        }
//        layouts.forEach(r -> dtos.add(LayoutSectionDTOBuilder.getLayoutSectionDTO(tv,r)));

        return dtos;
    }

    /**
     *
     * builder
     */
    public LayoutSectionDTOBuilder setLayoutId(String layoutId) {
        this.layoutSectionDTO.setLayoutId(layoutId);
        return this;
    }

    public LayoutSectionDTOBuilder setBrandCode(String brandCode) {
        this.layoutSectionDTO.setBrandCode(brandCode);
        return this;
    }

    public LayoutSectionDTOBuilder setProductTypeCode(String productTypeCode) {
        this.layoutSectionDTO.setProductTypeCode(productTypeCode);
        return this;
    }

    public LayoutSectionDTOBuilder setChargeTypeCode(String chargeTypeCode) {
        this.layoutSectionDTO.setChargeTypeCode(chargeTypeCode);
        return this;
    }

    public LayoutSectionDTOBuilder setSeqNo(int seqNo) {
        this.layoutSectionDTO.setSeqNo(seqNo);
        return this;
    }

    public LayoutSectionDTOBuilder setSectionId(String sectionId) {
        this.layoutSectionDTO.setSectionId(sectionId);
        return this;
    }

    public LayoutSectionDTOBuilder setDisplayHeaderFlag(String displayHeaderFlag) {
        this.layoutSectionDTO.setDisplayHeaderFlag(displayHeaderFlag);
        return this;
    }

    public LayoutSectionDTOBuilder setDisplayTypeCode(String displayTypeCode) {
        this.layoutSectionDTO.setDisplayTypeCode(displayTypeCode);
        return this;
    }

    public LayoutSectionDTOBuilder setUsedContentCmsFlag(String usedContentCmsFlag) {
        this.layoutSectionDTO.setUsedContentCmsFlag(usedContentCmsFlag);
        return this;
    }

    public LayoutSectionDTOBuilder setAutoSlide(String autoSlide) {
        this.layoutSectionDTO.setAutoSlide(autoSlide);
        return this;
    }

    public LayoutSectionDTOBuilder setSeeAllFlag(String seeAllFlag) {
        this.layoutSectionDTO.setSeeAllFlag(seeAllFlag);
        return this;
    }

    public LayoutSectionDTOBuilder setGotoSectionId(String gotoSectionId) {
        this.layoutSectionDTO.setGotoSectionId(gotoSectionId);
        return this;
    }

    public LayoutSectionDTOBuilder setStartDate(Timestamp startDate) {
        if(startDate != null) {
            this.layoutSectionDTO.setStartDate(startDate.toString());
        }
        return this;
    }

    public LayoutSectionDTOBuilder setEndDate(Timestamp endDate) {
        if(endDate != null) {
            this.layoutSectionDTO.setEndDate(endDate.toString());
        }
        return this;
    }

    public LayoutSectionDTOBuilder setTemplateCode(String templateCode) {
        this.layoutSectionDTO.setTemplateCode(templateCode);
        return this;
    }

    public LayoutSectionDTOBuilder setSectionName(String sectionName) {
        this.layoutSectionDTO.setSectionName(sectionName);
        return this;
    }

    public LayoutSectionDTOBuilder setDisplayNameType(String displayNameType) {
        this.layoutSectionDTO.setDisplayNameType(displayNameType);
        return this;
    }

    public LayoutSectionDTOBuilder setSectionDisplayName(String lang, List<RwdSectionDisplayName> sectionDisplayName) {
        for(RwdSectionDisplayName sdn : sectionDisplayName){
            if(sdn.getLang().equals(lang)){
                this.layoutSectionDTO.setSectionDisplayName(sdn.getSectionDisplayName());
            }
        }
        return this;
    }

    public LayoutSectionDTOBuilder setSectionDisplayImage(String lang, List<RwdSectionDisplayName> sectionDisplayName) {
        for(RwdSectionDisplayName sdn : sectionDisplayName){
            if(sdn.getLang().equals(lang)){
                this.layoutSectionDTO.setSectionDisplayImage(sdn.getDisplayImage());
            }
        }
        return this;
    }

    public LayoutSectionDTOBuilder setSectionDisplayNameEn(List<RwdSectionDisplayName> sectionDisplayName) {
        for(RwdSectionDisplayName sdn : sectionDisplayName){
            if(sdn.getLang().equals("EN")){
                this.layoutSectionDTO.setSectionDisplayNameEn(sdn.getSectionDisplayName());
            }
        }
        return this;
    }

    public LayoutSectionDTOBuilder setSectionDisplayImageEn(List<RwdSectionDisplayName> sectionDisplayName) {
        for(RwdSectionDisplayName sdn : sectionDisplayName){
            if(sdn.getLang().equals("EN")){
                this.layoutSectionDTO.setSectionDisplayImageEn(sdn.getDisplayImage());
            }
        }
        return this;
    }

    public LayoutSectionDTO build() {
        return this.layoutSectionDTO;
    }

}
