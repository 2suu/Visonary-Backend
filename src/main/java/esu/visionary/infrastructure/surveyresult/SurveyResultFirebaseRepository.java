package esu.visionary.infrastructure.surveyresult;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.FirebaseApp;
import esu.visionary.common.exception.InternalServerErrorException;
import esu.visionary.domain.surveyresult.BasicProfileDoc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SurveyResultFirebaseRepository {

    private final Firestore firestore; // com.google.cloud.firestore.Firestore
    private static final String COLLECTION = "survey_analysis_results"; // 스키마와 일치

    // Big5 전용: 표준 경로만 본다
    public Optional<Map<String, Object>> getBig5Root(Long sessionId) {
        String sid = String.valueOf(sessionId);
        try {
            DocumentSnapshot doc = firestore.collection(COLLECTION).document(sid).get().get();
            if (!doc.exists()) return Optional.empty();
            return Optional.ofNullable(doc.getData());
        } catch (Exception e) {
            log.warn("[Big5] read error {}/{} : {}", COLLECTION, sid, e.toString());
            return Optional.empty();
        }
    }

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

    // (임시 호환) 기존 프로브 함수도 첫 번째로 실제 컬렉션을 보게 수정
    public Optional<Map<String, Object>> getSessionDoc(Integer sessionId) {
        String sid = String.valueOf(sessionId);

        // 🔹 실제 사용하는 컬렉션부터 확인
        Optional<Map<String,Object>> hit = getByPath(COLLECTION, sid);
        if (hit.isPresent()) { log.info("[Big5][probe] hit path=/{}/{}", COLLECTION, sid); return hit; }

        // 이하 예전 추정 경로들(남겨두고 점진 제거)
        hit = getByPath("survey-results", sid);
        if (hit.isPresent()) { log.info("[Big5][probe] hit path=/survey-results/{}", sid); return hit; }
        hit = getByPath("survey_results", sid);
        if (hit.isPresent()) { log.info("[Big5][probe] hit path=/survey_results/{}", sid); return hit; }
        hit = getByPath("survey-sessions", sid);
        if (hit.isPresent()) { log.info("[Big5][probe] hit path=/survey-sessions/{}", sid); return hit; }
        hit = getByPath("surveySessions", sid);
        if (hit.isPresent()) { log.info("[Big5][probe] hit path=/surveySessions/{}", sid); return hit; }

        hit = queryBySessionId("survey-results", sessionId);
        if (hit.isPresent()) { log.info("[Big5][probe] hit query collection=survey-results sessionId={}", sessionId); return hit; }
        hit = queryBySessionId("survey_results", sessionId);
        if (hit.isPresent()) { log.info("[Big5][probe] hit query collection=survey_results sessionId={}", sessionId); return hit; }

        hit = getSubDoc("survey-sessions", sid, "results", "big5");
        if (hit.isPresent()) { log.info("[Big5][probe] hit subdoc=/survey-sessions/{}/results/big5", sid); return hit; }

        log.warn("[Big5][probe] no doc found for sessionId={} (after checking real collection too)", sessionId);
        return Optional.empty();
    }

    private Optional<Map<String,Object>> getByPath(String col, String docId) {
        try {
            DocumentSnapshot s = firestore.collection(col).document(docId).get().get();
            return s.exists() ? Optional.of(s.getData()) : Optional.empty();
        } catch (Exception e) {
            log.debug("[Big5][probe] error reading {}/{} : {}", col, docId, e.toString());
            return Optional.empty();
        }
    }

    private Optional<Map<String,Object>> queryBySessionId(String col, Integer sessionId) {
        try {
            // 숫자/문자 둘 다 시도
            QuerySnapshot q1 = firestore.collection(col)
                    .whereEqualTo("sessionId", sessionId).limit(1).get().get();
            if (!q1.isEmpty()) return Optional.of(q1.getDocuments().get(0).getData());

            QuerySnapshot q2 = firestore.collection(col)
                    .whereEqualTo("sessionId", String.valueOf(sessionId)).limit(1).get().get();
            if (!q2.isEmpty()) return Optional.of(q2.getDocuments().get(0).getData());
        } catch (Exception e) {
            log.debug("[Big5][probe] error query {}: {}", col, e.toString());
        }
        return Optional.empty();
    }

    private Optional<Map<String,Object>> getSubDoc(String col, String docId, String sub, String subDocId) {
        try {
            DocumentSnapshot s = firestore.collection(col).document(docId)
                    .collection(sub).document(subDocId).get().get();
            return s.exists() ? Optional.of(s.getData()) : Optional.empty();
        } catch (Exception e) {
            log.debug("[Big5][probe] error subdoc {}/{}/{}/{} : {}", col, docId, sub, subDocId, e.toString());
            return Optional.empty();
        }
    }
}
