package th.co.truecorp.commonapi.reward.transaction;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.service.ErrorService;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.model.*;
import th.co.truecorp.commonapi.reward.service.*;
import th.co.truecorp.commonapi.reward.service.layout.LayoutService;
import th.co.truecorp.commonapi.reward.service.section.SectionService;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.TransactionLog;
import th.co.truecorp.commonlib.log.context.LogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.exception.EndpointServiceException;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.log.model.TransactionResult;

import java.util.Map;

@Service
public class ShelfTransaction {

	private static Logger log = LoggerFactory.getLogger(ShelfTransaction.class);

	@Autowired
	private ResultService resultService;

	@Autowired
	private LogContextService logContextService;

	@Autowired
	private LayoutService layoutService;

	@Autowired
	private ShelfLayoutListService shelfLayoutListService;

	@Autowired
	private ShelfSectionNoneRelatedService shelfSectionNoneRelatedService;

	@Autowired
	private ShelfTemplateAllDealListService shelfTemplateAllDealListService;

	@Autowired
	private SectionService sectionService;

	@Autowired
	private ThematicHeaderService thematicHeaderService;

	@Autowired
	private ThematicFilterService thematicFilterService;

	@Autowired
	private ThematicDealListService thematicDealListService;

	@Autowired
	private ErrorService errorService;

	@Autowired
	private ShelfMajorListService shelfMajorListService;

	@Autowired
	private ShelfGroupingListService shelfGroupingListService;

	@Autowired
	private ShelfTemplateHighlightService shelfTemplateHighlightService;

	@Autowired
	private ShelfTemplateDetailListService shelfTemplateDetailListService;

	@Autowired
	private ShelfSectionAllDataService shelfSectionAllDataService;

	@Autowired
	private ShelfTemplateMerchantDealListService shelfTemplateMerchantDealListService;

	@Autowired
	private ShelfSectionContentDetailService shelfSectionContentDetailService;

	@Autowired
	private ShelfSectionDetailService shelfSectionDetailService;

	@TransactionLog(name = "getlayout")
	public TransactionResult getlayouts(Map<String, Object> tv, HttpServletRequest httpRequest) {

		final LogContext logContext = logContextService.getCurrentContext();

		try {
			logContext.loadHttpRequest(tv, httpRequest);
			log.info("prepair data");

			LayoutObj layout = shelfLayoutListService.GetlayoutId(tv);
			String layoutId = layout.getLayoutId();

			ShelfLoyoutRsp shelfLoyoutRsp = new ShelfLoyoutRsp();

			if (layoutId != null){
				tv.put(Constant.LAYOUT_ID, layoutId);

				ResponseEntity<ShelfLoyoutRsp> entity = layoutService.getList(tv, layoutId);

				shelfLoyoutRsp = entity.getBody();

			}else{
				log.info("Get layout failed!");
				shelfLoyoutRsp = null;
			}

			EndpointResultRWD endpointResultRwd = (EndpointResultRWD) tv.get("err");

			tv.put(Constant.TRANSACTION_RESPONSE_KEY,shelfLoyoutRsp);

			return  new TransactionResult(errorService.revertMapResult(endpointResultRwd));
		} catch (EndpointServiceException endpointServiceException) {
			return resultService.getTransactionExceptionResult(endpointServiceException);
		}  catch (Exception exception) {
			return resultService.getTransactionExceptionResult(tv, exception);
		}
	}

    @TransactionLog(name = "getSectionHeader")
    public TransactionResult getSectionHeader(Map<String, Object> tv, HttpServletRequest httpRequest, String lang, String brand,
                                              String layoutId, String sectionId, String displayTypeCode, String useCmsContent) {
		log.info("TransactionResult getSectionHeader");
        final LogContext logContext = logContextService.getCurrentContext();

        try {
            logContext.loadHttpRequest(tv, httpRequest);
            log.info("prepair data");


			tv.put(Constant.LAYOUT_ID,layoutId);

			ResponseEntity<ShelfSectionHeaderRsp> entity = sectionService.getList(tv, lang, brand, layoutId, sectionId, displayTypeCode, useCmsContent);

			EndpointResult shelfGetSectionHeaderRsp = (EndpointResult) tv.get("err");

			ShelfSectionHeaderRsp details = entity.getBody();

			tv.put(Constant.TRANSACTION_RESPONSE_KEY,details);


			return  new TransactionResult(shelfGetSectionHeaderRsp);
        } catch (EndpointServiceException endpointServiceException) {
			return resultService.getTransactionExceptionResult(endpointServiceException);
		} catch (Exception exception) {
            return resultService.getTransactionExceptionResult(tv, exception);
        }
    }

	@TransactionLog(name = "getSectionNoneRelated")
	public TransactionResult getSectionNoneRelated(Map<String, Object> tv, HttpServletRequest httpRequest) {

		final LogContext logContext = logContextService.getCurrentContext();

		try {
			logContext.loadHttpRequest(tv, httpRequest);
			log.info("prepair data");

			EndpointResult layoutData = shelfSectionNoneRelatedService.getSectionNoneRelated(tv);

			return  new TransactionResult(layoutData);
		} catch (EndpointServiceException endpointServiceException) {
			return resultService.getTransactionExceptionResult(endpointServiceException);
		} catch (Exception exception) {
			return resultService.getTransactionExceptionResult(tv, exception);
		}
	}

	@TransactionLog(name = "getTemplatealldeallist")
	public TransactionResult getTemplatealldeallist(Map<String, Object> tv, HttpServletRequest httpRequest, String brand) {

		final LogContext logContext = logContextService.getCurrentContext();

		try {
			logContext.loadHttpRequest(tv, httpRequest);
			log.info("prepair data");

			EndpointResult layoutData = shelfTemplateAllDealListService.getTemplateAllDealList(tv, brand);

			return  new TransactionResult(layoutData);
		} catch (EndpointServiceException endpointServiceException) {
			return resultService.getTransactionExceptionResult(endpointServiceException);
		} catch (Exception exception) {
			return resultService.getTransactionExceptionResult(tv, exception);
		}
	}

	@TransactionLog(name = "getSectionDetail")
	public TransactionResult getSectionDetail(Map<String, Object> tv, HttpServletRequest httpRequest, String brand) {

		final LogContext logContext = logContextService.getCurrentContext();

		try {
			logContext.loadHttpRequest(tv, httpRequest);
			log.info("prepair data");

			EndpointResult layoutData = shelfSectionDetailService.getSectionDetail(tv, brand);

			return  new TransactionResult(layoutData);
		} catch (EndpointServiceException endpointServiceException) {
			return resultService.getTransactionExceptionResult(endpointServiceException);
		} catch (Exception exception) {
			return resultService.getTransactionExceptionResult(tv, exception);
		}
	}

	@TransactionLog(name = "getThematicHeader")
	public TransactionResult getThematicHeader(Map<String, Object> tv, HttpServletRequest httpRequest) {

		final LogContext logContext = logContextService.getCurrentContext();
		try {
			logContext.loadHttpRequest(tv, httpRequest);
			EndpointResultRWD endpointResultRwd = thematicHeaderService.GetThematicHeaderService(tv,httpRequest);
			return  new TransactionResult(errorService.revertMapResult(endpointResultRwd));
		} catch (EndpointServiceException endpointServiceException) {
			return resultService.getTransactionExceptionResult(endpointServiceException);
		} catch (Exception exception) {
			return resultService.getTransactionExceptionResult(tv, exception);
		}
	}

	@TransactionLog(name = "getThematicFilter")
	public TransactionResult getThematicFilter(Map<String, Object> tv, HttpServletRequest httpRequest) {

		final LogContext logContext = logContextService.getCurrentContext();
		try {
			logContext.loadHttpRequest(tv, httpRequest);
			EndpointResultRWD endpointResultRwd = thematicFilterService.GetThematicFilterService(tv,httpRequest);
			return  new TransactionResult(errorService.revertMapResult(endpointResultRwd));
		} catch (EndpointServiceException endpointServiceException) {
			return resultService.getTransactionExceptionResult(endpointServiceException);
		} catch (Exception exception) {
			return resultService.getTransactionExceptionResult(tv, exception);
		}
	}

	@TransactionLog(name = "getThematicDealList")
	public TransactionResult getThematicDealList(Map<String, Object> tv, HttpServletRequest httpRequest) {

		final LogContext logContext = logContextService.getCurrentContext();
		try {
			logContext.loadHttpRequest(tv, httpRequest);
			EndpointResultRWD endpointResultRWD = thematicDealListService.GetThematicDealListService(tv,httpRequest);
			return  new TransactionResult(errorService.revertMapResult(endpointResultRWD));
		} catch (EndpointServiceException endpointServiceException) {
			return resultService.getTransactionExceptionResult(endpointServiceException);
		} catch (Exception exception) {
			return resultService.getTransactionExceptionResult(tv, exception);
		}
	}

    @TransactionLog(name = "getSectionContentDetail")
    public TransactionResult getSectionContentDetail(Map<String, Object> tv, HttpServletRequest httpRequest) {

        final LogContext logContext = logContextService.getCurrentContext();

        try {
            logContext.loadHttpRequest(tv, httpRequest);
            log.info("prepair data");

            EndpointResult sectionData = shelfSectionContentDetailService.getSectionContentDetail(tv);

            return  new TransactionResult(sectionData);
        } catch (EndpointServiceException endpointServiceException) {
			return resultService.getTransactionExceptionResult(endpointServiceException);
		} catch (Exception exception) {
            return resultService.getTransactionExceptionResult(tv, exception);
        }
    }

	@TransactionLog(name = "getTemplateMerchantDealList")
	public TransactionResult getTemplateMerchantDealList(Map<String, Object> tv, HttpServletRequest httpRequest, String brand) {

		final LogContext logContext = logContextService.getCurrentContext();

		try {
			logContext.loadHttpRequest(tv, httpRequest);
			log.info("prepair data");

			EndpointResultRWD sectionData = shelfTemplateMerchantDealListService.getTemplateMerchantDealList(tv, brand);
			String json = new Gson().toJson(sectionData);
			EndpointResult endpointResult = new Gson().fromJson(json, EndpointResult.class);

			return  new TransactionResult(endpointResult);
		} catch (EndpointServiceException endpointServiceException) {
			return resultService.getTransactionExceptionResult(endpointServiceException);
		} catch (Exception exception) {
			return resultService.getTransactionExceptionResult(tv, exception);
		}
	}

	@TransactionLog(name = "getSectionAllData")
	public TransactionResult getSectionAllData(Map<String, Object> tv, HttpServletRequest httpRequest, String brand) {

		final LogContext logContext = logContextService.getCurrentContext();

		try {
			logContext.loadHttpRequest(tv, httpRequest);
			log.info("prepair data");

			EndpointResult sectionData = shelfSectionAllDataService.getSectionAllData(tv, brand);

			return  new TransactionResult(sectionData);
		} catch (EndpointServiceException endpointServiceException) {
			return resultService.getTransactionExceptionResult(endpointServiceException);
		} catch (Exception exception) {
			return resultService.getTransactionExceptionResult(tv, exception);
		}
	}

	@TransactionLog(name = "getTemplateDetailList")
	public TransactionResult getTemplateDetailList(Map<String, Object> tv, HttpServletRequest httpRequest, String brand) {

		final LogContext logContext = logContextService.getCurrentContext();

		try {
			logContext.loadHttpRequest(tv, httpRequest);
			log.info("prepair data");

			EndpointResult sectionData = shelfTemplateDetailListService.getTemplateDetailList(tv, brand);

			return  new TransactionResult(sectionData);
		} catch (EndpointServiceException endpointServiceException) {
			return resultService.getTransactionExceptionResult(endpointServiceException);
		} catch (Exception exception) {
			return resultService.getTransactionExceptionResult(tv, exception);
		}
	}

	@TransactionLog(name = "getTemplateHighlight")
	public TransactionResult getTemplateHighlight(Map<String, Object> tv, HttpServletRequest httpRequest) {

		final LogContext logContext = logContextService.getCurrentContext();

		try {
			logContext.loadHttpRequest(tv, httpRequest);
			log.info("prepair data");

			EndpointResult sectionData = shelfTemplateHighlightService.getTemplateHighlight(tv);

			return  new TransactionResult(sectionData);
		} catch (EndpointServiceException endpointServiceException) {
			return resultService.getTransactionExceptionResult(endpointServiceException);
		} catch (Exception exception) {
			return resultService.getTransactionExceptionResult(tv, exception);
		}
	}

	@TransactionLog(name = "getGrouping")
	public TransactionResult getGrouping(Map<String, Object> tv, HttpServletRequest httpRequest) {

		final LogContext logContext = logContextService.getCurrentContext();

		try {
			logContext.loadHttpRequest(tv, httpRequest);
			log.info("prepair data");

			EndpointResultRWD sectionDataRwd = shelfGroupingListService.getGroupingService(tv);

			return  new TransactionResult(errorService.revertMapResult(sectionDataRwd));
		} catch (EndpointServiceException endpointServiceException) {
			return resultService.getTransactionExceptionResult(endpointServiceException);
		} catch (Exception exception) {
			return resultService.getTransactionExceptionResult(tv, exception);
		}
	}

	@TransactionLog(name = "getMajor")
	public TransactionResult getMajor(Map<String, Object> tv, HttpServletRequest httpRequest) {

		final LogContext logContext = logContextService.getCurrentContext();

		try {
			logContext.loadHttpRequest(tv, httpRequest);
			log.info("prepair data");

			EndpointResultRWD sectionDataRwd = shelfMajorListService.getMajorService(tv);

			return  new TransactionResult(errorService.revertMapResult(sectionDataRwd));
		} catch (EndpointServiceException endpointServiceException) {
			return resultService.getTransactionExceptionResult(endpointServiceException);
		} catch (Exception exception) {
			return resultService.getTransactionExceptionResult(tv, exception);
		}
	}
}
