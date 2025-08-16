//package esu.visionary;
//
//import org.junit.jupiter.api.TestInstance;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//
//import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.containers.GenericContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//@SpringBootTest
//@ExtendWith(SpringExtension.class)
//@Testcontainers
//@ActiveProfiles("test")
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public abstract class IntegrationTestSupport {
//
//    // MySQL 8 컨테이너
//    @Container
//    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
//            .withDatabaseName("visionary")
//            .withUsername("test")
//            .withPassword("test");
//
//    // Redis 컨테이너
//    @Container
//    static final GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
//            .withExposedPorts(6379);
//
//    @DynamicPropertySource
//    static void registerProps(DynamicPropertyRegistry r) {
//        // DataSource
//        r.add("spring.datasource.url", mysql::getJdbcUrl);
//        r.add("spring.datasource.username", mysql::getUsername);
//        r.add("spring.datasource.password", mysql::getPassword);
//
//        // Redis
//        r.add("spring.data.redis.host", redis::getHost);
//        r.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
//
//        // JPA/Flyway
//        r.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
//        r.add("spring.flyway.enabled", () -> true);
//        r.add("spring.flyway.locations", () -> "classpath:db/migration");
//    }
//}
