package th.co.truecorp.commonapi.reward.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class ShelfSectionContentDetailRsp {
    private String sectionId;
    private String lang;
    private String displayTypeCode;
    private List<SectionItem> sectionItem;

    @Data
    public static class SectionItem{
       private String itemNo;
       private String itemName;
       private String itemDisplayName;
       private ItemImageList itemImageList;
       private String itemType;
       private String contentDetail;

       @Data
       public static class ItemImageList{
           private String imageIcon;
           private String image1x1;
           private String image3x2;
           private String image4x3;
           private String image16x9;
           private String image9x16;
       }
    }


}
