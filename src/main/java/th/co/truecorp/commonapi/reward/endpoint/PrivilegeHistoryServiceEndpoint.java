package th.co.truecorp.commonapi.reward.endpoint;

import com.google.gson.Gson;
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
import th.co.truecorp.commonapi.reward.model.Condition;
import th.co.truecorp.commonapi.reward.model.EndpointResultRWD;
import th.co.truecorp.commonapi.reward.model.HistoryPointRsp;
import th.co.truecorp.commonapi.reward.model.ThumbnailRsp;
import th.co.truecorp.commonapi.reward.model.endpoint.ApiGwBurnHistoryRsp;
import th.co.truecorp.commonapi.reward.model.endpoint.ApiGwErrorRsp;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.service.APIGWService;


import java.text.ParseException;
import java.util.*;

@Component
public class PrivilegeHistoryServiceEndpoint {

	private static final Logger log = LoggerFactory.getLogger(PrivilegeHistoryServiceEndpoint.class);

	@Autowired
	private LogContextService logContextService;

	@Autowired
	private APIGWService apigwService;

	@Autowired
	private ResultService resultService;

	@Autowired
	private APIGWUtill apigwUtill;

	@Autowired
	private ErrorService errorService;

	@Autowired
	private RewardUtill rewardUtill;

	@Autowired
	private CustomMappingMessageService customMappingMessageService;

	@EndpointLog (name = Constant.ENDPOINT_SOURCE_SYSTEM_ID_APIGW+".getPrivilegeHistoryService")
	public EndpointResult getPrivilegeHistoryService(Map<String, Object> tv) {

		try {
			log.info("start process getPrivilegeHistoryService {}",tv.get("productStatus").toString());

			HttpHeaders headers = createHeaders(tv);
			Map<String, Object> pathParams = createPathParams(tv);
			Map<String, Object> queryParams = createQueryParams(tv);
			EndpointResult endpointResult = null;
			EndpointResultRWD endpointResult2 = new EndpointResultRWD();
			List<HistoryPointRsp> responsesList = new ArrayList<>();
			Gson gson = new Gson();

			ResponseEntity<String> gwResponse = apigwService.execute(
					logContextService.getEndpointLoggingContext(),
					HttpMethod.GET,
					Constant.ENDPOINT_SOURCE_SYSTEM_ID,
					Constant.ENDPOINT_SERVICE_GET_PRIVILEGE_HISTORY,
					String.class,
					tv,
					headers,
					pathParams,
					queryParams
			);

			if (gwResponse != null && !gwResponse.getStatusCode().is2xxSuccessful()) {
				responsesList = null;
				ApiGwErrorRsp apiGwErrorRsp = gson.fromJson(gwResponse.getBody(), ApiGwErrorRsp.class);
				String errorCode = Objects.requireNonNull(apiGwErrorRsp.getCode());
				String messageApigw = apiGwErrorRsp.getMessage();
				String descriptionApigw = apiGwErrorRsp.getDescription();
				String businessErrorApigw = apiGwErrorRsp.getBusinessError();
				String errorApigw = apiGwErrorRsp.getError();
				String timestamp = apiGwErrorRsp.getTimestamp() == null ? "" : apiGwErrorRsp.getTimestamp();

				String errorMessage = rewardUtill.mapError(descriptionApigw,messageApigw,errorApigw,timestamp);

				endpointResult2 = errorService.mapErrorCode(Constant.QUERY_DATA,
						tv.get("brand").toString().toUpperCase(),
						errorCode,
						tv.get(ComnConst.KEY_LANGUAGE).toString().toUpperCase(),
						errorMessage,
						null != businessErrorApigw && !businessErrorApigw.isEmpty() ? businessErrorApigw : Constant.N_A,
						Constant.MESSAGE);
				tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
				endpointResult = resultService.mapEndpointResultAPIGW(tv,Constant.ENDPOINT_SOURCE_SYSTEM_ID,Constant.ENDPOINT_SERVICE_GET_SHELF_CONTENT,errorCode, gwResponse.getStatusCode().value(),
						endpointResult2.getEndpointStatusType(),
						endpointResult2.getHttpStatus(),
						endpointResult2.getEndpointResponseCode(),
						errorCode,
						errorMessage);
			} else {
				log.info("api success!");
				ApiGwBurnHistoryRsp[] apiGwBurnHistory = gson.fromJson(gwResponse.getBody().trim(), ApiGwBurnHistoryRsp[].class);

				endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
				log.info("Response from profile API: {}", gwResponse.getBody());

				if(apiGwBurnHistory.length > 0){
					responsesList = mapResponse(tv, apiGwBurnHistory);
				}else{
					endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC, ComnConst.STTS_CODE_SUCC);
				}

				endpointResult2 = errorService.convertMapResult(endpointResult);
				tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
			}

			String productStatus = Objects.equals(tv.get("productStatus").toString(), "Used") ? "UsedPrivilegeHistory" : "AvailablePrivilegeHistory";
			tv.put(productStatus,responsesList);

			log.info("end process getPrivilegeHistoryService {}",tv.get("productStatus").toString());
			return endpointResult;

		} catch (Exception exception) {
			log.info("-------- error on call getPrivilegeHistoryService -----------"+exception.getMessage());
			EndpointResult endpointResult = errorService.mapErrorException(exception,tv);
			tv.put(Constant.ENDPOINT_RESULT_RWD, errorService.convertMapResult(endpointResult));
			return endpointResult;

		}
	}

	private HttpHeaders createHeaders(Map<String, Object> tv) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", tv.get("Authorization").toString());
		return headers;
	}

	private Map<String, Object> createPathParams(Map<String, Object> tv) {
		Map<String, Object> pathParams = new HashMap<>();
		return pathParams;
	}

	private Map<String, Object> createQueryParams(Map<String, Object> tv) {
		Map<String, Object> queryParams = new HashMap<>();
		String description = mapDescription(tv);
		queryParams.put("id", tv.get("transactionId"));
		queryParams.put("memberId", tv.get("digitalId"));
		queryParams.put("startDate", tv.get("startDate"));
		queryParams.put("endDate", tv.get("endDate"));
		queryParams.put("productStatus", tv.get("productStatus"));
		queryParams.put("description", description);
		return queryParams;
	}
	private String mapDescription(Map<String, Object> tv){
		String description = "1054";
		String language = tv.get(ComnConst.KEY_LANGUAGE).toString();
		switch (language) {
			case Constant.TH:
				description = "1054";
				break;
			case Constant.MY:
				description = "1109";
				break;
			case Constant.KM:
				description = "1107";
				break;
			default :
				description = "1033";
				break;
		}
		return description;
	}

	private List<HistoryPointRsp> mapResponse(Map<String, Object> tv , ApiGwBurnHistoryRsp[] apiGwBurnHistory)  throws Exception {

		String language = tv.get(ComnConst.KEY_LANGUAGE).toString();

		try {
			log.info("map historyPointRsps");
			List<HistoryPointRsp> historyPointRsps = new ArrayList<HistoryPointRsp>();
			if(apiGwBurnHistory != null) {
				for (ApiGwBurnHistoryRsp result : apiGwBurnHistory) {
					HistoryPointRsp historyPointDetail = new HistoryPointRsp();
					ThumbnailRsp ThumbnailRsp = new ThumbnailRsp();
					String Highlight = result.getHref();
					if (Highlight != null) {
						ThumbnailRsp.setHighlight16x9(Highlight);
					}
					ApiGwBurnHistoryRsp.ValidFor validFor = result.getLoyaltyAccount() != null &&
							result.getLoyaltyAccount().getLoyaltyBalance() != null &&
							result.getLoyaltyAccount().getLoyaltyBalance().getValidFor() != null
							? result.getLoyaltyAccount().getLoyaltyBalance().getValidFor()
							: null;

					String dateTime = validFor != null
							? validFor.getStartDateTime()
							: null;
					String endDateTime = validFor != null
							? validFor.getEndDateTime()
							: null;

					historyPointDetail.setThumbnail(ThumbnailRsp);
					historyPointDetail.setCampaignName(result.getName());
					historyPointDetail.setCampaignDescription(result.getDescription());
					historyPointDetail.setPoints(mapPoint(result));
					historyPointDetail.setDate(dateTime != null ? apigwUtill.convertToReturnFormat(dateTime, "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss") + "+07:00" : null);
					historyPointDetail.setCouponCode(result.getLoyaltyAccount().getId());
					historyPointDetail.setCampaignType(result.getCharacteristics() != null ? mapCampaignType(result.getCharacteristics()) : null);
					historyPointDetail.setTextButton(customMappingMessageService.getMappingMessage(Constant.MESSAGE_TEXT_BUTTOM, Constant.BUTTON, language, Constant.MESSAGE));
					historyPointDetail.setConditionInfo(conditionInfo(language, result.getCouponDetail(), result.getAccountId()));
					historyPointDetail.setType(mapType(result));
					historyPointDetail.setTimeCounterFlag(result.getLoyaltyAccount().getId() != null && !result.getLoyaltyAccount().getId().isEmpty() ? "Y" : "N");
					historyPointDetail.setCouponExpiryDate(endDateTime != null && !endDateTime.isEmpty() ? apigwUtill.convertToReturnFormat(endDateTime, "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss") + "+07:00" : null);

					historyPointRsps.add(historyPointDetail);
				}
			}
            return historyPointRsps;
		}catch (Exception e){
			log.info("Error Map Get PrivilegeHistoryService. " + e);
			return null;
		}
	}

	private List<Condition> conditionInfo(String language , ApiGwBurnHistoryRsp.CouponDetail couponDetail , String accountId){

		List<Condition> conditionList = new ArrayList<Condition>();

		if((couponDetail.getCouponApp_th() == null || couponDetail.getCouponApp_th().equals("null"))
				&& (couponDetail.getCouponApp_en() == null || couponDetail.getCouponApp_en().equals("null"))
				&& (couponDetail.getCouponApp_my() == null || couponDetail.getCouponApp_my().equals("null"))
				&& (couponDetail.getCouponApp_km() == null || couponDetail.getCouponApp_km().equals("null"))
				&& (couponDetail.getCouponApp_url() == null || couponDetail.getCouponApp_url().equals("null"))
				&& (couponDetail.getCouponWeb_th() == null || couponDetail.getCouponWeb_th().equals("null"))
				&& (couponDetail.getCouponWeb_en() == null || couponDetail.getCouponWeb_en().equals("null"))
				&& (couponDetail.getCouponWeb_my() == null || couponDetail.getCouponWeb_my().equals("null"))
				&& (couponDetail.getCouponWeb_km() == null || couponDetail.getCouponWeb_km().equals("null"))
				&& (couponDetail.getCouponWeb_url() == null || couponDetail.getCouponWeb_url().equals("null"))){
			Condition condition = new Condition();
			condition.setType(Constant.TEXT);
			condition.setMessage("แสดงรหัสที่ " + accountId);
			conditionList.add(condition);
		}

		if(couponDetail.getCouponApp_url() != null && !couponDetail.getCouponApp_url().equals("null")){
			Condition condition = new Condition();
			condition.setType(Constant.BUTTON);
			condition.setLinkType(Constant.EXTERNAL_LINK);
			condition.setUrl(couponDetail.getCouponApp_url().replace("\"", ""));

			switch (language) {
				case Constant.TH:
					condition.setMessage(couponDetail.getCouponApp_th().replace("\"", ""));
					break;
				case Constant.MY:
					condition.setMessage(couponDetail.getCouponApp_my().replace("\"", ""));
					break;
				case Constant.KM:
					condition.setMessage(couponDetail.getCouponApp_km().replace("\"", ""));
					break;
				default :
					condition.setMessage(couponDetail.getCouponWeb_en().replace("\"", ""));
					break;
			}
			conditionList.add(condition);
		}

		if(couponDetail.getCouponWeb_url() != null && !couponDetail.getCouponWeb_url().equals("null")){
			Condition condition = new Condition();
			condition.setMessage("For more information, please visit partner’s website");
			condition.setType(Constant.LINK);
			condition.setLinkType(Constant.EXTERNAL_LINK);
			condition.setUrl(couponDetail.getCouponWeb_url().replace("\"", ""));

			switch (language) {
				case Constant.TH:
					condition.setUrlName(couponDetail.getCouponWeb_th().replace("\"", ""));
					break;
				case Constant.MY:
					condition.setUrlName(couponDetail.getCouponWeb_my().replace("\"", ""));
					break;
				case Constant.KM:
					condition.setUrlName(couponDetail.getCouponWeb_km().replace("\"", ""));
					break;
				default :
					condition.setUrlName(couponDetail.getCouponWeb_en().replace("\"", ""));
					break;
			}
			conditionList.add(condition);
		}
		return conditionList;
	}

	private String mapType(ApiGwBurnHistoryRsp result) throws ParseException {
		String mapType = null;
		if(result.getType() != null){
			String type = result.getType();
			if("D".equals(type)){
				mapType = Constant.PRVILEAGE;
			}else if("C".equals(type)){
				mapType = Constant.POINT;
			}
		}
		return mapType;
	}

	private String mapPoint(ApiGwBurnHistoryRsp result) throws ParseException {
		String point = result.getLoyaltyAccount().getLoyaltyBalance().getQuantity().getBalance();
		if(result.getType() != null){
			String type = result.getType();
			if(type.equalsIgnoreCase("C")){
				if(point !=null && !point.equals("0")){
					point = "-"+point;
				}
			}
		}
		return point;
	}

	private String mapCampaignType(List<ApiGwBurnHistoryRsp.Characteristic> characteristics) throws ParseException {
		String campaignType = "";
		if(characteristics!=null&&!characteristics.isEmpty()){
			for(ApiGwBurnHistoryRsp.Characteristic result : characteristics){
				if("buttonText".equals(result.getName())){
					if(Constant.EXPIRED.equals(result.getValue())){
						campaignType = Constant.EXPIRED;
					}else if(Constant.REDEEMED.equals(result.getValue())){
						campaignType = Constant.USED;
					}
				}
			}
		}
		return campaignType;
	}
}