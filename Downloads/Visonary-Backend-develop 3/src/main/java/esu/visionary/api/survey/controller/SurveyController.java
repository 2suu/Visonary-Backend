// src/main/java/esu/visionary/api/survey/controller/SurveyController.java
package esu.visionary.api.survey.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import esu.visionary.api.survey.request.SurveyResultRequest;
import esu.visionary.api.survey.request.SurveyResultRequest.TraitAnswer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping(value = "/api/survey", produces = MediaType.APPLICATION_JSON_VALUE)
public class SurveyController {

    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping(value = "/submit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveSurveyResult(@RequestBody SurveyResultRequest request) {
        try {
            // createdAt 서버에서 자동 세팅 (클라에서 안 보내도 됨)
            if (request.getCreatedAt() == null) {
                request.setCreatedAt(LocalDateTime.now());
            }

            // 성향 점수 정규화: 배열/맵 어떤 형식이 와도 하나의 Map으로 합치기
            Map<String, Integer> normalized = new LinkedHashMap<>();

            if (request.getTraitsMap() != null) {
                normalized.putAll(request.getTraitsMap());
            }
            if (request.getTraitsMapKo() != null) {
                normalized.putAll(request.getTraitsMapKo());
            }
            if (request.getTraitsList() != null) {
                for (TraitAnswer t : request.getTraitsList()) {
                    if (t == null) continue;
                    // 키는 questionId 또는 question 텍스트 중 하나로 저장
                    String key = (t.getQuestionId() != null)
                            ? "Q" + t.getQuestionId()
                            : (t.getQuestion() != null ? t.getQuestion() : UUID.randomUUID().toString());
                    normalized.put(key, t.getAnswer());
                }
            }

            // 유효성 검사 (1~5)
            Map<String, Integer> invalid = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> e : normalized.entrySet()) {
                Integer v = e.getValue();
                if (v == null || v < 1 || v > 5) invalid.put(e.getKey(), v);
            }
            if (!invalid.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", 400,
                        "message", "성향 점수는 1~5 사이의 정수여야 합니다.",
                        "invalidFields", invalid
                ));
            }

            // Firestore 저장(요청 그대로 + 정규화된 traits를 추가로 함께 저장)
            Firestore db = FirestoreClient.getFirestore();
            Map<String, Object> toSave = new LinkedHashMap<>();

            // DTO를 그대로 저장하면 필드명은 영문(자바 필드명)으로 들어갑니다.
            // 필요하다면 toSave에 원하는 형태로 매핑해서 저장하세요.
            toSave.put("userId", request.getUserId());
            toSave.put("name", request.getName());
            toSave.put("age", request.getAge());
            toSave.put("gender", request.getGender());
            toSave.put("economicStatus", request.getEconomicStatus());
            toSave.put("interest", request.getInterest());
            toSave.put("minimumExpectedSalary", request.getMinimumExpectedSalary());
            toSave.put("value", request.getValue());
            toSave.put("processed", Boolean.TRUE.equals(request.getProcessed()));
            toSave.put("createdAt", request.getCreatedAt().toString());
            toSave.put("traits", normalized); // 정규화된 점수

            db.collection("survey_results")
                    .document(String.valueOf(request.getUserId()))
                    .set(toSave);

            return ResponseEntity.status(201).body(Map.of(
                    "status", 201,
                    "message", "검사 결과가 성공적으로 저장되었습니다."
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "status", 500,
                    "message", "검사 결과 저장 중 오류가 발생했습니다."
            ));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getSurveyResult(@PathVariable String userId) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            ApiFuture<DocumentSnapshot> future =
                    db.collection("survey_results").document(userId).get();
            DocumentSnapshot doc = future.get();

            if (doc.exists()) {
                return ResponseEntity.ok(Map.of(
                        "status", 200,
                        "message", "검사 결과 조회 성공",
                        "data", doc.getData()
                ));
            }
            return ResponseEntity.status(404).body(Map.of(
                    "status", 404,
                    "message", "해당 유저의 검사 결과를 찾을 수 없습니다."
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "status", 500,
                    "message", "검사 결과 조회 중 오류가 발생했습니다."
            ));
        }
    }
}
