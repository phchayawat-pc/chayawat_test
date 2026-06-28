package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

@Data
public class ThumbnailRsp {
    private String highlight16x9;
    private String logo;


    @Override
    public String toString(){
        return "[ThumbnailRsp] thumbnail: " + getHighlight16x9()
                +" ,logo: " + getLogo();
    }
}
