package th.co.truecorp.commonapi.reward.cms.jpa.entity;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "rwd_system_config", uniqueConstraints = @UniqueConstraint(columnNames = {"config_code", "config_group"}))
@Getter
@Setter
public class RwdSystemConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Integer id;

    @Column(name = "config_code", length = 25)
    private String configCode;

    @Column(name = "config_group", length = 25)
    private String configGroup;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "value", length = 100)
    private String value;

    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    @Column(name = "modified_date")
    private Timestamp modifiedDate;

    @Override
    public String toString(){
        return "[RwdSystemConfig] configCode=" + getConfigCode()
                +" ,configGroup=" + getConfigGroup()
                +" ,value=" + getValue()
                +" ,description=" + getDescription();
    }
}
