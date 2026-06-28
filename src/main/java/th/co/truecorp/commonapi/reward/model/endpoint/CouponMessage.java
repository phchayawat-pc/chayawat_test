package th.co.truecorp.commonapi.reward.model.endpoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CouponMessage {
    private String Code;
    private String Name;
    private int Points;
    private String UsedDate;
    private String Batch;
    private String PartitionKey;
    private String RowKey;
    private String Timestamp;
    private String ETag;
}
