package th.co.truecorp.commonapi.reward.cms.jpa.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import th.co.truecorp.commonapi.reward.cms.jpa.RedeemReportProjection;
import th.co.truecorp.commonapi.reward.cms.jpa.RedeemReportSummaryProjection;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdRedeemHistory;

import java.util.List;

@Repository
public interface RwdRedeemHistoryRepository extends JpaRepository<RwdRedeemHistory, Integer>, JpaSpecificationExecutor<RwdRedeemHistory> {

    @Query(value = "select * " +
            "from rwd_redeem_history r " +
            "where r.redeem_status = :redeemStatus " +
            "and r.brand_code = :brandCode " +
            "and r.digital_id = :digitalId " +
            "and DATE_TRUNC('day', r.action_date) between to_date(:startDate,'yyyy-MM-dd') and to_date(:endDate,'yyyy-MM-dd') " +
            "order by r.campaign_id", nativeQuery = true)
    List<RwdRedeemHistory> findRwdRedeemHistoryByRedeemStatusAndBrandCodeAndDigitalIdAndActionDateStartEnd(
            @org.springframework.data.repository.query.Param("redeemStatus") String redeemStatus,
            @org.springframework.data.repository.query.Param("brandCode") String brandCode,
            @org.springframework.data.repository.query.Param("digitalId") String digitalId,
            @org.springframework.data.repository.query.Param("startDate") String startDate,
            @org.springframework.data.repository.query.Param("endDate") String endDate);

    @Query(value = "select campaign_id  " +
            "from rwd_redeem_history r " +
            "where r.redeem_status = :redeemStatus " +
            "and r.brand_code = :brandCode " +
            "and r.digital_id = :digitalId " +
            "and DATE_TRUNC('day', r.action_date) between to_date(:startDate,'yyyy-MM-dd') and to_date(:endDate,'yyyy-MM-dd') " +
            "group by campaign_id", nativeQuery = true)
    List<String> findCampaignIdByRedeemStatusAndBrandCodeAndDigitalIdAndActionDateStartEnd(
            @org.springframework.data.repository.query.Param("redeemStatus") String redeemStatus,
            @org.springframework.data.repository.query.Param("brandCode") String brandCode,
            @org.springframework.data.repository.query.Param("digitalId") String digitalId,
            @org.springframework.data.repository.query.Param("startDate") String startDate,
            @org.springframework.data.repository.query.Param("endDate") String endDate);
    // Example of custom query methods:
    List<RwdRedeemHistory> findByDigitalId(String digitalId);

    List<RwdRedeemHistory> findByBrandCodeAndRedeemStatus(String brandCode, String redeemStatus);
}