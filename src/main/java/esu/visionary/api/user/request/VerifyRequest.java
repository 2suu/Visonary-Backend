package esu.visionary.api.user.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyRequest {

    @NotBlank(message = "이름은 필수입니다.")
    private String name; // 이름

    // 프론트에서 다양한 키로 올 수 있으니 alias를 넓게 받아줌
    @JsonProperty("identificationNumber")
    @JsonAlias({
            "identification_number", "identification", "idNumber",
            "residentNumber", "rrn", "ssn", "jumin", "주민번호"
    })
    private String identificationNumber; // 주민등록번호 (필수 여부는 정책에 따라 @NotBlank 추가 가능)

    @NotBlank(message = "전화번호는 필수입니다.")
    @JsonProperty("phoneNumber")
    @JsonAlias({"phone_number", "phone", "tel", "mobile", "휴대폰", "전화번호"})
    private String phoneNumber; // 전화번호

    @NotBlank(message = "통신사는 필수입니다.")
    @JsonProperty("carrier")
    @JsonAlias({"통신사"})
    private String carrier; // 통신사 (예: SKT, KT 등)

    public VerifyRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIdentificationNumber() { return identificationNumber; }
    public void setIdentificationNumber(String identificationNumber) { this.identificationNumber = identificationNumber; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
}
