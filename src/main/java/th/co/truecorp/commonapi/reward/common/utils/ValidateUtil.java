package th.co.truecorp.commonapi.reward.common.utils;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ValidateUtil {
	
	private static Logger log = LoggerFactory.getLogger(ValidateUtil.class);
	
	@Value("${app.validate.earn.bzbProductId}")
    private String bzbProductIds;
	
	public boolean apiEarnPointAntCoin(boolean isLogin, String productBrand, String digitalId, String campaignCode, String bzbProductId, String bzbAmount) {
        boolean invalid = false;
        
        if (!isLogin && null == digitalId) {
			return true;
		}
        
        if (null != productBrand && "TMH".equalsIgnoreCase(productBrand) && null == campaignCode) {
        	log.info("missing campaignCode -> {}", campaignCode);
        	return true;
        }
        
        if (null != productBrand && "DTAC".equalsIgnoreCase(productBrand) && null == bzbProductId) {
        	log.info("missing campaignCode -> {}", bzbProductId);
        	return true;
        }
        
        List<String> allowedProductIds = Arrays.asList(bzbProductIds.split(","));
        if ("DTAC".equalsIgnoreCase(productBrand) && 
            allowedProductIds.contains(bzbProductId) && null == bzbAmount) {
        	log.info("missing bzbAmount -> {}", bzbAmount);
            return true;
        }
        
        return invalid;
    }

}
