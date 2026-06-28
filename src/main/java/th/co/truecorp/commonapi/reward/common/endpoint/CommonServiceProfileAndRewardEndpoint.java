package th.co.truecorp.commonapi.reward.common.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import th.co.truecorp.commonapi.reward.common.model.CommonTrueProfileRsp;
import th.co.truecorp.commonapi.reward.common.model.GetDigitalByDigitalIdResponse;
import th.co.truecorp.commonapi.reward.common.model.Points;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.model.CustomerProfileRsp;
import th.co.truecorp.commonapi.reward.model.ProfileAndRewardRsp;
import th.co.truecorp.commonapi.reward.model.ServiceProfileRsp;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.annotation.EndpointLog;
import th.co.truecorp.commonlib.log.context.EndpointLogContext;
import th.co.truecorp.commonlib.log.context.LogContextService;
import th.co.truecorp.commonlib.log.model.EndpointResult;
import th.co.truecorp.commonlib.service.CommonBEService;
import th.co.truecorp.commonlib.util.SecurityUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class CommonServiceProfileAndRewardEndpoint {

    private static final Logger log = LoggerFactory.getLogger(CommonServiceProfileAndRewardEndpoint.class);

    @Autowired
    private LogContextService logContextService;

    @Autowired
    private ResultService resultService;

    @Autowired
    private CommonBEService commonBEService;

    @Value("${app.fe.aes256}")
    private String aes256Key;

    @EndpointLog(name = Constant.ENDPOINT_SOURCE_SYSTEM_ID_COMMONBE + ".getCommonServiceProfileAndReward")
    public EndpointResult getCommonServiceProfileAndReward(Map<String, Object> tv) {
        EndpointLogContext logContext = logContextService.getEndpointLoggingContext();
        try {

            HttpHeaders headers = createHeaders(tv);
            Map<String, Object> pathParams = createPathParams(tv);
            Map<String, Object> queryParams = createQueryParams();
            EndpointResult endpointResult = null;
            CustomerProfileRsp commonProfileRsp = new CustomerProfileRsp();
            ServiceProfileRsp serviceProfileRsp = new ServiceProfileRsp();
            ProfileAndRewardRsp profileAndRewardRsp = new ProfileAndRewardRsp();

            ResponseEntity<GetDigitalByDigitalIdResponse> response = commonBEService.execute(
                    logContext,
                    HttpMethod.GET,
                    Constant.ENDPOINT_SOURCE_SYSTEM_ID,
                    "GetProfileFromDigitalID",
                    GetDigitalByDigitalIdResponse.class,
                    tv,
                    headers,
                    pathParams,
                    queryParams
            );

//            log.info("Response from profile API: {}", response);
            GetDigitalByDigitalIdResponse.Reward dataReward = response.getBody().getData().getReward();
            log.info("Response from dataReward API: {}", dataReward);
            GetDigitalByDigitalIdResponse.Profile dataProfile = response.getBody().getData().getProfile();
            log.info("Response from dataProfile API: {}", dataProfile);
            if (!ComnConst.STTS_TYPE_SUCC.equals(dataReward.getStatus().getStatusType())) {
                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC, ComnConst.STTS_MSSG_SUCC);
                endpointResult.setHttpStatus(Objects.equals(dataReward.getStatus().getStatusType(), "B") ? 400 : 500);
                endpointResult.setEndpointStatusType(dataReward.getStatus().getStatusType());
                endpointResult.setEndpointStatusCode(dataReward.getStatus().getStatusType() + "-RWD-" + dataReward.getStatus().getErrorCode());
                endpointResult.setEndpointErrorMessage(dataReward.getStatus().getErrorMessage());
                endpointResult.setEndpointErrorDescription(dataReward.getStatus().getErrorDescription());
                profileAndRewardRsp.setCustomerProfileRsp(null);
                profileAndRewardRsp.setServiceProfileRsp(null);
            } else {
                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
                commonProfileRsp = parseRewardProfileResponse(dataReward.getData(), tv);
                serviceProfileRsp = parseProfileResponse(dataProfile, tv);

                profileAndRewardRsp.setCustomerProfileRsp(commonProfileRsp);
                profileAndRewardRsp.setServiceProfileRsp(serviceProfileRsp);
            }
            logContext.putA("convert", Thread.currentThread().getName());
            tv.put("profileAndRewardRspEndpoint", profileAndRewardRsp);

            log.info("Response error code: {}", dataReward.getStatus().getErrorCode());
            return endpointResult;

        } catch (Exception exception) {
            log.error("Error during execute apigw: {}", "GetProfileFromDigitalID", exception);
            return resultService.getEndpointExceptionResult(tv, exception);
        }
    }

    private HttpHeaders createHeaders(Map<String, Object> tv) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private Map<String, Object> createPathParams(Map<String, Object> tv) {
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("digitalId", tv.get("digitalId"));
        return pathParams;
    }

    private Map<String, Object> createQueryParams() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("profileType", "profile,reward");
        return queryParams;
    }

    private ServiceProfileRsp parseProfileResponse(GetDigitalByDigitalIdResponse.Profile dataProfile, Map<String, Object> tv) throws Exception {

        ServiceProfileRsp serviceProfileRsp = new ServiceProfileRsp();
        String brand = Objects.toString(tv.get("productBrand"), Objects.toString(tv.get("brand"), "")).toLowerCase();

        ServiceProfileRsp.status status = new ServiceProfileRsp.status();
        status.setStatusType(dataProfile.getStatus().getStatusType());
        status.setErrorCode(dataProfile.getStatus().getErrorCode());
        status.setErrorMessage(dataProfile.getStatus().getErrorMessage());
        status.setErrorDescription(dataProfile.getStatus().getErrorDescription());
        status.setHostId(dataProfile.getStatus().getHostId());
        status.setTransactionId(dataProfile.getStatus().getTransactionId());


        serviceProfileRsp.setStatus(status);
        serviceProfileRsp.setDigitalId(dataProfile.getDigitalId());
        serviceProfileRsp.setProductId(dataProfile.getProductId());
        serviceProfileRsp.setProductBrand(dataProfile.getProductBrand());
        serviceProfileRsp.setProductType(dataProfile.getProductType());
        serviceProfileRsp.setProductStatus(dataProfile.getProductStatus());
        serviceProfileRsp.setProductSegment(dataProfile.getProductSegment());
        serviceProfileRsp.setCustomerIdentity(dataProfile.getCustomerIdentity());
        serviceProfileRsp.setCustomerIdentityType(dataProfile.getCustomerIdentityType());
        serviceProfileRsp.setCustomerType(dataProfile.getCustomerType());
        serviceProfileRsp.setCustomerNumber(dataProfile.getCustomerNumber());
        serviceProfileRsp.setBcBan(dataProfile.getBcBan());
        serviceProfileRsp.setBan(dataProfile.getBan());
        serviceProfileRsp.setMultiSimLevel(dataProfile.getMultiSimLevel());
        serviceProfileRsp.setMainConvergenceCode(dataProfile.getMainConvergenceCode());
        serviceProfileRsp.setAssetGroupId(dataProfile.getAssetGroupId());
        serviceProfileRsp.setMultiSimIndicator(dataProfile.getMultiSimIndicator());

        return serviceProfileRsp;
    }

    private CustomerProfileRsp parseRewardProfileResponse(GetDigitalByDigitalIdResponse.RewardData dataReward, Map<String, Object> tv) throws Exception {

        CustomerProfileRsp commonProfileRsp = new CustomerProfileRsp();
        String brand = Objects.toString(tv.get("productBrand"), Objects.toString(tv.get("brand"), "")).toLowerCase();
        List<Points> pointList = parsePoints(dataReward.getMyPoint().getPoints(), dataReward.getMyPoint().getTotalPoint(), brand);

        CustomerProfileRsp.MyPoint myPoint = new CustomerProfileRsp.MyPoint();
        myPoint.setTotalPoint(dataReward.getMyPoint().getTotalPoint());
        myPoint.setPoints(pointList);
        myPoint.setLoyaltyProgramMember(parseLoyaltyProgramMember(dataReward.getMyPoint()));

        String phoneNo = decryptPhoneNo(tv);

        commonProfileRsp.setName((null != dataReward.getCardName() && !Objects.equals(dataReward.getCardName(), "null")) ? dataReward.getCardName() : null);
        commonProfileRsp.setId(Objects.equals(brand, "dtac") ? phoneNo : dataReward.getCard().getNo());
        commonProfileRsp.setCardType(dataReward.getCard().getType());
        commonProfileRsp.setMyPoint(myPoint);
        commonProfileRsp.setTypeCard(dataReward.getTypeCard());

        return commonProfileRsp;
    }

    private CommonTrueProfileRsp.LoyaltyProgramMember parseLoyaltyProgramMember(GetDigitalByDigitalIdResponse.MyPoint dataMyPoint) {
        if (dataMyPoint.getLoyaltyProgramMember() != null) {
            CommonTrueProfileRsp.LoyaltyProgramMember loyaltyProgramMember = new CommonTrueProfileRsp.LoyaltyProgramMember();
            loyaltyProgramMember.setId(dataMyPoint.getLoyaltyProgramMember().getId());
            loyaltyProgramMember.setHref(dataMyPoint.getLoyaltyProgramMember().getHref());
            return loyaltyProgramMember;
        }
        return null;
    }

    private String decryptPhoneNo(Map<String, Object> tv) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        try {
//          log.info(" decryptPhoneNo aes256Key = "+aes256Key+"| value = "+SecurityUtil.aes256CBCDecryptRandomIV(aes256Key, tv.get("productId").toString()).replace("66", "0"));
            String phoneDec = SecurityUtil.aes256CBCDecryptRandomIV(aes256Key, tv.get("productId").toString()).replace("66", "0");
            Pattern pattern = Pattern.compile("^[\\x20-\\x7E]*$");
            boolean isString = pattern.matcher(phoneDec).matches();
            if (!isString) {
                log.info("phoneDec = " + phoneDec);
                phoneDec = SecurityUtil.aes256CBCDecryptRandomIV(aes256Key, tv.get("productId").toString()).replace("66", "0");
            }
            return phoneDec;
        } catch (Exception e) {
            log.error("Error decrypting phone number", e);
            return SecurityUtil.aes256CBCDecryptRandomIV(aes256Key, tv.get("productId").toString()).replace("66", "0");
        }
    }

    private List<Points> parsePoints(List<GetDigitalByDigitalIdResponse.PointDetail> pointsArray, int totalPoint, String brand) {
        List<Points> pointList = new ArrayList<>();
        if (!pointsArray.isEmpty()) {
            for (GetDigitalByDigitalIdResponse.PointDetail pointNode : pointsArray) {
                Points pointDetail = new Points();
                pointDetail.setPoint(brand.equalsIgnoreCase(Constant.DTAC) ? totalPoint : Integer.parseInt(pointNode.getPoints()));
                pointDetail.setExpiredAt(formatExpirationDate(pointNode.getExpirationDate()));
                pointList.add(pointDetail);
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        // Sort the pointList based on expiredAt
        Collections.sort(pointList, new Comparator<Points>() {
            @Override
            public int compare(Points a, Points b) {
                try {
                    Date aDate = dateFormat.parse(a.getExpiredAt());
                    Date bDate = dateFormat.parse(b.getExpiredAt());
                    return aDate.compareTo(bDate);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return pointList;
    }

    private static String formatExpirationDate(String expirationDateStr) {
        LocalDate expirationDate = LocalDate.parse(expirationDateStr);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return expirationDate.format(formatter);
    }

}