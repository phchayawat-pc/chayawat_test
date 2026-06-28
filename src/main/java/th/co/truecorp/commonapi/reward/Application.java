package th.co.truecorp.commonapi.reward;


import java.security.Security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import th.co.truecorp.commonlib.configuration.TrueAppConfig;

@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan("th.co.truecorp.*")
@EnableConfigurationProperties(TrueAppConfig.class)
public class Application {
	protected static Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		setSecurity();
		SpringApplication.run(Application.class);
	}
	
	public static void setSecurity()
	{
		Security.setProperty(
                "jdk.certpath.disabledAlgorithms",
                "MD2, MD5 keySize < 768, SHA1 jdkCA & usage TLSServer, RSA keySize < 1024, DSA keySize < 1024, EC keySize < 224"
        );
        Security.setProperty(
                "jdk.tls.disabledAlgorithms",
                "SSLv3, RC4, MD5withRSA keySize < 768, DH keySize < 1024, EC keySize < 224, DES40_CBC, RC4_40, 3DES_EDE_CBC"
        );
		System.setProperty("https.protocols", "TLSv1,TLSv1.2");
	}

}
