package th.co.truecorp.commonapi.reward.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetPrivilegeHistoryResponse {

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Campaign {
        private String id;
        private String type;
        private String productSpecId;
        private String name;
        private String description;
        private Account accountId;
        private List<LoyaltyAccount> loyaltyAccounts;
        private Quantity quantity;
        private ValidFor validFor;
        private List<Characteristic> characteristics;
    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Account {
        private String id;
        private String href;

    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class LoyaltyAccount {
        private String id;
        private LoyaltyBalance loyaltyBalance;

    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class LoyaltyBalance {
        private String id;
        private String unit;
        private int balance;

    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Quantity {
        private String unit;
        private int balance;

    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class ValidFor {
        private String startDateTime;
        private String endDateTime;

    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Characteristic {
        private String name;
        private String value;

    }


}
