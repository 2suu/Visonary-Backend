package esu.visionary;

import esu.visionary.bootstrap.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ImportAutoConfiguration(exclude = {SecurityConfig.class})
class VisionaryApplicationTests{
	@Test void contextLoads() {}
}
