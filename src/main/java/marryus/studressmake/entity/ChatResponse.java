package marryus.studressmake.entity;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    private String type;
    private Long sessionId;
    private String counselorName;
    private String message;
    private String senderId;    // 추가
    private Long timestamp;


}
