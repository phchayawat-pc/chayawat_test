package th.co.truecorp.commonapi.reward.model.endpoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import th.co.truecorp.commonapi.reward.common.utils.MessageDeserializer;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultItem {
    @JsonProperty("transactionDate")
    private String transactionDate;

    @JsonProperty("change")
    private String change;

    @JsonProperty("message")
    @JsonDeserialize(using = MessageDeserializer.class)
    private Object message;

    @JsonProperty("type")
    private String type;

    @JsonProperty("imageUrl")
    private String imageUrl;

    @JsonProperty("tansactionDate")
    private void setTransactionDateFromTypo(String transactionDate) {
        this.transactionDate = transactionDate;
    }
}
