package th.co.truecorp.commonapi.reward.model.endpoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerTransferMessage {
    private String IdentityKey;
    private String ReferralCode;
    private String PartnerChannel;
    private String ValidateDate;
    private boolean Valid;
    private String PayType;
    private String CompanyCode;
    private String SubrType;
    private String SubStatus;
    private boolean Transfer;
    private String TransferDate;
    private String RefTransactionId;
    private String TransactionDate;
    private double PartnerPoint;
    private int PointToEarn;
    private String DtacId;
    private String UserId;
    private String HistoryTH;
    private String HistoryEN;
    private String PeriodFormat;
    private String PartitionKey;
    private String RowKey;
    private String Timestamp;
    private String ETag;
    private String HistoryDetailTH;
    private String HistoryDetailEN;
}
