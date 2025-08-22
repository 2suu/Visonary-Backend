package esu.visionary.api.user.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

/**
 * 로그인 요청 DTO
 * - 프론트가 id / loginId / userId 등 어떤 키로 보내도 매핑되도록 @JsonAlias 추가
 * - 비밀번호는 절대 trim 하지 말고 원문 그대로 사용 (매칭 실패 방지)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginRequest {

    @NotBlank
    @JsonAlias({"loginId", "userId", "userid", "user_id"})
    private String id;        // 서버에서는 loginId로 사용

    @NotBlank
    private String password;  // 그대로 비교 (trim 금지)

    public LoginRequest() {}

    public String getId() {
        return id;
    }

    public void setId(String id) { // 필요 시 컨트롤러에서 id만 trim 권장 (비번은 X)
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) { // 원문 그대로 보관
        this.password = password;
    }
}
