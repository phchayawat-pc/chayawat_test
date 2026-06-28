package th.co.truecorp.commonapi.reward.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import th.co.truecorp.commonapi.reward.common.model.DateRange;
import th.co.truecorp.commonapi.reward.common.model.profile.GetCustPrfByProdIdResAPIGW;
import th.co.truecorp.commonapi.reward.constant.Constant;
import th.co.truecorp.commonapi.reward.model.PagedResult;
import th.co.truecorp.commonlib.jpa.service.ConfigService;
import th.co.truecorp.commonlib.util.SecurityUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

@Component
public class APIGWUtill {

    private static final Logger log = LoggerFactory.getLogger(APIGWUtill.class);

    @Autowired
    private ConfigService configService;

    @Value("${app.private.key.aes256cbc}")
    private String appPrivateKeyAES256CBC;
    @Value("${app.fe.aes256}")
    private String appFeAES256;

    public String generateRewardBackendId() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmssSSS");
        String dateTimePart = currentDateTime.format(formatter);
        log.info("dateTimePart : " + dateTimePart);
        String randomPart = generateRandomString(3);

        return "RWDBE" + dateTimePart + randomPart;
    }

    public String generateRewardRedeemBackendId(String key) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmssSSS");
        String dateTimePart = currentDateTime.format(formatter);
        log.info("dateTimePart : " + dateTimePart);
        String randomPart = generateRandomString(3);

        return key + dateTimePart + randomPart;
    }

    public String generateRewardRedeemBackendIdYYYYMMDD(String key, int random) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String dateTimePart = currentDateTime.format(formatter);
        log.info("dateTimePart : " + dateTimePart);
        String randomPart = generateRandomString(random);

        return key + dateTimePart + "_" + randomPart;
    }

    public String generateRewardRedeemBackendIdyyyyMMddHHmmssSSS(String key) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String dateTimePart = currentDateTime.format(formatter);
        log.info("dateTimePart : " + dateTimePart);
        String randomPart = generateRandomNumberByDigits(3);

        return key + dateTimePart + "_" + randomPart;
    }

    public static String generateRandomNumberByDigits(int digits) {

        if (digits <= 0) {
            return "";
        }

        int lowerBound = (int) Math.pow(10, digits - 1);
        int upperBound = (int) Math.pow(10, digits) - 1;

        Random random = new Random();
        int randomNumber = lowerBound + random.nextInt(upperBound - lowerBound + 1);

        return String.valueOf(randomNumber);

    }

    public String generateRandomString(int length) {
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder randomString = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            randomString.append(characters.charAt(random.nextInt(characters.length())));
        }
        return randomString.toString();
    }

    public String decryptAndEncrypt(String input)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException {
        String decrypted = SecurityUtil.aes256CBCDecryptRandomIV(appFeAES256, input);
        return SecurityUtil.aes256CBCEncryptRandomIV(appPrivateKeyAES256CBC, decrypted);
    }

    public static DateRange createDateRange(String strDate) {
        System.out.println("createDateRange " + strDate);
        DateRange dateRange = new DateRange();
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            int month = Integer.parseInt(strDate.substring(0, 2)) - 1;  // Calendar month is 0-based
            int year = Integer.parseInt(strDate.substring(2, 6));

            Calendar startCal = Calendar.getInstance();
            startCal.set(year, month, 1, 0, 0, 0);
            startCal.set(Calendar.MILLISECOND, 0);

            Calendar endCal = Calendar.getInstance();
            int lastDay = startCal.getActualMaximum(Calendar.DAY_OF_MONTH);
            endCal.set(year, month, lastDay, 23, 59, 59);
            endCal.set(Calendar.MILLISECOND, 0);

            // Use DateFormat to format Date objects obtained from Calendar
            dateRange.setStartDate(dateFormat.format(startCal.getTime()));
            dateRange.setEndDate(dateFormat.format(endCal.getTime()));

        } catch (Exception e) {
            log.error("Exception createDateRange " + e);
        }
        return dateRange;
    }

    public static DateRange createDateRangeFormat(String strDate, String Format) { //strDate = MMyyyy , Format

        DateRange dateRange = new DateRange();
        try {
            int month = Integer.parseInt(strDate.substring(0, 2)) - 1;
            int year = Integer.parseInt(strDate.substring(2, 6));
            Calendar startCal = Calendar.getInstance();
            startCal.set(year, month, 01);
            startCal.set(Calendar.HOUR_OF_DAY, 00);
            startCal.set(Calendar.MINUTE, 00);
            startCal.set(Calendar.SECOND, 00);
            startCal.set(Calendar.MILLISECOND, 00);
            int lastDay = startCal.getActualMaximum(Calendar.DAY_OF_MONTH);
            Calendar endCal = Calendar.getInstance();
            endCal.set(year, month, lastDay);
            endCal.set(Calendar.HOUR_OF_DAY, 23);
            endCal.set(Calendar.MINUTE, 59);
            endCal.set(Calendar.SECOND, 59);
            endCal.set(Calendar.MILLISECOND, 00);

            DateFormat dateFormat = new SimpleDateFormat(Format);

            dateRange.setStartDate(dateFormat.format(startCal.getTime()));
            dateRange.setEndDate(dateFormat.format(endCal.getTime()));

            System.out.println("response createDateRangeFormat " + dateRange);
        } catch (Exception e) {
            System.out.println("Exception createDateRangeFormat " + e);
        }
        return dateRange;
    }

    public boolean isDtac(String brand) {
        return brand.equalsIgnoreCase(Constant.DTAC);
    }

    public String convertToBKKTimeReturnFormat(String gmtDateTime, String format, String returnFormat) {

        if(format == null){
            format = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        }
        if(gmtDateTime != null && !gmtDateTime.equals("")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            LocalDateTime localDateTime = LocalDateTime.parse(gmtDateTime, formatter);
            ZonedDateTime gmtZonedDateTime = localDateTime.atZone(ZoneOffset.UTC);
            ZonedDateTime bkkZonedDateTime = gmtZonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Bangkok"));
            return bkkZonedDateTime.format(DateTimeFormatter.ofPattern(returnFormat));
        } else {
            return null;
        }
    }

    public String convertToReturnFormat(String gmtDateTime, String format, String returnFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime localDateTime = LocalDateTime.parse(gmtDateTime, formatter);
        return localDateTime.format(DateTimeFormatter.ofPattern(returnFormat));
    }

    public String convertTodayFormatYYYYMMDD() {
        try {
            ZonedDateTime todayDate = ZonedDateTime.now(ZoneId.of("Asia/Bangkok"));
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("YYYY-MM-DD'T'HH:mm:ss'Z'");
            return fmt.format(todayDate);
        } catch (Exception e) {
            log.info("Exception convertTodayFormat " + e);
            return null;
        }
    }

    public static <T> PagedResult<T> paginate(List<T> items, int page, int limit) {

        int totalCount = items.size();
        int totalPages = (int) Math.ceil((double) totalCount / limit);

        int skip = (page - 1) * limit;

        if (skip >= items.size()) {
            return new PagedResult<>(List.of(), totalCount, totalPages); // Return an empty list if the starting index is beyond the list size
        }

        int endIndex = Math.min(skip + limit, items.size());

        List<T> paginatedItems = items.subList(skip, endIndex);

        return new PagedResult<>(paginatedItems, totalCount, totalPages);
    }

    public String encryptPhoneNoToApiGw(Map<String, Object> tv) throws Exception {
        try {
            String phoneEnc = null;
            String phoneDec = SecurityUtil.aes256CBCDecryptRandomIV(appFeAES256, tv.get("productId").toString());
            Pattern pattern = Pattern.compile("^[\\x20-\\x7E]*$");
            boolean isString = pattern.matcher(phoneDec).matches();
            if (!isString) {
                phoneDec = SecurityUtil.aes256CBCDecryptRandomIV(appFeAES256, tv.get("productId").toString());
            }
            if (isDtac(tv.get("brand").toString())) {
                if (phoneDec.charAt(0) == '0') {
                    StringBuilder replacePrefix = new StringBuilder(phoneDec);
                    replacePrefix.replace(0, 1, "66");
                    phoneDec = replacePrefix.toString();
                }
            }
            phoneEnc = SecurityUtil.aes256CBCEncryptRandomIV(appPrivateKeyAES256CBC, phoneDec);
            return phoneEnc;
        } catch (Exception e) {
            log.error("Error encrypting phone number", e);
            throw new Exception(e);
        }
    }

}
