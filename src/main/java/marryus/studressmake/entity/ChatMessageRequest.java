package marryus.studressmake.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatMessageRequest {
    private Long sessionId;
    private String senderId;
    private String content;
}
