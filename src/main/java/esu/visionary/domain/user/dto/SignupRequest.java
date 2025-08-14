// esu.visionary.dto.SignupRequest.java

package esu.visionary.domain.user.dto;

public class SignupRequest {
    private String id;
    private String password;
    private String nickName;
    private String transactionId; // 🔹 인증 요청 시 발급된 ID (고유 키)

    // 기본 생성자
    public SignupRequest() {
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
