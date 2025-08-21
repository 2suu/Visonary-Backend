package esu.visionary.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {

    private int code;
    private String status;
    private String message;
    private T data;

    // 성공 응답 편의 메서드
    public static <T> CommonResponse<T> success(T data) {
        return CommonResponse.<T>builder()
                .code(200)
                .status("OK")
                .message("SUCCESS")
                .data(data)
                .build();
    }

    // (선택) 커스텀 성공 코드가 필요하면 여기도 추가 가능
    public static <T> CommonResponse<T> of(int code, String status, String message, T data) {
        return CommonResponse.<T>builder()
                .code(code)
                .status(status)
                .message(message)
                .data(data)
                .build();
    }
}
