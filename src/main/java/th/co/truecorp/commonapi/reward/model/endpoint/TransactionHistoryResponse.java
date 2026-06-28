package th.co.truecorp.commonapi.reward.model.endpoint;

import java.time.LocalDateTime;
import java.util.List;

public class TransactionHistoryResponse {
    private String id;
    private String code;
    private String description;
    private LocalDateTime timestamp;
    private List<Result> result;

    // Getters and Setters

    public static class Result {
        private LocalDateTime transactionDate;
        private int change;
        private String type;
        private String imageUrl;
        private Message message;

        // Getters and Setters
    }

    public static class Message {
        private long TransactionDate;
        private String DtacId;
        private boolean Success;
        private String ProductId;
        private double Amount;
        private double InputAmount;
        private long ActivityDate;
        private long Period;
        private String Message;
        private String UserType;
        private String BuCode;
        private String ProductName;
        private String ProductType;
        private int Points;
        private long StartDate;
        private long EndDate;
        private String HistoryTH;
        private String HistoryEN;
        private String PointType;
        private String HistoryDetailTH;
        private String HistoryDetailEN;
        private String PeriodFormat;
        private String NotificationTH;
        private String NotificationEN;
        private int Template;
        private String UserId;
        private String PartitionKey;
        private String RowKey;
        private long Timestamp;
        private String ETag;

        // Getters and Setters
    }
}
