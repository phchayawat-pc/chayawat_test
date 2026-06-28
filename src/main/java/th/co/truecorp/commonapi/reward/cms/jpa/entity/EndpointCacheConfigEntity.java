package th.co.truecorp.commonapi.reward.cms.jpa.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "profile_endpoint_cache_config")
public class EndpointCacheConfigEntity implements Serializable {

    /** Primary key. */
    protected static final String PK = "id";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "srv_id", nullable = false)
    private Integer srvId;

    @Column(name = "period_time", nullable = false)
    private Integer periodTime;

    @Column(name = "remark", length = 2000)
    private String remark;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "updated_by", nullable = false, length = 50)
    private String updatedBy;

    @Column(name = "created_dttm")
    private LocalDateTime createdDttm;

    @Column(name = "updated_dttm")
    private LocalDateTime updatedDttm;
}
