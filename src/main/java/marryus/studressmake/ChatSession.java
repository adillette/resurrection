package marryus.studressmake;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "CHAT_SESSION")
@Getter @Setter
@NoArgsConstructor
public class ChatSession {
    @Id
    @GeneratedValue
    private Long sessionId;
    private String customerId;

    private Counselor counselor;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SessionStatus sessionStatus;
    @OneToMany(mappedBy = "chatSession", cascade = CascadeType.ALL)
    private List<ChatMessage> messages = new ArrayList<>();
}
