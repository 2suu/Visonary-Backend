package esu.visionary.common.exception;

import esu.visionary.common.response.ErrorResponse;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    // 404 - 오늘의 명언 없음 등 커스텀 404
    @ExceptionHandler(QuoteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleQuoteNotFound(QuoteNotFoundException e) {
        log.warn("Not Found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.createErrorResponse(404, "NOT_FOUND", e.getMessage()));
    }

    // 400 - 잘못된 요청 (유효성/파싱/타입/파라미터 누락 등)
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
        log.warn("Bad Request: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.createErrorResponse(400, "BAD_REQUEST", "잘못된 요청 형식입니다."));
    }

    // (선택) 데이터 계층 예외 → 500
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccess(DataAccessException e) {
        log.error("Data access error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.createErrorResponse(500, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."));
    }

    // 500 - 그 외 모든 예외 (⚠️ catch-all 는 딱 1개만)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.createErrorResponse(500, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."));
    }
}
