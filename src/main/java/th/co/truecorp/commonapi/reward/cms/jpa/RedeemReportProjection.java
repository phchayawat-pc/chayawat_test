package th.co.truecorp.commonapi.reward.cms.jpa;

public interface RedeemReportProjection {
    String getBrand_code();
    String getAction_date();
    String getDigital_id();
    String getCampaign_id();
    String getCampaign_code();
    String getDescription();
    String getRedeem_message();
    String getRedeem_status();
}