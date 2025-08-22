package esu.visionary.common.sms;

import esu.visionary.bootstrap.config.SmsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    private final DefaultMessageService messageService; // SmsConfig에서 Bean 등록
    private final SmsProperties props;

    /**
     * 인증번호 문자 발송
     */
    public void sendAuthCode(String to, String code) {
        String text = "[Visionary] 인증번호 " + code + " (5분 내 입력)";

        // 발송 비활성화 시 로깅만
        if (!props.isEnabled()) {
            log.info("🚫 SMS 발송 비활성화 - 실제 전송 안 함: to={}, text={}", to, text);
            return;
        }

        try {
            // 메시지 생성
            Message message = new Message();
            message.setFrom(props.getFrom());
            message.setTo(to);
            message.setText(text);

            // 단건 발송
            SingleMessageSentResponse res =
                    messageService.sendOne(new SingleMessageSendingRequest(message));

            // 응답 로깅
            if (res == null) {
                log.error("❌ 문자 발송 응답이 null 입니다. to={}", to);
                return;
            }
            log.info("✅ 문자 발송 성공: {}", res);

            // 필요 시 응답 코드 추가 체크 (SDK 제공 필드에 맞게 사용)
            // if (!"2000".equals(res.getStatusCode())) {
            //     log.warn("❌ 문자 발송 실패 응답: {}", res);
            // }

        } catch (Exception e) {
            log.error("❌ 문자 발송 중 예외 발생: to={}, msg={}", to, text, e);
        }
    }
}
