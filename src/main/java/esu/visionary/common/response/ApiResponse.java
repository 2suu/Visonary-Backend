package esu.visionary.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class ApiResponse<T> {
    private final int code;
    private final String status;
    private final String message;
    private final T data;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.of(200, "OK", "SUCCESS", data);
    }

    public static ApiResponse<Void> success() {
        return ApiResponse.of(200, "OK", "SUCCESS", null);
    }
}
