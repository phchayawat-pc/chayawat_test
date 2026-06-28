package th.co.truecorp.commonapi.reward.cms.jpa.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "rwd_custom_mapping_message")
@Data
public class RwdCustomMappingMessage {

    @Id
    private Integer id;

    private String custom_code;
    private String lang;
    private String static_flag;
    private String display_type;
    private String action;
    private String message;
    private String description;
    private String created_by;
    private String created_date;
    private String modified_by;
    private String modified_date;

}
