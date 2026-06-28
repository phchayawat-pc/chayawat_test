package th.co.truecorp.commonapi.reward.model;

import lombok.Data;

import java.util.List;

@Data
public class ProfileTierDetailRsp {
   private String cardType;
   private String title;
   private String desc;
   private ConditionTier condition;
   private UpgradeNextTier upgradeNextTier;

   @Data
   public static class ConditionTier{
       private List<ConditionColumn> column;
       private List<ConditionDataSource> dataSource;
   }

   @Data
   public static class ConditionColumn{
       private Integer seq;
       private String title;
   }

   @Data
   public static class ConditionDataSource{
       private Integer seq;
       private String longevity;
       private String usage;
   }

   @Data
   public static class UpgradeNextTier{
       private String title;
       private String desc;
   }

}
