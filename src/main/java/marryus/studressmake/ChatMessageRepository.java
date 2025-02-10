package marryus.studressmake;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatSession_SessionId(Long sessionId);
    List<ChatMessage> findByChatSession_SessionIdOrderBySendTimeDesc(Long sessionId);
}
