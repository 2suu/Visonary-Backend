package esu.visionary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class VisionaryApplication {
    public static void main(String[] args) {
        SpringApplication.run(VisionaryApplication.class, args);
    }
}
