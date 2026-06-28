package th.co.truecorp.commonapi.reward.cms.jpa;

public interface RedeemReportSummaryProjection {
    String getBrand_code();
    String getRedeem_status();
    String getCampaign_type();
    String getPeriod_date();
    String getTotal_transaction();
}