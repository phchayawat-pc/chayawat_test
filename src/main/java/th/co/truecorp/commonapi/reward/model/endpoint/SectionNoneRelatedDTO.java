package th.co.truecorp.commonapi.reward.model.endpoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SectionNoneRelatedDTO {

    private String itemNo;
    private String itemName;
    private String itemDisplayName;
    private String imageIcon;
    private String image1x1;
    private String image3x2;
    private String image4x3;
    private String image16x9;
    private String image9x16;
    private String itemType;
    private String itemSubtype;
    private String shelfType;
    private String itemMapping;
    private String itemMapping2;

}
