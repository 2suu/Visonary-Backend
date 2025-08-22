package esu.visionary.bootstrap.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;

@Configuration
@Profile("!test") // 테스트 프로필 제외
public class FirebaseConfig {

    // application.yml 설정 값들
    @Value("${firebase.service-account-json-base64:}")
    private String serviceAccountJsonBase64;

    @Value("${firebase.service-account-json-path:}")
    private String serviceAccountJsonPath;        // 예: file:/etc/secrets/firebase.json 또는 절대경로

    @Value("${firebase.classpath-json:firebase/firebase-service-account.json}")
    private String classpathJson;                 // 예: src/main/resources/firebase/firebase-service-account.json

    @Value("${firebase.use-google-default-credentials:true}")
    private boolean useGoogleDefaultCredentials;  // GCP 환경에서 ADC 사용 여부

    @Bean
    public FirebaseApp firebaseApp() throws Exception {
        GoogleCredentials credentials = resolveCredentials();

        // (필요 시) 스코프 추가 — Firestore/Storage 등
        try {
            // 일부 환경/버전에서 createScopedRequired가 사라졌다면 아래 블록은 제거 가능
            //noinspection deprecation
            if (credentials.createScopedRequired()) {
                credentials = credentials.createScoped("https://www.googleapis.com/auth/cloud-platform");
            }
        } catch (NoSuchMethodError ignored) {
            // 최신 라이브러리에서는 필요없을 수 있음
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }

    @Bean
    public Firestore firestore(FirebaseApp app) {
        return FirestoreClient.getFirestore(app);
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp app) {
        return FirebaseAuth.getInstance(app);
    }

    /**
     * 자격증명 탐색 우선순위:
     * 1) BASE64 (firebase.service-account-json-base64)
     * 2) 파일 경로 (firebase.service-account-json-path) - "file:" 접두어 허용
     * 3) 클래스패스 (firebase.classpath-json)
     * 4) ADC (use-google-default-credentials=true)
     */
    private GoogleCredentials resolveCredentials() throws Exception {
        // 1) BASE64
        if (serviceAccountJsonBase64 != null && !serviceAccountJsonBase64.isBlank()) {
            byte[] decoded = Base64.getDecoder().decode(serviceAccountJsonBase64);
            try (InputStream in = new ByteArrayInputStream(decoded)) {
                return GoogleCredentials.fromStream(in);
            }
        }

        // 2) 파일 경로
        if (serviceAccountJsonPath != null && !serviceAccountJsonPath.isBlank()) {
            String path = serviceAccountJsonPath.startsWith("file:")
                    ? serviceAccountJsonPath.substring("file:".length())
                    : serviceAccountJsonPath;
            try (InputStream in = new FileInputStream(path)) {
                return GoogleCredentials.fromStream(in);
            }
        }

        // 3) 클래스패스
        if (classpathJson != null && !classpathJson.isBlank()) {
            String cp = classpathJson.startsWith("classpath:")
                    ? classpathJson.substring("classpath:".length())
                    : classpathJson;

            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(cp);
            if (in != null) {
                try (in) {
                    return GoogleCredentials.fromStream(in);
                }
            }
        }

        // 4) ADC
        if (useGoogleDefaultCredentials) {
            return GoogleCredentials.getApplicationDefault();
        }

        throw new IllegalStateException(
                "Firebase credentials not found. " +
                        "Set one of: service-account-json-base64, service-account-json-path, classpath-json, " +
                        "or enable use-google-default-credentials=true."
        );
    }
}
