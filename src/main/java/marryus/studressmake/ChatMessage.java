package marryus.studressmake;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CHAT_MESSAGE")
@Getter @Setter
@NoArgsConstructor //기본자 생성
public class ChatMessage {
    @Id
    @GeneratedValue
    private  Long messageId;

    @ManyToOne(fetch =FetchType.LAZY )
    @JoinColumn(name="SESSION_ID")
    private ChatSession chatSession;

    private String senderId;

    @Lob //db에 많은양 저장하려고 설정
    private String messageContent;

    private LocalDateTime sendTime;

    @Enumerated(EnumType.STRING)
    @Column(name="MESSAGE_TYPE", nullable = false)
    private MessageType messageType;
}
