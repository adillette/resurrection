package marryus.studressmake.repository;

import marryus.studressmake.entity.ChatSession;
import marryus.studressmake.entity.Counselor;
import marryus.studressmake.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession,Long> {
    // 고객 ID로 채팅세션 찾기
    List<ChatSession> findByCustomerId(String customerId);

    // counselorId 대신 counselor로 찾기
    List<ChatSession> findByCounselor(Counselor counselor);

    // 진행중인 상담 찾기 optional을 사용허는 이유? findbyCustomerIdAndSessionStatus로 호출한 값이 존재할수도 있고 존재하지 않을수 있어서
    Optional<ChatSession> findByCustomerIdAndSessionStatus(String customerId, SessionStatus status);

    // 특정상태의 채팅 세션 모두 찾기
    List<ChatSession> findBySessionStatus(SessionStatus status);

    // 특정 기간의 채팅세션 찾기
    List<ChatSession> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    List<ChatSession> findByCounselorAndSessionStatus(Counselor counselor, SessionStatus sessionStatus);

    // Fetch 조인을 사용한 메서드 추가
    @Query("SELECT cs FROM ChatSession cs JOIN FETCH cs.counselor c LEFT JOIN FETCH c.sessions " +
                                    "WHERE c.counselorId = :counselorId AND cs.sessionStatus = :status")
    List<ChatSession> findByCounselorIdAndStatusWithFetch
                        (@Param("counselorId") String counselorId, @Param("status") SessionStatus status);

    // 세션 ID로 fetch 조인 조회
    @Query("SELECT cs FROM ChatSession cs JOIN FETCH cs.counselor c " +
                   "LEFT JOIN FETCH c.sessions WHERE cs.sessionId = :sessionId")
    Optional<ChatSession> findByIdWithFetch(@Param("sessionId") Long sessionId);

    int countByCounselorAndSessionStatus(Counselor counselor, SessionStatus sessionStatus);
}
