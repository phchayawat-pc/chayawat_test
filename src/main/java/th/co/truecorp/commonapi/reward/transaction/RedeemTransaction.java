package th.co.truecorp.commonapi.reward.transaction;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonapi.reward.service.*;
import th.co.truecorp.commonlib.jpa.service.ConfigService;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.TransactionLog;
import th.co.truecorp.commonlib.log.context.LogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.exception.EndpointServiceException;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.log.model.TransactionResult;

import java.util.Map;

@Service
public class RedeemTransaction {
	
	private static Logger log = LoggerFactory.getLogger(RedeemTransaction.class);
	
	@Autowired
	private ResultService resultService;
	
	@Autowired
	private LogContextService logContextService;
	
	@Autowired
	private RedeemDetailService redeemDetailService;

	@Autowired
	private RedeemPrizeService redeemPrizeService;

	@Autowired
	private RedeemGetTakeRedeemService redeemGetTakeRedeemService;

	@Autowired
	private RedeemArticDetailService redeemArticDetailService;

	@TransactionLog(name = "getDetail")
	public TransactionResult getDetail(Map<String, Object> tv, HttpServletRequest httpRequest, String brand) {

		final LogContext logContext = logContextService.getCurrentContext();
		log.info("Starting getDetail operation - Brand: {}", brand);

		try {
			logContext.loadHttpRequest(tv, httpRequest);

			log.info("Preparing data for redeemDetailService.");
			EndpointResultRWD endpointResultRwd = redeemDetailService.getDetail(tv, brand);

			log.info("Detail information retrieved successfully. Converting result to JSON.");
			String json = new Gson().toJson(endpointResultRwd);

			log.info("Parsing JSON response into EndpointResult.");
			EndpointResult endpointResult = new Gson().fromJson(json, EndpointResult.class);
			return new TransactionResult(endpointResult);
		} catch (EndpointServiceException endpointServiceException) {
			return resultService.getTransactionExceptionResult(endpointServiceException);
		}  catch (Exception exception) {
			log.error("Exception occurred in getDetail: {}", exception.getMessage(), exception);

			return resultService.getTransactionExceptionResult(tv, exception);

		}
	}

	@TransactionLog(name = "getRedeemDealDetailTransaction")
	public TransactionResult getRedeemDealDetailTransaction(Map<String, Object> tv, String lang, String brand, String cmpgId
			, HttpServletRequest httpRequest) {

		final LogContext logContext = logContextService.getCurrentContext();

		log.info("Starting getRedeemDealDetailTransaction - Lang: {}, Brand: {}, CampaignId: {}", lang, brand, cmpgId);

		try {
			logContext.loadHttpRequest(tv, httpRequest);

			log.info("Preparing data for redeemPrizeService.");
			EndpointResultRWD redeemDealScanCode = redeemPrizeService.getRedeemDealScanCode(tv, brand, lang, cmpgId);

			log.info("Redeem deal details retrieved successfully. Converting result to JSON.");
			String json = new Gson().toJson(redeemDealScanCode);

			EndpointResult endpointResult = new Gson().fromJson(json, EndpointResult.class);

			return  new TransactionResult(endpointResult);
		}  catch (EndpointServiceException endpointServiceException) {
			return resultService.getTransactionExceptionResult(endpointServiceException);
		} catch (Exception exception) {
			return resultService.getTransactionExceptionResult(tv, exception);
		}
	}

	@TransactionLog(name = "getTakeRedeemTransaction")
	public TransactionResult getTakeRedeemTransaction(Map<String, Object> tv, String lang, String brand, RedeemDataReq redeemDataReq
			, HttpServletRequest httpRequest) {

		final LogContext logContext = logContextService.getCurrentContext();

		log.info("Starting getTakeRedeemTransaction - lang: {}, brand: {}, redeemDataReq: {}", lang, brand, redeemDataReq);

		try {
			logContext.loadHttpRequest(tv, httpRequest);

			log.info("Preparing data for redeemGetTakeRedeemService.");
			EndpointResultRWD takeRedeemData = redeemGetTakeRedeemService.getTakeRedeem(tv, brand, lang, redeemDataReq, httpRequest);

			log.info("takeRedeemData information retrieved successfully. Converting result to JSON.");
			String json = new Gson().toJson(takeRedeemData);

			EndpointResult endpointResult = new Gson().fromJson(json, EndpointResult.class);

			return  new TransactionResult(endpointResult);
		}  catch (EndpointServiceException endpointServiceException) {
			return resultService.getTransactionExceptionResult(endpointServiceException);
		} catch (Exception exception) {
			log.error("Error occurred in getTakeRedeemTransaction: {}", exception.getMessage(), exception);
			return resultService.getTransactionExceptionResult(tv, exception);
		}
	}

	@TransactionLog(name = "getArticleDetail")
	public TransactionResult getArticleDetail(Map<String, Object> tv, HttpServletRequest httpRequest, String brand) {

		final LogContext logContext = logContextService.getCurrentContext();

		log.info("Starting getArticleDetail operation - Brand: {}", brand);

		try {
			logContext.loadHttpRequest(tv, httpRequest);

			log.info("Preparing data for redeemArticDetailService.");
			EndpointResultRWD resultProfile = redeemArticDetailService.getArticleDetail(tv, brand);

			log.info("Article details retrieved successfully. Converting result to JSON.");
			String json = new Gson().toJson(resultProfile);

			EndpointResult endpointResult = new Gson().fromJson(json, EndpointResult.class);
			return new TransactionResult(endpointResult);
		}  catch (EndpointServiceException endpointServiceException) {
			return resultService.getTransactionExceptionResult(endpointServiceException);
		} catch (Exception exception) {
			log.error("Exception occurred in getArticleDetail: {}", exception.getMessage(), exception);
			return resultService.getTransactionExceptionResult(tv, exception);

		}
	}
}
