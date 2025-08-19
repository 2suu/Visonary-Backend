package esu.visionary.domain.surveyresult;

import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicProfileDoc {
    private Integer age;
    private String gender;
    private String education;
    private String career;     // 맵에 없으면 null
    private Instant updatedAt; // 문자열이면 parse

    public static BasicProfileDoc fromMap(Map<String, Object> m) {
        BasicProfileDoc d = new BasicProfileDoc();
        Object age = m.get("age");
        if (age instanceof Number n) d.setAge(n.intValue());
        else if (age instanceof String s && !s.isBlank()) d.setAge(Integer.parseInt(s));

        d.setGender(asString(m.get("gender")));
        d.setEducation(asString(m.get("education")));
        d.setCareer(asString(m.get("career")));

        Object ua = m.get("updatedAt");
        if (ua instanceof Timestamp ts) {
            d.setUpdatedAt(ts.toDate().toInstant());
        } else if (ua instanceof String s && !s.isBlank()) {
            d.setUpdatedAt(Instant.parse(s)); // ISO-8601 문자열일 때
        }
        return d;
    }

    private static String asString(Object o) {
        return (o == null) ? null : String.valueOf(o);
    }
}
