package marryus.studressmake.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marryus.studressmake.CounselorStatus;
import marryus.studressmake.entity.*;
import marryus.studressmake.exception.NoAvailableCounselorException;
import marryus.studressmake.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {


    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;//웹소켓 메시지 전송용


    //채팅 시작 요청처리
    @MessageMapping("/chat.start")
    @SendTo("/topic/chat.connect")
    public ChatResponse startChat(@Payload Map<String,String> payload) {
        log.info("채팅 시작 요청 수신: {}", payload);
        String customerId = payload.get("customerId");
        log.info("추출된 customerId: {}", customerId);

        if (customerId == null || customerId.isEmpty()) {
            log.error("고객 ID가 없음");
            return ChatResponse.builder()
                    .type("ERROR")
                    .message("고객 ID가 필요합니다.")
                    .build();
        }

        try {
            //채팅방 생성 및 상담원 배정
            ChatSession session = chatService.createChatSession(customerId);
            log.info("chat.start 1입니다.customerid 들어왔냐?{}", customerId);
            //** 수정: 새 세션인 경우에만 상담원에게 알림
            if (session.getStartTime().plusSeconds(5).isAfter(LocalDateTime.now())) {
                log.info("상담원에게 세션 알림 전송");
                messagingTemplate.convertAndSend("/topic/counselor.sessions", session);
            }
            log.info("chat.start 2입니다.채팅 세션 생성 성공: {}", session);
            // 상담 시작 메시지 전송

            return ChatResponse.builder()
                    .type("CONNECT")
                    .sessionId(session.getSessionId())
                    .counselorName(session.getCounselor().getCounselorName())
                    .message("상담이 시작되었습니다.")
                    .build();
        } catch (NoAvailableCounselorException e) {
            log.error("상담원 배정 실패: 가용 상담원 없음", e);
            return ChatResponse.builder()
                    .type("ERROR")
                    .message("현재 모든 상담원이 상담중입니다. 잠시후 다시 시도해주세요.")
                    .build();
        } catch (Exception e) {
            // 다른 모든 예외 처리
            log.error("채팅 세션 생성 중 오류 발생", e);
            return ChatResponse.builder()
                    .type("ERROR")
                    .message("서버 오류가 발생했습니다. 다시 시도해주세요.")
                    .build();
        }
    }

    @MessageMapping("/counselor.register")
    public void registerCounselor(Counselor counselor) {
        chatService.registerCounselor(counselor.getCounselorId());
    }

    @Transactional
    @MessageMapping("/counselor.getSessions")
    public void getCounselorSessions(@RequestBody Map<String, String> payload) {
        String counselorId = payload.get("counselorId");
        List<ChatSessionDTO> sessionDTOs = chatService.getSessionByCounselor(counselorId);
log.info("counselorId 나오니?{}", counselorId);
        for (ChatSessionDTO sessionDTO : sessionDTOs) {
            messagingTemplate.convertAndSend("/topic/counselor.sessions", sessionDTO);
        }
        log.info("sessionDTO는 나오냐" );
    }

    @MessageMapping("/chat.sendMessage")
    public void handleChatMessage(@Payload ChatMessageRequest messageRequest) {
        log.info("메시지 수신: {}", messageRequest);

        // 메시지 저장
        ChatMessage savedMessage = chatService.saveMessage(
                messageRequest.getSessionId(),
                messageRequest.getSenderId(),
                messageRequest.getContent()
        );
        log.info("메시지 저장: {}", savedMessage);

        // 세션 정보 조회 (상담원 이름 가져오기 위해)
        ChatSession session = chatService.getSession(String.valueOf(messageRequest.getSessionId()));

        // ChatResponse 객체 생성
        ChatResponse response = ChatResponse.builder()
                .type("CHAT")
                .sessionId(messageRequest.getSessionId())
                .counselorName(session.getCounselor().getCounselorName())  // 상담원 이름
                .message(messageRequest.getContent())
                .senderId(messageRequest.getSenderId())
                .timestamp(System.currentTimeMillis())
                .build();

        // 메시지 전송
        messagingTemplate.convertAndSend(
                "/topic/chat." + messageRequest.getSessionId(),
                response
        );

        log.info("메시지 전송 완료: 세션ID={}, 발신자={}", messageRequest.getSessionId(), messageRequest.getSenderId());    }

    @MessageMapping("/chat.end")
    public void endChat(@Payload Map<String, Long> payload) {
        log.info("채팅 종료 요청 받음: payload={}", payload);

        Object sessionIdObj = payload.get("sessionId");
        log.info("세션 id 타입: {}, 값:{}", sessionIdObj != null ? sessionIdObj.getClass().getName() : "null", sessionIdObj);

        Long sessionId;
        try {
            // 다양한 타입 처리
            if (sessionIdObj instanceof Number) {
                sessionId = ((Number) sessionIdObj).longValue();
            } else if (sessionIdObj instanceof String) {
                sessionId = Long.parseLong((String) sessionIdObj);
            } else {
                log.error("잘못된 세션 ID 형식: {}", sessionIdObj);
                return;
            }

            log.info("채팅 종료 처리 시작: 세션ID={}", sessionId);

            // 세션 종료 처리
            // ** 수정 1: void 반환에서 ChatSession 반환으로 변경됨
            ChatSession endedSession = chatService.endChatSession(sessionId);

            // 종료 메시지 전송
            ChatResponse response = ChatResponse.builder()
                    .type("END")
                    .sessionId(sessionId)
                    .message("상담이 종료되었습니다.")
                    .build();

            // 해당 세션의 참여자들에게 종료 메시지 전송
            messagingTemplate.convertAndSend("/topic/chat." + sessionId, response);

            log.info("채팅 종료 완료: 세션ID={}", sessionId);

            // ** 수정 2: 추가된 부분 - 상담원 상태 변경 알림 전송
            if (endedSession != null && endedSession.getCounselor() != null) {
                Counselor counselor = endedSession.getCounselor();
                messagingTemplate.convertAndSend("/topic/counselor.status",
                        new CounselorStatusDTO(
                                counselor.getCounselorId(),
                                counselor.getCounselorName(),
                                CounselorStatus.AVAILABLE.name()
                        ));
            }
        } catch (Exception e) {
            log.error("채팅 종료 처리 중 오류 발생", e);
        }
    }
}
