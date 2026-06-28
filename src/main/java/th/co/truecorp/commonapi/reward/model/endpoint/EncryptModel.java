package th.co.truecorp.commonapi.reward.model.endpoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EncryptModel {
    String encrypt;
    String decrypt;
    String env;
    String decryptFE;
    String encryptFE;
    String decryptRandom;
}
