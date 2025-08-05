package esu.visionary.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FirebaseUserService {

    private static final String COLLECTION_NAME = "users";

    public void saveUser(Long userId, Map<String, Object> userData) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            ApiFuture<WriteResult> future = db
                    .collection(COLLECTION_NAME)
                    .document(String.valueOf(userId))
                    .set(userData);

            WriteResult result = future.get();
            System.out.println("✅ Firebase 유저 저장 완료: " + result.getUpdateTime());

        } catch (Exception e) {
            System.err.println("❌ Firebase 유저 저장 실패");
            e.printStackTrace();
        }
    }
}
