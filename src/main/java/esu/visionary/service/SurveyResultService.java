package esu.visionary.service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class SurveyResultService {

    private static final String COLLECTION_NAME = "survey_results";

    public String saveSurveyResult(Long userId, Map<String, Object> surveyData) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            db.collection(COLLECTION_NAME)
                    .document(String.valueOf(userId))
                    .set(surveyData)
                    .get();
            return "설문 저장 성공";
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return "설문 저장 실패: " + e.getMessage();
        }
    }
}
