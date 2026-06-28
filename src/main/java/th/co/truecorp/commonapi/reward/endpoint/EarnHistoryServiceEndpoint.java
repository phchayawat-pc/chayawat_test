package th.co.truecorp.commonapi.reward.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import th.co.truecorp.commonapi.reward.cms.jpa.service.CustomMappingMessageService;
import th.co.truecorp.commonapi.reward.cms.jpa.service.ErrorService;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.model.EndpointResultRWD;
import th.co.truecorp.commonapi.reward.model.HistoryPointRsp;
import th.co.truecorp.commonapi.reward.model.ThumbnailRsp;
import th.co.truecorp.commonapi.reward.model.endpoint.*;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.service.APIGWService;

import java.util.*;

@Component
public class EarnHistoryServiceEndpoint {

	private static final Logger log = LoggerFactory.getLogger(EarnHistoryServiceEndpoint.class);

	@Autowired
	private LogContextService logContextService;

	@Autowired
	private APIGWService apigwService;

	@Autowired
	private APIGWUtill apigwUtill;

	@Autowired
	private ResultService resultService;

	@Autowired
	private ErrorService errorService;

	@Autowired
	private CustomMappingMessageService customMappingMessageService;

	@Autowired
	private RewardUtill rewardUtill;

	@EndpointLog(name = Constant.ENDPOINT_SOURCE_SYSTEM_ID_APIGW+".GetEarnHistory")
	public EndpointResult getEarnHistoryService(Map<String, Object> tv) throws Exception {

	EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
	try {
		List<HistoryPointRsp> pointRspList = new ArrayList<>();
        EndpointResult endpointResult = null;
		EndpointResultRWD endpointResult2 = new EndpointResultRWD();

			log.info("start process GetEarnHistory");
			HttpHeaders headers = createHeaders(tv);
			Map<String, Object> pathParams = createPathParams(tv);
			Map<String, Object> queryParams = createQueryParams(tv);

			ResponseEntity<ApiGwEarnHistoryRsp> gwResponse = apigwService.execute(
					logContext,
					HttpMethod.GET,
					Constant.ENDPOINT_SOURCE_SYSTEM_ID,
					Constant.ENDPOINT_SERVICE_GET_EARN_HISTORY,
					ApiGwEarnHistoryRsp.class,
					tv,
					headers,
					pathParams,
					queryParams
			);

		if (gwResponse != null && !gwResponse.getStatusCode().is2xxSuccessful()) {
				String errorCode = gwResponse.getBody().getCode();
				String messageApigw = gwResponse.getBody().getMessage();
				String descriptionApigw = gwResponse.getBody().getDescription();
				String businessErrorApigw = Objects.isNull(gwResponse.getBody().getBusinessError())
						? Constant.N_A
						: gwResponse.getBody().getBusinessError();
				String errorApigw = gwResponse.getBody().getError();
				String timestamp = gwResponse.getBody().getTimestamp() != null ? gwResponse.getBody().getTimestamp() : "";

				String errorMessage = rewardUtill.mapError(descriptionApigw,messageApigw,errorApigw,timestamp);

				endpointResult2 = errorService.mapErrorCode(Constant.QUERY_DATA,
						tv.get("brand").toString(),
						Objects.requireNonNull(errorCode),
						tv.get(ComnConst.KEY_LANGUAGE).toString(),
						errorMessage,
						businessErrorApigw,
						Constant.MESSAGE);
				pointRspList = null;
				tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
				endpointResult = resultService.mapEndpointResultAPIGW(tv,Constant.ENDPOINT_SOURCE_SYSTEM_ID,Constant.ENDPOINT_SERVICE_GET_SHELF_CONTENT,errorCode, gwResponse.getStatusCode().value(),
						endpointResult2.getEndpointStatusType(),
						endpointResult2.getHttpStatus(),
						endpointResult2.getEndpointResponseCode(),
						errorCode,
						errorMessage);
			} else {
				log.info("api success!");
				endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
				pointRspList = gwResponse.getBody() == null ? null : processResult(gwResponse.getBody(), tv.get(ComnConst.KEY_LANGUAGE).toString());

				endpointResult2 = errorService.convertMapResult(endpointResult);
				tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
			}

			tv.put("EarnHistory",pointRspList);

			// for mock response
//			String filePath = "src/main/java/th/co/truecorp/commonapi/reward/tempDataFile/earnResult.json";
//			jsonResponse = new String(Files.readAllBytes(Paths.get(filePath)));
//			EarnHistoryEndpointResponse earnHistoryEndpointResponse = objectMapper.readValue(new File(filePath), EarnHistoryEndpointResponse.class);
			log.info("end process GetEarnHistory");
			return endpointResult;
		} catch(Exception exception) {
			log.error("Error during execute apigw: {}", "GetContent", exception);
			EndpointResult endpointResult = errorService.mapErrorException(exception,tv);
			tv.put(Constant.ENDPOINT_RESULT_RWD, errorService.convertMapResult(endpointResult));
			return endpointResult;
		}
	}

	private HttpHeaders createHeaders(Map<String, Object> tv) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	private Map<String, Object> createPathParams(Map<String, Object> tv) {

		return new HashMap<>();
	}

	private Map<String, Object> createQueryParams(Map<String, Object> tv) {
		Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("transactionId", tv.get("transactionId"));
		queryParams.put("id", tv.get("digitalId"));
		queryParams.put("startDate", tv.get("startDate"));
		queryParams.put("endDate", tv.get("endDate"));
		queryParams.put("channel", Constant.DTAC);
		return queryParams;
	}

    private List<HistoryPointRsp> processResult(ApiGwEarnHistoryRsp resultList, String language) {
        log.info("Processing result");
        List<HistoryPointRsp> historyPointList = new ArrayList<>();
        if (null !=resultList && null!= resultList.getResult() ) {
            for (ApiGwEarnHistoryRsp.Transaction result : resultList.getResult()) {

                ThumbnailRsp thumbnailRsp = new ThumbnailRsp();
                String campaignName = null;
                String campaignDescription = null;
                int points = 0;
                PartnerTransferMessage partnerMessage = new ObjectMapper().convertValue(result.getMessageDetails(), PartnerTransferMessage.class);
                Message message = new ObjectMapper().convertValue(result.getMessageDetails(), Message.class);
                if (!result.getType().equals("rollback")) {
                    switch (result.getType()) {
                        case "adjust":
                            campaignName = resolveAdjustCampaignName(result);
                            campaignDescription = campaignName;
                            points = result.getChange();
                            break;
                        case "partner_transfer":
                            if(null !=partnerMessage){
								campaignName = Objects.equals(language, Constant.TH) ? partnerMessage.getHistoryTH() : partnerMessage.getHistoryEN();
								campaignDescription = campaignName;
								points = partnerMessage.getPointToEarn();
							}
                            break;
                        case "coupon":
                            campaignName = "Promotion Code Redemption to dtac reward coins";
                            campaignDescription = campaignName;
                            points = result.getMessageDetails()!=null?result.getMessageDetails().getPoints():0;
                            break;
                        case "earn":
							if (message != null) {
								campaignName = Objects.equals(language, Constant.TH) ? message.getHistoryTH() : message.getHistoryEN();
								campaignDescription = message.getNotificationEN();
								points = message.getPoints() !=null ? message.getPoints() : 0;
							}
                            break;
                        case "transfer":
                            if(message != null){
								campaignName = Objects.equals(language, Constant.TH) ? message.getHistoryTH() : message.getHistoryEN();
								campaignDescription = campaignName;
								points = message.getPoints() != null ? message.getPoints() : 0;
							}
                            break;
                        case "trace":
							if (result.getMessageDetails()!=null
									&& result.getMessageDetails().getMessage() != null) {
								campaignName = result.getMessageDetails().getMessage();
								campaignDescription = campaignName;
								points = result.getChange();
							}
                            break;
                    }

                    HistoryPointRsp historyPointRsp = new HistoryPointRsp();
                    thumbnailRsp.setHighlight16x9(result.getImageUrl());
                    historyPointRsp.setThumbnail(thumbnailRsp);
                    historyPointRsp.setDate(apigwUtill.convertToReturnFormat(result.getTransactionDate(), "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss") + "+07:00");
                    historyPointRsp.setCampaignName(campaignName);
                    historyPointRsp.setPoints(String.valueOf(points));
                    historyPointRsp.setCampaignDescription(campaignDescription);
                    historyPointRsp.setCouponCode(null);
                    historyPointRsp.setCampaignType(Constant.EARN);
                    historyPointRsp.setCampaignId(null);
                    historyPointRsp.setType(Constant.POINT);
                    historyPointRsp.setTimeCounterFlag("N");
                    historyPointRsp.setTextButton(customMappingMessageService.getMappingMessage(Constant.MESSAGE_TEXT_BUTTOM, Constant.BUTTON, language, Constant.MESSAGE));
                    historyPointList.add(historyPointRsp);
                }
            }
        }
        return historyPointList;
    }

    private String resolveAdjustCampaignName(ApiGwEarnHistoryRsp.Transaction result) {
        if (result != null && result.getMessageDetails() != null) {
            return result.getMessageDetails().getMessage();
        } else {
            int change = result != null ?result.getChange(): 0;
            return (change < 0) ? "Coin Adjust Deduct" : "Coin Adjust Add";
        }
    }
}
