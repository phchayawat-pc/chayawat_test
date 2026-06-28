package th.co.truecorp.commonapi.reward.model.endpoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
    @JsonProperty("TransactionDate")
    private String transactionDate;

    @JsonProperty("DtacId")
    private String dtacId;

    @JsonProperty("Success")
    private Boolean success;

    @JsonProperty("ProductId")
    private String productId;

    @JsonProperty("Amount")
    private Double amount;

    @JsonProperty("InputAmount")
    private Double inputAmount;

    @JsonProperty("ActivityDate")
    private String activityDate;

    @JsonProperty("Period")
    private String period;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("UserType")
    private String userType;

    @JsonProperty("BuCode")
    private String buCode;

    @JsonProperty("ProductName")
    private String productName;

    @JsonProperty("ProductType")
    private String productType;

    @JsonProperty("Points")
    private Integer points;

    @JsonProperty("StartDate")
    private String startDate;

    @JsonProperty("EndDate")
    private String endDate;

    @JsonProperty("HistoryTH")
    private String historyTH;

    @JsonProperty("HistoryEN")
    private String historyEN;

    @JsonProperty("PointType")
    private String pointType;

    @JsonProperty("PeriodFormat")
    private String periodFormat;

    @JsonProperty("NotificationTH")
    private String notificationTH;

    @JsonProperty("NotificationEN")
    private String notificationEN;

    @JsonProperty("Template")
    private Integer template;

    @JsonProperty("UserId")
    private String userId;

    @JsonProperty("PartitionKey")
    private String partitionKey;

    @JsonProperty("RowKey")
    private String rowKey;

    @JsonProperty("Timestamp")
    private String timestamp;

    @JsonProperty("ETag")
    private String eTag;

}
