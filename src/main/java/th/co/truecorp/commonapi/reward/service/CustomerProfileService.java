package th.co.truecorp.commonapi.reward.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.entity.RwdSystemConfig;
import th.co.truecorp.commonapi.reward.cms.jpa.service.RwdStaticContentDetailService;
import th.co.truecorp.commonapi.reward.cms.jpa.service.RwdSystemConfigService;
import th.co.truecorp.commonapi.reward.common.model.CommonTrueProfileRsp;
import th.co.truecorp.commonapi.reward.common.model.Points;
import th.co.truecorp.commonapi.reward.common.utils.APIGWUtill;
import th.co.truecorp.commonapi.reward.common.utils.RewardUtill;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.dto.ProfileRwdStaticContentDetailDto;
import th.co.truecorp.commonapi.reward.model.CustomerProfileRsp;
import th.co.truecorp.commonapi.reward.model.ProfileHowToUseRsp;
import th.co.truecorp.commonapi.reward.model.ProfileTierDetailRsp;
import th.co.truecorp.commonlib.constant.ComnConst;
import th.co.truecorp.commonlib.jpa.service.ResultService;
import th.co.truecorp.commonlib.log.model.EndpointResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomerProfileService {
    private static Logger log = LoggerFactory.getLogger(CustomerProfileService.class);
    Gson gson = new Gson();

    @Autowired
    private RewardUtill rewardUtill;

    @Autowired
    private APIGWUtill apigwUtill;

    @Autowired
    private ResultService resultService;

    @Autowired
    private RwdSystemConfigService rwdSystemConfigService;

    @Autowired
    private RwdStaticContentDetailService rwdStaticContentDetailService;

    public CustomerProfileRsp getCustProfileService(CustomerProfileRsp commonProfileRsp) {

        CustomerProfileRsp customerProfileRsp = new CustomerProfileRsp();
        CustomerProfileRsp.MyPoint myPoint = new CustomerProfileRsp.MyPoint();
        Integer totalPoint = 0;
        List<Points> point = new ArrayList<>();
        CommonTrueProfileRsp.LoyaltyProgramMember loyaltyProgramMember = new CommonTrueProfileRsp.LoyaltyProgramMember();
        if (commonProfileRsp != null && commonProfileRsp.getMyPoint() != null) {
            if (commonProfileRsp.getMyPoint().getTotalPoint() != null) {
                totalPoint = commonProfileRsp.getMyPoint().getTotalPoint();
            }

            if (commonProfileRsp.getMyPoint().getPoints() != null) {
                point = commonProfileRsp.getMyPoint().getPoints();
            }

            loyaltyProgramMember = commonProfileRsp.getMyPoint().getLoyaltyProgramMember();

        }

        myPoint.setTotalPoint(totalPoint);
        myPoint.setPoints(point);
        myPoint.setLoyaltyProgramMember(loyaltyProgramMember);

        customerProfileRsp.setName(commonProfileRsp != null ? commonProfileRsp.getName() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
        customerProfileRsp.setId(commonProfileRsp != null ? commonProfileRsp.getId() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
        customerProfileRsp.setCardType(commonProfileRsp != null ? commonProfileRsp.getCardType() : Constant.DEFAULT_NULL_EXCEPTION_VALUE);
        customerProfileRsp.setMyPoint(myPoint);
        return customerProfileRsp;
    }

    public EndpointResult getProfileHowToUseService(Map<String, Object> tv) throws Exception {

        tv.put("lang", rewardUtill.handleLanguage(tv.get("lang").toString()));
        ProfileHowToUseRsp howToUseRsp = new ProfileHowToUseRsp();
        EndpointResult endpointResult = null;
        try {
            String brand = tv.get("brand") != null ? tv.get("brand").toString() : "";
            String lang = tv.get("lang") != null ? tv.get("lang").toString().toUpperCase() : "";
            String value = "";
            try {
                Optional<RwdSystemConfig> optional = rwdSystemConfigService.findConfigs("HOW_TO_USED", apigwUtill.isDtac(brand) ? "DTAC" : "TRUE");
                RwdSystemConfig rwdSystemConfig = optional.get();
                value = rwdSystemConfig.getValue();
            } catch (Exception e) {
                log.info("Error gat RwdSystemConfig : HOW_TO_USED : " + brand + " , " + e.getMessage());
                endpointResult = resultService.getEndpointExceptionResult(tv, e);
            }

            List<ProfileRwdStaticContentDetailDto> dtos = rwdStaticContentDetailService.findContentIdAndLang(value, lang);

            if (dtos != null && !dtos.isEmpty()) {

                StringBuilder titleBuilder = new StringBuilder();
                StringBuilder descBuilder = new StringBuilder();
                StringBuilder imageBuilder = new StringBuilder();

                for (ProfileRwdStaticContentDetailDto dto : dtos) {
                    titleBuilder.append(Optional.ofNullable(dto.gettopic()).orElse("").replace("\n", ""));
                    descBuilder.append(Optional.ofNullable(dto.getdescription()).orElse("").replace("\n", ""));
                    imageBuilder.append(Optional.ofNullable(dto.getimage()).orElse(""));
                }

                String title = titleBuilder.toString();
                String desc = descBuilder.toString();
                String image = imageBuilder.toString();

                howToUseRsp.setTitle(!title.isEmpty() ? title : null);
                howToUseRsp.setDesc(!desc.isEmpty() ? desc : null);
                howToUseRsp.setImage(!image.isEmpty() ? image : null);

                log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + " is success");
                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
            } else {
                howToUseRsp = null;
                log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + "is not success");
                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC, ComnConst.STTS_CODE_SUCC);
            }

            log.info("set howToUse : " + gson.toJson(howToUseRsp));
            tv.put(Constant.TRANSACTION_RESPONSE_KEY, howToUseRsp);
        } catch (Exception e) {
            log.info("Error is getProfileHowToUseService : " + e.getMessage());
            endpointResult = resultService.getEndpointExceptionResult(tv, e);
            return endpointResult;
        }

        return endpointResult;
    }

    public EndpointResult getProfileTierDetailService(Map<String, Object> tv) throws Exception {

        tv.put("lang", rewardUtill.handleLanguage(tv.get("lang").toString()));
        List<ProfileTierDetailRsp> profileTierDetailRspList = new ArrayList<>();
        EndpointResult endpointResult = null;
        try {
            String brand = tv.get("brand") != null ? tv.get("brand").toString() : "";
            String lang = tv.get("lang") != null ? tv.get("lang").toString().toUpperCase() : "";
            String value = "";
            try {
                Optional<RwdSystemConfig> optional = rwdSystemConfigService.findConfigs("MY_TIER", apigwUtill.isDtac(brand) ? "DTAC" : "TRUE");
                RwdSystemConfig rwdSystemConfig = optional.get();
                value = rwdSystemConfig.getValue();
            } catch (Exception e) {
                log.info("Error gat RwdSystemConfig : MY_TIER : " + brand + " , " + e.getMessage());
                endpointResult = resultService.getEndpointExceptionResult(tv, e);
            }

            List<ProfileRwdStaticContentDetailDto> dtos = rwdStaticContentDetailService.findContentIdAndLang(value, lang);

            if (dtos != null && !dtos.isEmpty()) {
                for (ProfileRwdStaticContentDetailDto dto : dtos) {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        ProfileTierDetailRsp rsp = objectMapper.readValue(dto.getdescription(), ProfileTierDetailRsp.class);
                        profileTierDetailRspList.add(rsp);
                    } catch (Exception e) {
                        log.info("Error map JsonString to  Object ProfileTierDetail ," + e.getMessage());
                        endpointResult = resultService.getEndpointExceptionResult(tv, e);
                    }
                }

                log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + "is success");
                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC_WITH_DATA, ComnConst.STTS_CODE_SUCC_WITH_DATA);
            } else {
                profileTierDetailRspList = null;
                log.info(Constant.ENDPOINT_SERVICE_GET_SHELF + "is not success");
                endpointResult = resultService.findEndpointResult(tv, ComnConst.STTS_CODE_SUCC, ComnConst.STTS_CODE_SUCC);
            }
            log.info("set tier detail : " + gson.toJson(profileTierDetailRspList));
            tv.put(Constant.TRANSACTION_RESPONSE_KEY, profileTierDetailRspList);
        } catch (Exception e) {
            log.info("Error is getProfileTierDetailService : " + e.getMessage());
            endpointResult = resultService.getEndpointExceptionResult(tv, e);
            return endpointResult;
        }
        return endpointResult;
    }

}
