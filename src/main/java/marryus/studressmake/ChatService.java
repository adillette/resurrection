package marryus.studressmake;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.DoubleStream;

import static java.util.Arrays.stream;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final  ChatSessionRepository chatSessionRepository;
    private final CounselorRepository counselorRepository;
    private final ChatMessageRepository chatMessageRepository;

    //채팅방 생성 및 상담원 배정
    public ChatSession createChatSession(String customerId){
        // 디버깅용 로그 추가
        List<Counselor> availableCounselors = counselorRepository.findByStatus(CounselorStatus.AVAILABLE);
        // 상담원 상태 상세 로깅
        for (Counselor counselor : counselorRepository.findAll()) {
            log.info("상담원 ID: {}, 이름: {}, 상태: {}",
                    counselor.getConselorId(),
                    counselor.getCounselorName(),
                    counselor.getStatus());
        }

        //가용 상담원 찾기
        Counselor counselor = counselorRepository.findByStatus(CounselorStatus.AVAILABLE)
                .stream()
                .findFirst()
                .orElseThrow(()-> new NoAvailableCounselorException("상담원이 모두 상담중입니다."));
        //채팅방 생성
        ChatSession session = ChatSession.builder()
                .customerId(customerId)
                .counselor(counselor)
                .startTime(LocalDateTime.now())
                .sessionStatus(SessionStatus.ACTIVE)
                .build();

        //상담원 상태 변경
        counselor.setStatus(CounselorStatus.BUSY);
        counselorRepository.save(counselor);

        return chatSessionRepository.save(session);
    }


}
