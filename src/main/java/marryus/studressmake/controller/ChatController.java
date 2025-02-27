package marryus.studressmake.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {


    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;//웹소켓 메시지 전송용


    //채팅 시작 요청처리
    @MessageMapping("/chat.start")
    @SendTo("/topic/chat.connect")
    public ChatResponse startChat(String customerId) {

        try {
            //채팅방 생성 및 상담원 배정
            ChatSession session = chatService.createChatSession(customerId);

            //새 세션을 해당 상담원에게 알림(이부분 추가)
            messagingTemplate.convertAndSend("/topic/counselor.sessions", session);
            // 상담 시작 메시지 전송
            return ChatResponse.builder()
                    .type("CONNECT")
                    .sessionId(session.getSessionId())
                    .counselorName(session.getCounselor().getCounselorName())
                    .message("상담이 시작되었습니다.")
                    .build();
        } catch (NoAvailableCounselorException e) {
            return ChatResponse.builder()
                    .type("ERROR")
                    .message("현재 모든 상담원이 상담중입니다. 잠시후 다시 시도해주세요.")
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

        for (ChatSessionDTO sessionDTO : sessionDTOs) {
            messagingTemplate.convertAndSend("/topic/counselor.sessions", sessionDTO);
        }
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

        // 세션 정보 조회 (상담원 이름 가져오기 위해)
        ChatSession session = chatService.getSession(String.valueOf(messageRequest.getSessionId()));

        // ChatResponse 객체 생성
        ChatResponse response = ChatResponse.builder()
                .type("CHAT")
                .sessionId(messageRequest.getSessionId())
                .counselorName(session.getCounselor().getCounselorName())  // 상담원 이름
                .message(messageRequest.getContent())
                .build();

        // 메시지 전송
        messagingTemplate.convertAndSend(
                "/topic/chat." + messageRequest.getSessionId(),
                response
        );

        log.info("메시지 전송 완료: 세션ID={}, 발신자={}", messageRequest.getSessionId(), messageRequest.getSenderId());
    }

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
            chatService.endChatSession(sessionId);

            // 종료 메시지 전송
            ChatResponse response = ChatResponse.builder()
                    .type("END")
                    .sessionId(sessionId)
                    .message("상담이 종료되었습니다.")
                    .build();

            // 해당 세션의 참여자들에게 종료 메시지 전송
            messagingTemplate.convertAndSend("/topic/chat." + sessionId, response);

            log.info("채팅 종료 완료: 세션ID={}", sessionId);
        } catch (Exception e) {
            log.error("채팅 종료 처리 중 오류 발생", e);
        }
    }
}
