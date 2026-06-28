package th.co.truecorp.commonapi.reward.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import th.co.truecorp.commonapi.reward.cms.jpa.service.CustomMappingMessageService;
import th.co.truecorp.commonapi.reward.cms.jpa.service.ErrorService;
import th.co.truecorp.commonapi.reward.common.model.IdAndType;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.model.Condition;
import th.co.truecorp.commonapi.reward.model.EndpointResultRWD;
import th.co.truecorp.commonapi.reward.model.HistoryPointRsp;
import th.co.truecorp.commonapi.reward.model.ThumbnailRsp;
import th.co.truecorp.commonapi.reward.model.endpoint.ApiGwTransactionHistoryRsp;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.service.APIGWService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class GetTransactionServiceEndpoint {

	private static final Logger log = LoggerFactory.getLogger(GetTransactionServiceEndpoint.class);

	@Autowired
	private LogContextService logContextService;

	@Autowired
	private ResultService resultService;

	@Autowired
	private APIGWService apigwService;

	@Autowired
	private APIGWUtill apigwUtill;

	@Autowired
	private RewardUtill rewardUtill;

	@Autowired
	private ErrorService errorService;

	@Autowired
	private CustomMappingMessageService customMappingMessageService;

	@EndpointLog(name = Constant.ENDPOINT_SOURCE_SYSTEM_ID_APIGW+".getCommonService")
	public EndpointResult getTransaction(Map<String, Object> tv) {

		log.info("start process getTransaction");

		EndpointResult endpointResult = null;
		EndpointResultRWD endpointResult2 = new EndpointResultRWD();
		EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
		try {
			HttpHeaders headers = createHeaders(tv);
			Map<String, Object> pathParams = createPathParams(tv);
			Map<String, Object> queryParams = createQueryParams(tv);
			ResponseEntity<ApiGwTransactionHistoryRsp> gwResponse = apigwService.execute(
					logContext,
					HttpMethod.GET,
					Constant.ENDPOINT_SOURCE_SYSTEM_ID,
					Constant.ENDPOINT_SERVICE_GET_TRANSACTION,
					ApiGwTransactionHistoryRsp.class,
					tv,
					headers,
					pathParams,
					queryParams
			);

			List<HistoryPointRsp> historyPointRsps = new ArrayList<HistoryPointRsp>();

			if (gwResponse != null && !gwResponse.getStatusCode().is2xxSuccessful()) {
				log.info(Constant.ENDPOINT_SERVICE_GET_TRANSACTION + "is not success");
				String errorCode = gwResponse.getBody().getCode();
				String messageApigw = gwResponse.getBody().getMessage();
				String descriptionApigw = gwResponse.getBody().getDescription();
				String businessErrorApigw = gwResponse.getBody().getBusinessError();
				String errorApigw = gwResponse.getBody().getError();
				String timestamp = gwResponse.getBody().getTimestamp() != null ? gwResponse.getBody().getTimestamp() : "";
				String errorMessage = rewardUtill.mapError(descriptionApigw,messageApigw,errorApigw,timestamp);

				endpointResult2 = errorService.mapErrorCode(Constant.QUERY_DATA,
						tv.get("brand").toString(),
						Objects.requireNonNull(errorCode),
						tv.get(ComnConst.KEY_LANGUAGE).toString(),
						errorMessage,
						null != businessErrorApigw ? businessErrorApigw : Constant.N_A,
						Constant.MESSAGE);
				historyPointRsps = null;
				tv.put("historyPointRsps", historyPointRsps);
				tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
				endpointResult = resultService.mapEndpointResultAPIGW(
										tv,
										Constant.ENDPOINT_SOURCE_SYSTEM_ID,
										Constant.ENDPOINT_SERVICE_GET_SHELF_CONTENT,
										errorCode,
										gwResponse.getStatusCode().value(),
										endpointResult2.getEndpointStatusType(),
										endpointResult2.getHttpStatus(),
										endpointResult2.getEndpointResponseCode(),
										errorCode,
										errorMessage
				);
			} else {
				log.info(Constant.ENDPOINT_SERVICE_GET_TRANSACTION + " api success!");
				endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
				if (gwResponse!=null&&!gwResponse.getBody().getResult().isEmpty()) {
					historyPointRsps = mapHistoryPointResponse(tv, gwResponse.getBody().getResult());
				}else{
					endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC, ComnConst.STTS_CODE_SUCC);
				}
				endpointResult2 = errorService.convertMapResult(endpointResult);
				tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
				tv.put("historyPointRsps", historyPointRsps);
				log.info("end process getTransaction");
			}
		} catch (Exception exception) {
			log.error("error in get transaction "+exception.getMessage());
			endpointResult = errorService.mapErrorException(exception,tv);
			endpointResult2 = errorService.convertMapResult(endpointResult);
			tv.put(Constant.ENDPOINT_RESULT_RWD,endpointResult2);
		}
		return endpointResult;
	}

	private HttpHeaders createHeaders(Map<String, Object> tv) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	private Map<String, Object> createPathParams(Map<String, Object> tv) {
		Map<String, Object> pathParams = new HashMap<>();
		return pathParams;
	}

	private Map<String, Object> createQueryParams(Map<String, Object> tv) throws Exception {
		Map<String, Object> queryParams = new HashMap<>();
		Integer offset = Integer.valueOf(tv.get("paging").toString()) - 1;
		String productId = apigwUtill.decryptAndEncrypt(tv.get("productId").toString());
		IdAndType idAndType = rewardUtill.getThaiIdOrPhoneNoForTrue(tv);
		queryParams.put("id", idAndType.getId());
		queryParams.put("identifierType", idAndType.getIdnType());
		queryParams.put("dateFrom", tv.get("dateFrom"));
		queryParams.put("dateTo", tv.get("dateTo"));
		queryParams.put("channel", Constant.TRUE);
		queryParams.put("projectName", "CONTENT");
		queryParams.put("limit", String.valueOf(tv.get("limit")));
		queryParams.put("offset", offset.toString());
		return queryParams;
	}

	private List<HistoryPointRsp> mapHistoryPointResponse(Map<String, Object> tv, List<ApiGwTransactionHistoryRsp.RedeemResult> resultList) throws Exception {
		log.info("map GetTransaction to HistoryPoint");
		String language = tv.get(ComnConst.KEY_LANGUAGE).toString();
		try {
			List<HistoryPointRsp> historyPointRsps = new ArrayList<HistoryPointRsp>();
			if (resultList != null && !resultList.isEmpty()) {
				for (ApiGwTransactionHistoryRsp.RedeemResult result : resultList) {
					HistoryPointRsp historyPointDetail = new HistoryPointRsp();

					String points = String.valueOf(result.getPoints());
					if(result.getContent() != null || !points.equals("0")) {
						String campaignId = result.getContent() != null && !result.getContent().getData().isEmpty()
								&& result.getContent().getData().get(0).getId() != null
								&& !"null".equals(result.getContent().getData().get(0).getId())
								? result.getContent().getData().get(0).getId() : null;
						log.info("map CampaignId : "+campaignId);
						String type = result.getType() != null ? rewardUtill.parseStringNULL(result.getType()) : null;
						String campaignType = mapCampaignType(result.getCouponCode() != null ? result.getCouponCode() : null
								, result.getCampaignDescription() != null ? result.getCouponStatus() : null
								, result.getCouponExpireDate() != null ? result.getCouponExpireDate() : null, type);

						historyPointDetail.setThumbnail(mapThumbnailRsp(result.getContent() != null && result.getContent().getData() != null ? result.getContent().getData() : Collections.emptyList()));
						historyPointDetail.setCampaignName(mapCampaignName(result, language, campaignType, type));
						historyPointDetail.setCampaignDescription(result.getCampaignDescription() != null ? result.getCampaignDescription() : null);
						historyPointDetail.setPoints(String.valueOf(result.getPoints()));
						historyPointDetail.setDate(result.getDate() != null && !result.getDate().isEmpty() ? apigwUtill.convertToBKKTimeReturnFormat(result.getDate(), "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ss") + "+07:00" : null);
						historyPointDetail.setCouponCode(result.getCouponCode() != null && !result.getCouponCode().isEmpty() ? result.getCouponCode() : null);
						historyPointDetail.setCampaignType(campaignType);
						historyPointDetail.setTextButton(customMappingMessageService.getMappingMessage(Constant.MESSAGE_TEXT_BUTTOM, Constant.BUTTON, language, Constant.MESSAGE));
						historyPointDetail.setConditionInfo(mapConditionList(result));
						historyPointDetail.setCampaignId(campaignId);
						historyPointDetail.setCouponExpiryDate(result.getCouponExpireDate() != null && !result.getCouponExpireDate().isEmpty() ? apigwUtill.convertToBKKTimeReturnFormat(result.getCouponExpireDate(), "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ss") + "+07:00" : null);
						historyPointDetail.setType(mapType(result.getContent() != null && result.getContent().getData() != null ? result.getContent().getData() : null, String.valueOf(result.getPoints())));
						historyPointDetail.setTimeCounterFlag(mapTimeCounterFlag(result));
						historyPointRsps.add(historyPointDetail);
					}
				}
			}
			return historyPointRsps;
		}catch (Exception e){
			log.error("Error Map Get Transaction. " + e);
			return null;
		}
	}

	private List<Condition> mapConditionList(ApiGwTransactionHistoryRsp.RedeemResult result) throws Exception {
		List<Condition> conditionList = new ArrayList<Condition>();
		Condition condition = new Condition();
		condition.setType(Constant.TEXT);
		condition.setLinkType(Constant.EXTERNAL_LINK);
		condition.setMessage(result.getMessageOut());
		conditionList.add(condition);

		return conditionList;
	}

	private String mapTimeCounterFlag(ApiGwTransactionHistoryRsp.RedeemResult result) {
		String timeCounterFlag = "N";
		if(result.getContent() != null){
			List<ApiGwTransactionHistoryRsp.ContentData> resultData = result.getContent().getData();
			if (!resultData.isEmpty()) {
				timeCounterFlag = resultData.get(0).getInfo().getTime_counter_show()!=null ? resultData.get(0).getInfo().getTime_counter_show():"N";
			}
		}
		return timeCounterFlag;
	}

	private String mapCampaignName(ApiGwTransactionHistoryRsp.RedeemResult result, String lang, String campaignType, String type) {
		String campaignName = null;
		//			----------new----------------------------------------------------
		if(result.getContent() != null){
			List<ApiGwTransactionHistoryRsp.ContentData> resultData = result.getContent().getData();

			if ("ER".equals(type)) {
				if(lang.equals(Constant.TH)){
					if(!resultData.isEmpty() && resultData.get(0).getInfo().getMerchant_name_th() != null && !"null".equals(resultData.get(0).getInfo().getMerchant_name_th())){
						campaignName =  rewardUtill.parseStringNULL(resultData.get(0).getInfo().getMerchant_name_th());
					}else if(result.getCampaignDescription() != null){
						campaignName =  rewardUtill.parseStringNULL(result.getCampaignDescription());
					}
				}else{
					if(!resultData.isEmpty() && resultData.get(0).getInfo().getMerchant_name_en() != null && !"null".equals(resultData.get(0).getInfo().getMerchant_name_en())){
						campaignName =  rewardUtill.parseStringNULL(resultData.get(0).getInfo().getMerchant_name_en());
					}else if(result.getCampaignDescription() != null){
						campaignName =  rewardUtill.parseStringNULL(result.getCampaignDescription());
					}
				}
			} else {
				if (lang.equals(Constant.TH)) {
					if (!resultData.isEmpty() && resultData.get(0).getInfo().getMerchant_name_th() != null) {
						campaignName =  rewardUtill.parseStringNULL(resultData.get(0).getInfo().getMerchant_name_th());
					}
				} else {
					if (!resultData.isEmpty() && resultData.get(0).getInfo().getMerchant_name_en() != null) {
						campaignName =  rewardUtill.parseStringNULL(resultData.get(0).getInfo().getMerchant_name_en());
					}
				}
			}
		}

//		---old--------------------------------------------------------------
//		if (!resultData.isEmpty()) {
//			campaignName = resultData.get(0).path("info").path("merchant_name_th")!=null ? resultData.get(0).path("info").path("merchant_name_th").asText():null;
//		}
		return campaignName;
	}


	private String mapType(List<ApiGwTransactionHistoryRsp.ContentData> dataNode, String points) throws Exception {
		String type = Constant.POINT;
		if (dataNode != null && !dataNode.isEmpty()) {
			String campaign_type = dataNode.get(0).getCampaign_type() !=null ? dataNode.get(0).getCampaign_type() :null;
			if (campaign_type != null && !campaign_type.equals("null")) {
				type = campaign_type;
			} else {
				type = Constant.PRVILEAGE;
			}
		} else {
			if (points.equals("0")) {
				type = Constant.PRVILEAGE;
			}
		}
		return type;
	}

	private ThumbnailRsp mapThumbnailRsp(List<ApiGwTransactionHistoryRsp.ContentData> thumbListNode) throws Exception {
		ThumbnailRsp thumbnailRsp = new ThumbnailRsp();
		if (!thumbListNode.isEmpty()) {
			thumbnailRsp.setHighlight16x9(thumbListNode.get(0).getThumb_list().getHighlight16x9() !=null ? thumbListNode.get(0).getThumb_list().getHighlight16x9():null);
			thumbnailRsp.setLogo(thumbListNode.get(0).getThumb_list().getThumbnail() !=null ? thumbListNode.get(0).getThumb_list().getThumbnail() :null);
		}
		return thumbnailRsp;
	}

	private String mapCampaignType(String couponCode, String couponStatus, String couponExpireDate, String type) throws ParseException {

		String campaignType = "";
		Date dateCouponExpireDate = null;
		if(couponExpireDate != null && !couponExpireDate.equals("")){
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			dateCouponExpireDate = inputFormat.parse(apigwUtill.convertToBKKTimeReturnFormat(couponExpireDate, "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ss'Z'"));//convertToBKKTimeReturnFormat, convertToReturnFormat
		}
		Date nowDate = new Date();
		if (couponCode != null && !couponCode.equals("")) {
			if ("U".equals(couponStatus)) {
				campaignType = Constant.USED;
			} else if ("E".equals(couponStatus)) {
				if(dateCouponExpireDate != null){
					if (dateCouponExpireDate.before(nowDate)) {
						campaignType = Constant.EXPIRED;
					} else {
						campaignType = Constant.EXPIRE;
					}
				} else {
					campaignType = Constant.EXPIRE;
				}
			} else if ("R".equals(couponStatus)) {
				campaignType = Constant.AVAILABLE;
			}
		} else {
			if ("ER".equals(type)) {
				campaignType = Constant.EARN;
			} else if ("BR".equals(type)) {
				campaignType = Constant.BURN;
			} else {
				campaignType = Constant.AVAILABLE;
			}
		}

		return campaignType;
	}

}
