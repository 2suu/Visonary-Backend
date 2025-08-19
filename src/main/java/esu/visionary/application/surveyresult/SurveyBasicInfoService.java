package esu.visionary.application.surveyresult;

import esu.visionary.common.exception.GoneException;
import esu.visionary.common.exception.NotFoundException;
import esu.visionary.domain.survey.SurveySession;
import esu.visionary.domain.survey.enums.SurveyStatus;
import esu.visionary.domain.surveyresult.BasicInfoCareerResponse;
import esu.visionary.domain.surveyresult.BasicInfoItem;
import esu.visionary.domain.surveyresult.enums.BasicCategory;
import esu.visionary.infrastructure.survey.repository.SurveySessionRepository;
import esu.visionary.domain.surveyresult.BasicProfileDoc;
import esu.visionary.infrastructure.surveyresult.SurveyResultFirebaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyBasicInfoService {

    private final SurveySessionRepository sessionRepo;      // JPA
    private final SurveyResultFirebaseRepository fbRepo;    // Firestore DAO

    public BasicInfoCareerResponse getBasicInfoCareer(Long surveySessionId) {
        log.info("[BasicInfo] START id={}", surveySessionId);

        SurveySession session = sessionRepo.findById(surveySessionId)
                .orElseThrow(() -> new NotFoundException("[RDB] 세션 없음 id=" + surveySessionId));
        log.info("[BasicInfo] RDB OK id={} status={}", surveySessionId, session.getStatus());

        if (session.getStatus() == SurveyStatus.CANCELLED) {
            throw new GoneException("[RDB] 세션 취소/만료 id=" + surveySessionId);
        }

        BasicProfileDoc doc = fbRepo.getBasicProfile(surveySessionId)
                .orElseThrow(() -> new NotFoundException("[Firebase] basic_profile 없음 id=" + surveySessionId));
        log.info("[BasicInfo] Firebase OK id={} doc={}", surveySessionId, doc);

        List<BasicInfoItem> items = new ArrayList<>();
        if (doc.getAge() != null)    items.add(item(BasicCategory.AGE, "나이", doc.getAge() + "세"));
        if (doc.getGender() != null) items.add(item(BasicCategory.GENDER, "성별", doc.getGender()));
        if (doc.getEducation() != null) items.add(item(BasicCategory.EDUCATION, "최종 학력", doc.getEducation()));
        if (doc.getCareer() != null) items.add(item(BasicCategory.CAREER, "경력", doc.getCareer()));

        if (items.isEmpty()) {
            // 임시로 어디까지 왔는지 보려고 200 빈배열로 내려보는 것도 방법
            // throw new NotFoundException("[Firebase] 필드 비어있음 id=" + surveySessionId);
            log.warn("[BasicInfo] 결과 항목 비어있음 id={}", surveySessionId);
        }

        return BasicInfoCareerResponse.builder()
                .code(200).status("OK").message("SUCCESS").data(items).build();
    }

    private BasicInfoItem item(BasicCategory c, String l, String v) {
        return BasicInfoItem.builder().category(c).label(l).value(v).build();
    }

}
