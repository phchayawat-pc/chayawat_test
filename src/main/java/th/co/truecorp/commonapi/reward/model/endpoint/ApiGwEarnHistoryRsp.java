package th.co.truecorp.commonapi.reward.model.endpoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiGwEarnHistoryRsp {

    private String id;
    private String code;
    private String description;
    private String timestamp;
    private String message;
    private String businessError;
    private String error;
    private List<Transaction> result;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Transaction {
        private String transactionDate;
        private int change;
//        private String message;
        private String type;
        private String imageUrl;

        @JsonProperty("message")
        private Message messageDetails;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Message {
            private long transactionDate;
            private String dtacId;
            private boolean success;
            private String productId;
            private int amount;
            private int inputAmount;
            private long activityDate;
            private long period;
            private String message;
            private String userType;
            private String buCode;
            private String productName;
            private String productType;
            private int points;
            private long startDate;
            private long endDate;
            private String historyTH;
            private String historyEN;
            private String pointType;
            private String periodFormat;
            private String notificationTH;
            private String notificationEN;
            private int template;
            private String userId;
            private String partitionKey;
            private String rowKey;
            private long timestamp;
            private String etag;
            private String id;
        }
    }
}
