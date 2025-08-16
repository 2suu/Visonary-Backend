package esu.visionary.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class ErrorResponse {
    private final int code;       // HTTP 상태 코드 숫자 (예: 400)
    private final String status;  // 상태 문자열 (예: BAD_REQUEST)
    private final String message; // 상세 메시지

    public static ErrorResponse createErrorResponse(int code, String status, String message) {
        return ErrorResponse.of(code, status, message);
    }
}
