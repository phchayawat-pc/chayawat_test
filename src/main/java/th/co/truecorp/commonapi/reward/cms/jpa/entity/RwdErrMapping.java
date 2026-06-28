package th.co.truecorp.commonapi.reward.cms.jpa.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "rwd_err_mapping")
@Data
public class RwdErrMapping {

    @Id
    private Integer id;

    private String system_service_name;
    private String brand;
    private String http_code;
    private String err_code;
    private String system_error_code;
    private String err_message;
    private String custom_code;
    private String brand_code;
    private String created_by;
    private String created_date;
    private String modified_by;
    private String modified_date;

}

