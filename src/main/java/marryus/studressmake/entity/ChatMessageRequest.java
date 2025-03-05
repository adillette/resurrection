package marryus.studressmake.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ChatMessageRequest {
    private Long sessionId;
    private String senderId;
    private String senderName;
    private String content;
    private String counselorName;
   // private Long timestamp;
}
