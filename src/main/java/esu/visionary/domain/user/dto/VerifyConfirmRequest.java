package esu.visionary.domain.user.dto;

public class VerifyConfirmRequest {
    private String transactionId;
    private String verificationCode;

    public VerifyConfirmRequest() {
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
