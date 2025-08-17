package esu.visionary.bootstrap.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@Configuration
@Profile("!test") // 테스트 프로필 제외
public class FirebaseConfig {

    @Value("${firebase.service-account-json-path:}")
    private String serviceAccountJsonPath;

    @Bean
    public FirebaseApp firebaseApp() throws Exception {
        GoogleCredentials credentials;

        if (serviceAccountJsonPath != null && !serviceAccountJsonPath.isBlank()) {
            String path = serviceAccountJsonPath.startsWith("file:")
                    ? serviceAccountJsonPath.substring("file:".length())
                    : serviceAccountJsonPath;
            try (InputStream in = new FileInputStream(path)) {
                credentials = GoogleCredentials.fromStream(in);
            }
        } else {
            credentials = GoogleCredentials.getApplicationDefault()
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }

    @Bean public Firestore firestore(FirebaseApp app) { return FirestoreClient.getFirestore(app); }
    @Bean public FirebaseAuth firebaseAuth(FirebaseApp app) { return FirebaseAuth.getInstance(app); }
}
