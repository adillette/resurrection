package marryus.studressmake;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ChatMessageDTO {
    private Long sessionId;
    private String senderId;
    private String content;
    private LocalDateTime timestamp;

    public ChatMessageDTO(){

    }
    public ChatMessageDTO(Long sessionId, String senderId, String content, LocalDateTime timestamp) {
        this.sessionId = sessionId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
    }


}
