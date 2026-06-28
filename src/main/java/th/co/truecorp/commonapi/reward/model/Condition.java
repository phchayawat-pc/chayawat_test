package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

@Data
public class Condition {
    private String type;
    private String linkType;
    private String message;
    private String url;
    private String urlName;


    @Override
    public String toString(){
        return "[Condition] type: " + getType()
                +" ,linkType: " + getLinkType()
                +" ,message: " + getMessage()
                +" ,url: " + getUrl()
                +" ,urlName: " + getUrlName();
    }
}
