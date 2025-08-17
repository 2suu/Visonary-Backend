package esu.visionary.application.user.service;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import esu.visionary.domain.user.model.User;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    private static final String COLLECTION_NAME = "users";

    public boolean existsById(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot document = db.collection(COLLECTION_NAME)
                .document(id)
                .get()
                .get();
        return document.exists();
    }

    // 닉네임 중복 확인: nickName / nickname 어느 쪽으로 저장돼도 탐지
    public boolean existsByNickname(String nickname) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        // 1) nickName 필드 탐색
        Query q1 = db.collection(COLLECTION_NAME)
                .whereEqualTo("nickName", nickname)
                .limit(1);

        QuerySnapshot s1 = q1.get().get();
        if (!s1.isEmpty()) return true;

        // 2) nickname 필드도 혹시 몰라 체크
        Query q2 = db.collection(COLLECTION_NAME)
                .whereEqualTo("nickname", nickname)
                .limit(1);

        QuerySnapshot s2 = q2.get().get();
        return !s2.isEmpty();
    }

    public void saveUser(User user) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        db.collection(COLLECTION_NAME)
                .document(user.getId())
                .set(user)
                .get();
    }

    public User getUserById(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot document = db.collection(COLLECTION_NAME)
                .document(id)
                .get()
                .get();
        if (document.exists()) {
            return document.toObject(User.class);
        }
        return null;
    }
}
