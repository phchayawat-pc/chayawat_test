package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

@Data
public class ThumbnailListRsp {

    private String thumbnail1x1;
    private String thumbnail3x2;
    private String thumbnail4x3;
    private String thumbnail16x9;
    private String thumbnail9x16;


    @Override
    public String toString(){
        return "[ThumbnailListRsp] thumbnail1x1: " + getThumbnail1x1()
                +" ,thumbnail3x2: " + getThumbnail3x2()
                +" ,thumbnail4x3: " + getThumbnail4x3()
                +" ,thumbnail16x9: " + getThumbnail16x9()
                +" ,thumbnail9x16: " + getThumbnail9x16();
    }
}
