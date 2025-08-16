package esu.visionary.api.test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User API", description = "사용자 관리 API")
@RestController
@RequestMapping("/api/users")
public class UserTestController {

    // ✅ 간단한 GET API
    @Operation(summary = "모든 사용자 조회", description = "등록된 모든 사용자를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(
                List.of(
                        new UserResponse(1L, "홍길동", "hong@example.com"),
                        new UserResponse(2L, "김철수", "kim@example.com")
                )
        );
    }

    // ✅ 단일 사용자 조회
    @Operation(summary = "단일 사용자 조회", description = "userId를 기준으로 사용자를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(new UserResponse(id, "테스트유저", "test@example.com"));
    }

    // ✅ 사용자 생성
    @Operation(summary = "사용자 생성", description = "새로운 사용자를 등록합니다.")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        return ResponseEntity.ok(new UserResponse(100L, request.getName(), request.getEmail()));
    }

    // ✅ DTO 정의
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class UserRequest {
        private String name;
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class UserResponse {
        private Long id;
        private String name;
        private String email;
    }
}
