package marryus.studressmake.entity;

import lombok.Getter;
import lombok.Setter;
import marryus.studressmake.SessionStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatSessionDTO {
    private Long sessionId;
    private String customerId;
    private String counselorId;
    private LocalDateTime startTime;
    private SessionStatus sessionStatus;
    private String counselorName; // 추가

    public ChatSessionDTO(){

    }
    // 생성자
    public ChatSessionDTO(ChatSession session) {
        this.sessionId = session.getSessionId();
        this.customerId = session.getCustomerId();

        // 지연 로딩된 counselor에 안전하게 접근
        if (session.getCounselor() != null) {
            this.counselorId = session.getCounselor().getCounselorId();
        }

        this.startTime = session.getStartTime();
        this.sessionStatus = session.getSessionStatus();
        this.counselorName=session.getCounselorName();

    }
}
