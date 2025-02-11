package marryus.studressmake;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
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
        log.info("상담 시작 - customerId: {}", customerId);


//        // 모든 상담원 조회 추가
//        List<Counselor> allCounselors = counselorRepository.findAll();
//        log.info("전체 상담원: {}", allCounselors);
//        // Native Query
//        List<Counselor> counselorsNative = counselorRepository.findAllWithNativeQuery();
//        log.info("Native Query 결과: {}", counselorsNative);

//        // 디버깅용 로그 추가
//        List<Counselor> counselors = counselorRepository.findByStatus(CounselorStatus.AVAILABLE);
//        log.info("어딨어SQL Query result size: {}", counselors.size());
//        counselors.forEach(c -> log.info("Counselor: id={}, status={}", c.getCounselorId(), c.getStatus()));

//         //상담원 상태 상세 로깅
//        for (Counselor counselor : counselorRepository.findAll()) {
//            log.info("상담원 ID: {}, 상태: {}, 이름: {}, ",
//                    counselor.getCounselorId(),
//                    counselor.getStatus(),
//                    counselor.getCounselorName());
//        }

        //가용 상담원 찾기
        Counselor counselor = counselorRepository.findByStatus(CounselorStatus.AVAILABLE.name())
                .stream()
                .sorted(Comparator.comparing(Counselor::getCounselorId))
                .findFirst()
                .orElseThrow(()-> new NoAvailableCounselorException("상담원이 모두 상담중입니다."));
        // 디버깅용 로그
        log.info("배정된 상담원: id={}, name={}", counselor.getCounselorId(), counselor.getCounselorName());
        //채팅방 생성
        ChatSession session = ChatSession.builder()
                .customerId(customerId)
                .counselor(counselor)
                .startTime(LocalDateTime.now())
                .sessionStatus(SessionStatus.ACTIVE)
                .build();

        log.info("생성된 세션: {}", session);

        //상담원 상태 변경
        counselor.setStatus(CounselorStatus.BUSY);
        counselorRepository.save(counselor);

        return chatSessionRepository.save(session);
    }
    // 채팅 메시지 저장
   public ChatMessage saveMessage(Long sessionId, String senderId, String content){
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(()-> new SessionNotFoundException("세션을 찾을수 없습니다."));

        ChatMessage message = ChatMessage.builder()
                .chatSession(session)
                .senderId(senderId)
                .messageContent(content)
                .sendTime(LocalDateTime.now())
                .messageType(MessageType.CHAT)
                .build();

        return chatMessageRepository.save(message);

    }
    //상담종료
    public void endChatSession(Long sessionId){
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(()-> new SessionNotFoundException("세션을 찾을수 없습니다."));

        session.setEndTime(LocalDateTime.now());
        session.setSessionStatus(SessionStatus.COMPLETED);

        Counselor counselor = session.getCounselor();
        counselor.setStatus(CounselorStatus.AVAILABLE);

        chatSessionRepository.save(session);
        counselorRepository.save(counselor);
    }

}
