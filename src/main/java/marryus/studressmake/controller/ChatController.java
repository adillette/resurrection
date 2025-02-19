package marryus.studressmake.controller;

import lombok.RequiredArgsConstructor;
import marryus.studressmake.entity.ChatMessageDTO;
import marryus.studressmake.entity.ChatResponse;
import marryus.studressmake.entity.ChatSession;
import marryus.studressmake.entity.Counselor;
import marryus.studressmake.exception.NoAvailableCounselorException;
import marryus.studressmake.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;//웹소켓 메시지 전송용



    //채팅 시작 요청처리
    @MessageMapping("/chat.start")
    @SendTo("/topic/chat.connect")
    public ChatResponse startChat(String customerId){

        try{
            //채팅방 생성 및 상담원 배정
            ChatSession session = chatService.createChatSession(customerId);
            //상담 시작 메시지 전송
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

    @MessageMapping("/chat.assign")
    @SendTo("/topic/counselor.sessions")
    public ChatSession assignChat(String sessionId) {
        return chatService.getSession(sessionId);
    }
    @MessageMapping("/chat.send")
    public void handleMessage(ChatMessageDTO message){
        //메시지를 해당 세션의 참여자들에게 전송
        messagingTemplate.convertAndSend("/topic/chat."+ message.getSessionId(),message);



    }

    //채팅 페이지 이동
    @GetMapping("chat")
    public String chatPage(){
        return "chat";
    }

}
