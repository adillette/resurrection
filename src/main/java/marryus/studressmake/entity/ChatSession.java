package marryus.studressmake.entity;

import lombok.*;
import marryus.studressmake.SessionStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MARRYUS_CHAT_SESSION")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {
    @Id
    @Column(name = "SESSION_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long sessionId;
    @Column(name = "CUSTOMER_ID")
    private String customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNSELOR_ID")
    private Counselor counselor;  // 관계 정의 추가

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private SessionStatus sessionStatus;

    @Builder.Default
    @OneToMany(mappedBy = "chatSession", cascade = CascadeType.ALL)
    private List<ChatMessage> messages = new ArrayList<>();
}
