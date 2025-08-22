package esu.visionary.common.exception;

import esu.visionary.common.response.ErrorResponse;
<<<<<<< HEAD
import esu.visionary.domain.quote.exception.QuoteNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    // ───────────── 4xx: Client Errors ─────────────

    // 404 - 도메인 NotFound (예: 오늘의 명언 없음, 일반 조회 없음)
    @ExceptionHandler({ QuoteNotFoundException.class, NotFoundException.class })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException e) {
        log.warn("Not Found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.createErrorResponse(404, "NOT_FOUND", e.getMessage()));
    }

    // 410 - 세션 만료/취소 등
    @ExceptionHandler(GoneException.class)
    public ResponseEntity<ErrorResponse> handleGone(GoneException e) {
        log.warn("Gone: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.GONE)
                .body(ErrorResponse.createErrorResponse(410, "GONE", e.getMessage()));
    }

    // 400 - 잘못된 요청(검증/파싱/타입/파라미터 누락 등)
    @ExceptionHandler({
            IllegalArgumentException.class,
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MissingPathVariableException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
        String message = switch (e) {
            case MethodArgumentNotValidException manve ->
                    extractBindingErrors(manve);
            case ConstraintViolationException cve ->
                    cve.getConstraintViolations().stream()
                            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                            .findFirst().orElse("잘못된 요청 형식입니다.");
            default -> "잘못된 요청 형식입니다.";
        };
        log.warn("Bad Request: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.createErrorResponse(400, "BAD_REQUEST", message));
    }

    // (선택) 405 - 지원하지 않는 HTTP 메서드
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(Exception e) {
        log.warn("Method Not Allowed: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorResponse.createErrorResponse(405, "METHOD_NOT_ALLOWED", "허용되지 않는 메서드입니다."));
    }

    // ───────────── 5xx: Server Errors ─────────────

    // 데이터 계층 에러
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccess(DataAccessException e) {
        log.error("Data access error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.createErrorResponse(500, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."));
    }

    // 외부 서비스/SDK(Firebase 등) 래핑 예외가 있다면 여기에 매핑
    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleInternal(InternalServerErrorException e) {
        log.error("Internal error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.createErrorResponse(500, "INTERNAL_SERVER_ERROR", e.getMessage()));
    }

    // 500 - 그 외 모든 예외 (catch‑all: 마지막 한 개)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.createErrorResponse(500, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."));
    }

    // ───────────── Utils ─────────────

    private String extractBindingErrors(MethodArgumentNotValidException ex) {
        var fieldError = ex.getBindingResult().getFieldErrors().stream().findFirst();
        if (fieldError.isPresent()) {
            var f = fieldError.get();
            return String.format("%s: %s (rejected: %s)", f.getField(), f.getDefaultMessage(), f.getRejectedValue());
        }
        return "잘못된 요청 형식입니다.";
=======
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.createErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.name(),
                        e.getMessage()
                ));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.createErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.name(),
                        "서버 내부 오류가 발생했습니다."
                ));
    }

    /**
     * ✅ Bean Validation (ex. @NotBlank, @Size 등) 실패 시 응답 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException e) {
        // 어떤 필드가 어떤 이유로 실패했는지 추출
        Map<String, String> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fe -> fe.getField(),
                        fe -> fe.getDefaultMessage(),
                        (a, b) -> a // 중복 필드 발생 시 첫 번째 메시지 유지
                ));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "message", "요청 값이 유효하지 않습니다.",
                        "errors", fieldErrors
                ));
>>>>>>> aff50e0 (최종 버전)
    }
}
