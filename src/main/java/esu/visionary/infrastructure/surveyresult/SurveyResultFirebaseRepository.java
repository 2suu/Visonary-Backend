package esu.visionary.infrastructure.surveyresult;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import esu.visionary.common.exception.InternalServerErrorException;
import esu.visionary.domain.surveyresult.BasicProfileDoc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class SurveyResultFirebaseRepository {

    private final Firestore firestore; // com.google.cloud.firestore.Firestore
    private static final String COLLECTION = "survey_analysis_results"; // 스키마와 일치

    public Optional<BasicProfileDoc> getBasicProfile(Long surveySessionId) {
        try {
            // 루트 문서에서 basic_profile 맵 필드 읽기
            DocumentSnapshot root = firestore
                    .collection(COLLECTION)
                    .document(String.valueOf(surveySessionId))
                    .get().get();

            if (!root.exists()) return Optional.empty();

            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) root.get("basic_profile");
            if (map == null || map.isEmpty()) return Optional.empty();

            BasicProfileDoc doc = BasicProfileDoc.fromMap(map);
            return Optional.of(doc);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InternalServerErrorException("Firebase interrupted", e);
        } catch (ExecutionException e) {
            throw new InternalServerErrorException("Firebase execution error", e);
        }
    }
}

