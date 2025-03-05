package marryus.studressmake.service;

import lombok.AllArgsConstructor;
import marryus.studressmake.SessionStatus;
import marryus.studressmake.entity.ChatSession;
import marryus.studressmake.entity.ChatSessionDTO;
import marryus.studressmake.repository.ChatSessionRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;



@Service
@AllArgsConstructor
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;

    // fetch 조인을 사용하여 LazyInitializationException 방지
    @Transactional(readOnly = true)
    public List<ChatSessionDTO> getSessionsByCounselor(String counselorId) {
        // fetch 조인을 사용하여 LazyInitializationException 방지
        List<ChatSession> sessions = chatSessionRepository.findByCounselorIdAndStatusWithFetch(
                counselorId, SessionStatus.ACTIVE);

        // DTO로 변환하여 반환
        return sessions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChatSessionDTO getSessionById(Long sessionId) {

        ChatSession session = chatSessionRepository.findByIdWithFetch(sessionId)
                .orElseThrow(()-> new RuntimeException("채팅 세션을 찾을수 없습니다."));

        return convertToDTO(session);

    }

    private ChatSessionDTO convertToDTO(ChatSession session) {
        ChatSessionDTO dto = new ChatSessionDTO();
        dto.setSessionId(session.getSessionId());
        dto.setCustomerId(session.getCustomerId());
        dto.setStartTime(session.getStartTime());
        dto.setSessionStatus(session.getSessionStatus());

        // 상담원 정보 설정
        if (session.getCounselor() != null) {
            dto.setCounselorId(session.getCounselor().getCounselorId());
            dto.setCounselorName(session.getCounselor().getCounselorName());
        }

        return dto;
    }
}