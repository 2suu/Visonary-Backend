package esu.visionary.api.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SignupRequest {

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 4, max = 50, message = "아이디는 4~50자여야 합니다.")
    private String id;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 16, message = "비밀번호는 8~16자여야 합니다.")
    // 영문 1+ 숫자 1+ 특수문자 1+ 포함, 공백 금지, 전체 길이 8~16
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9])\\S{8,16}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 각각 최소 1자 이상 포함하고 공백 없이 8~16자여야 합니다."
    )
    private String password;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 30, message = "닉네임은 2~30자여야 합니다.")
    private String nickName;

    @NotBlank(message = "본인 인증 트랜잭션 ID가 필요합니다.")
    private String transactionId;

    public SignupRequest() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
}
