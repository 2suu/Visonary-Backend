package esu.visionary.dto;

public class VerifyRequest {

    private String name;                     // 이름
    private String identificationNumber;     // 주민등록번호
    private String phoneNumber;              // 전화번호
    private String carrier;                  // 통신사 (예: SKT, KT 등)
    private String gender;                   // 성별 (예: M/F)

    public VerifyRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
