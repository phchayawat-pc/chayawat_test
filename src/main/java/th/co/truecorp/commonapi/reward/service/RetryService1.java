package th.co.truecorp.commonapi.reward.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdRedeemHistory;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSystemConfig;
import th.co.truecorp.commonapi.reward.cms.jpa.repository.RwdRedeemHistoryRepository;

@Service
public class RetryService1 {

    private static Logger log = LoggerFactory.getLogger(RetryService1.class);

    private final RwdRedeemHistoryRepository rwdRedeemHistoryRepository;

    int attempt = 1;
    @Autowired
    public RetryService1(RwdRedeemHistoryRepository myRepository) {
        this.rwdRedeemHistoryRepository = myRepository;
    }

    @Async
    @Retryable(
            value = { DataAccessException.class, RuntimeException.class },
            maxAttempts = 1,
            backoff = @Backoff(delay = 5000))
    public void retrySaveData(RwdRedeemHistory data) throws InterruptedException {
        rwdRedeemHistoryRepository.save(data);
    }

    @Recover
    public void recoverSaveData(Exception e, RwdSystemConfig data) {
        // บันทึก error หรือทำอย่างอื่นเมื่อ retry ครบตามจำนวนที่กำหนดแล้วยังไม่สำเร็จ
        log.error("Failed to save data after retrying: " + e.getMessage());
    }
}

