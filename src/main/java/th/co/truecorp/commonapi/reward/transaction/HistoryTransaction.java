package th.co.truecorp.commonapi.reward.transaction;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.model.EndpointResultRWD;
import th.co.truecorp.commonapi.reward.service.HistoryListService;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.TransactionLog;
import th.co.truecorp.commonlib.log.context.LogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.exception.EndpointServiceException;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.log.model.TransactionResult;

import java.util.Map;

@Service
public class HistoryTransaction {
	
	private static Logger log = LoggerFactory.getLogger(HistoryTransaction.class);
	
	@Autowired
	private ResultService resultService;
	
	@Autowired
	private LogContextService logContextService;

	@Autowired
	private HistoryListService historyListService;

	@TransactionLog(name = "getHistoryPoint")
	public TransactionResult getHistoryPoint(Map<String, Object> tv, String brand, String subType, String date
										, HttpServletRequest httpRequest) {

		final LogContext logContext = logContextService.getCurrentContext();

		log.info("Starting getHistoryPoint - Brand: {}, SubType: {}, Date: {}", brand, subType, date);

		try {
			// setTransactionId, putT for log transaction & endpoint (*mandatory*)
			logContext.loadHttpRequest(tv, httpRequest);
			log.info("Preparing data for history point list service.");

			EndpointResultRWD historyData = historyListService.historyPointListService(tv, brand, subType, date);
			log.info("History point data retrieved successfully. Converting result to JSON.");
			String json = new Gson().toJson(historyData);
			EndpointResult endpointResult = new Gson().fromJson(json, EndpointResult.class);

			return  new TransactionResult(endpointResult);
		} catch (EndpointServiceException endpointServiceException) {
			return resultService.getTransactionExceptionResult(endpointServiceException);
		}  catch (Exception exception) {
			log.error("Exception occurred in getHistoryPoint: {}", exception.getMessage(), exception);
			return resultService.getTransactionExceptionResult(tv, exception);
		}
	}
}
