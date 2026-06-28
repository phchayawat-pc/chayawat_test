package th.co.truecorp.commonapi.reward.model.endpoint.redeem.earn.trueenv.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Redemption {
    private long transactionId;
    private String message;
    private int points;
    private int balance;
    private boolean blockUser;
    private boolean blockCustomer;
    private boolean blockAccount;
    private String trnDate;
    private String campaignCode;
    private String campaignExpireDate;
    private List<IssuedCoupon> issuedCoupons;

    private int bonusPoints;
    private int loanPoints;
    private int moneyAmount;
    private String pointBalances;
    private String pointTypes;

}
