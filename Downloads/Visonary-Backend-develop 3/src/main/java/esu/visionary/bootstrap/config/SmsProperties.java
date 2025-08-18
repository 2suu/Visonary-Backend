package esu.visionary.bootstrap.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "coolsms")
public class SmsProperties {
    private boolean enabled = false;
    private String apiKey;
    private String apiSecret;
    private String from;
    private String baseUrl = "https://api.coolsms.co.kr";
}
