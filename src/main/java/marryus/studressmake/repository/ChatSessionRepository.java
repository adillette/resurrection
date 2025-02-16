package marryus.studressmake.repository;

import marryus.studressmake.ChatSession;
import marryus.studressmake.Counselor;
import marryus.studressmake.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession,Long> {
    // 고객 ID로 채팅세션 찾기
    List<ChatSession> findByCustomerId(String customerId);

    // counselorId 대신 counselor로 찾기
    List<ChatSession> findByCounselor(Counselor counselor);

    // 진행중인 상담 찾기
    Optional<ChatSession> findByCustomerIdAndSessionStatus(String customerId, SessionStatus status);

    // 특정상태의 채팅 세션 모두 찾기
    List<ChatSession> findBySessionStatus(SessionStatus status);

    // 특정 기간의 채팅세션 찾기
    List<ChatSession> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
