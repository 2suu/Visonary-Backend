package esu.visionary.bootstrap.config;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SmsProperties.class)
public class CoolSmsConfig {

    @Bean
    public DefaultMessageService coolsmsMessageService(SmsProperties props) {
        // props.enabled 가 false 여도 Bean은 만들어 두고, 실제 전송은 서비스에서 분기
        return NurigoApp.INSTANCE.initialize(props.getApiKey(), props.getApiSecret(), props.getBaseUrl());
    }
}
