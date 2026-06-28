package th.co.truecorp.commonapi.reward.model.mapper;

import lombok.Data;
import th.co.truecorp.commonapi.reward.dto.ShelfSectionHeaderMapperDto;

@Data
public class ShelfSectionHeaderMapperModel implements ShelfSectionHeaderMapperDto {
    private String section_id;
    private Integer priority;
    private Integer seq_no;
    private String item_icon;
    private String item_name;
    private String item_image1x1;
    private String item_image4x3;
    private String item_image16x9;
    private String item_image9x16;
    private String item_type_code;
    private String item_subtype;
    private String shelf_type_code;
    private String item_mapping;
    private String item_mapping2;
    private String dummy_flag;
    private String item_display_name;
    private String item_display_name_en;

    public ShelfSectionHeaderMapperModel(String section_id, Integer priority, Integer seq_no,String item_name, String item_icon,
                     String item_image1x1, String item_image4x3, String item_image16x9,
                     String item_image9x16, String item_type_code, String item_subtype,
                     String shelf_type_code, String item_mapping, String item_mapping2, String dummy_flag,
                                         String item_display_name, String item_display_name_en) {
        this.section_id = section_id;
        this.priority = priority;
        this.seq_no = seq_no;
        this.item_icon = item_icon;
        this.item_name = item_name;
        this.item_image1x1 = item_image1x1;
        this.item_image4x3 = item_image4x3;
        this.item_image16x9 = item_image16x9;
        this.item_image9x16 = item_image9x16;
        this.item_type_code = item_type_code;
        this.item_subtype = item_subtype;
        this.shelf_type_code = shelf_type_code;
        this.item_mapping = item_mapping;
        this.item_mapping2 = item_mapping2;
        this.dummy_flag = dummy_flag;
        this.item_display_name = item_display_name;
        this.item_display_name_en = item_display_name_en;
    }
    @Override
    public String toString() {
        return "ShelfSectionHeaderMapper{" +
                "sectionId='" + section_id + '\'' +
                ", priority=" + priority +
                ", seqNo=" + seq_no +
                ", itemIcon='" + item_icon + '\'' +
                ", itemImage1x1='" + item_image1x1 + '\'' +
                ", itemImage4x3='" + item_image4x3 + '\'' +
                ", itemImage16x9='" + item_image16x9 + '\'' +
                ", itemImage9x16='" + item_image9x16 + '\'' +
                ", itemTypeCode='" + item_type_code + '\'' +
                ", itemSubtype='" + item_subtype + '\'' +
                ", shelfTypeCode='" + shelf_type_code + '\'' +
                ", itemMapping='" + item_mapping + '\'' +
                ", itemMapping2='" + item_mapping2 + '\'' +
                ", dummyFlag='" + dummy_flag + '\'' +
                ", item_display_name='" + item_display_name + '\'' +
                ", item_display_name_en='" + item_display_name_en + '\'' +
                '}';
    }
}

