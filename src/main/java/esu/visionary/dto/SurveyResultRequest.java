package esu.visionary.dto;

import java.util.List;
import java.util.Map;

public class SurveyResultRequest {
    private Long userId;
    private String name;
    private int age;
    private String gender;
    private String economicStatus;
    private List<String> interest;
    private int minimumExpectedSalary;
    private String value;
    private Map<String, Integer> traits;
    private boolean processed;
    private String createdAt;

    public SurveyResultRequest() {}

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEconomicStatus() {
        return economicStatus;
    }
    public void setEconomicStatus(String economicStatus) {
        this.economicStatus = economicStatus;
    }

    public List<String> getInterest() {
        return interest;
    }
    public void setInterest(List<String> interest) {
        this.interest = interest;
    }

    public int getMinimumExpectedSalary() {
        return minimumExpectedSalary;
    }
    public void setMinimumExpectedSalary(int minimumExpectedSalary) {
        this.minimumExpectedSalary = minimumExpectedSalary;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public Map<String, Integer> getTraits() {
        return traits;
    }
    public void setTraits(Map<String, Integer> traits) {
        this.traits = traits;
    }

    public boolean isProcessed() {
        return processed;
    }
    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
