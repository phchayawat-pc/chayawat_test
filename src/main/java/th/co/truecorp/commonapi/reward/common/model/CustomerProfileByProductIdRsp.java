package th.co.truecorp.commonapi.reward.common.model;

import lombok.Data;
import lombok.Value;

import javax.naming.InsufficientResourcesException;
import java.util.List;
import java.util.Map;


@Data
public class CustomerProfileByProductIdRsp {
    private String id;
    private String code;
    private String description;
    private String timestamp;
    private String size;
    private List<ProductPreferenceList> productPreferenceList;

    @Data
    public static class ProductPreferenceList{
        private Integer size;
        private String system;
        private String mainConvergenceCode;
        private ConvergenceList convergenceList;
        private Customer customer;
        private Account account;
        private Subscriber subscriber ;
        private List<Characteristic> characteristic;

        @Data
        public static class ConvergenceList{
            private Integer size;
            private List<ConvergenceList> convergenceListList;
            private String assetGroupId;
            private String convergenceCode;
            private String convergenceType;
        }
        @Data
        public static class Customer{
            private String certificateNumber;
            private String custNumber;
            private String certificateType;
            private Map<String,String> customerType; //code, description
        }
        @Data
        public static class Account{
            private String accountId ;
            private String classify;
            private String statusCode;
            private String statusDescription ;
            private Ben ben;

            @Data
            public static class Ben{
                private String Ben ;
                private String consolidateIndicator ;
            }
        }

        @Data
        public static class Subscriber {
            private String status ;
            private String multiSIMLevel;
            private String multiSIMIndicator;
            private String inactiveStatus;
        }

        @Data
        public static class Characteristic {
            private List<Map<String, Value>> item; //name, value
        }

    }
}
