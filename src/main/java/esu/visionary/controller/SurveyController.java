package esu.visionary.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import esu.visionary.dto.SurveyResultRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/survey")
public class SurveyController {

    // 설문 결과 저장
    @PostMapping("/submit")
    public ResponseEntity<?> saveSurveyResult(@RequestBody SurveyResultRequest request) {
        try {
            Map<String, Integer> traits = request.getTraits();
            Map<String, Integer> invalidFields = new HashMap<>();

            if (traits != null) {
                for (Map.Entry<String, Integer> entry : traits.entrySet()) {
                    int value = entry.getValue();
                    if (value < 1 || value > 5) {
                        invalidFields.put(entry.getKey(), value);
                    }
                }
            }

            if (!invalidFields.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of(
                                "status", 400,
                                "message", "성향 점수는 1~5 사이의 정수여야 합니다.",
                                "invalidFields", invalidFields
                        )
                );
            }

            Firestore db = FirestoreClient.getFirestore();

            db.collection("survey_results")
                    .document(String.valueOf(request.getUserId()))
                    .set(request);

            return ResponseEntity.status(201).body(
                    Map.of(
                            "status", 201,
                            "message", "검사 결과가 성공적으로 저장되었습니다."
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                    Map.of(
                            "status", 500,
                            "message", "검사 결과 저장 중 오류가 발생했습니다."
                    )
            );
        }
    }

    // 설문 결과 조회
    @GetMapping("/{userId}")
    public ResponseEntity<?> getSurveyResult(@PathVariable String userId) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            ApiFuture<DocumentSnapshot> future = db.collection("survey_results")
                    .document(userId)
                    .get();

            DocumentSnapshot document = future.get();

            if (document.exists()) {
                return ResponseEntity.ok(
                        Map.of(
                                "status", 200,
                                "message", "검사 결과 조회 성공",
                                "data", document.getData()
                        )
                );
            } else {
                return ResponseEntity.status(404).body(
                        Map.of(
                                "status", 404,
                                "message", "해당 유저의 검사 결과를 찾을 수 없습니다."
                        )
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                    Map.of(
                            "status", 500,
                            "message", "검사 결과 조회 중 오류가 발생했습니다."
                    )
            );
        }
    }
}
