package marryus.studressmake.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marryus.studressmake.entity.Counselor;
import marryus.studressmake.service.CounselorService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/counselors")
@RequiredArgsConstructor
@Slf4j
public class CounselorController {

    private final CounselorService counselorService;
    private final SimpMessagingTemplate messagingTemplate;

    // 기본 상담원 ID 설정
    private static final String DEFAULT_COUNSELOR_ID = "cs1";

    /**
     * REST API: 기본 상담원(cs1) 상태 업데이트
     */
    @PutMapping("/default/status")
    public ResponseEntity<Counselor> updateDefaultCounselorStatus(
            @RequestParam String status) {

        Counselor counselor = counselorService.updateDefaultCounselorStatus(status);

        // WebSocket을 통해 상태 변경 알림
        messagingTemplate.convertAndSend("/topic/counselor.status",
                new CounselorStatusDTO(DEFAULT_COUNSELOR_ID, status));

        return ResponseEntity.ok(counselor);
    }

    /**
     * REST API: 상담원 상태 업데이트
     */
    @PutMapping("/{counselorId}/status")
    public ResponseEntity<Counselor> updateCounselorStatus(
            @PathVariable String counselorId,
            @RequestParam String status) {

        Counselor counselor = counselorService.updateCounselorStatus(counselorId, status);

        // WebSocket을 통해 상태 변경 알림
        messagingTemplate.convertAndSend("/topic/counselor.status",
                new CounselorStatusDTO(counselorId, status));

        return ResponseEntity.ok(counselor);
    }

    /**
     * WebSocket: 상담원 상태 업데이트 (cs1 기본값 사용)
     */
    @MessageMapping("/counselor.updateDefaultStatus")
    @SendTo("/topic/counselor.status")
    public CounselorStatusDTO updateDefaultStatus(CounselorStatusDTO statusDTO) {
        log.info("WebSocket: 기본 상담원(cs1) 상태 업데이트 - {}", statusDTO.getStatus());

        counselorService.updateDefaultCounselorStatus(statusDTO.getStatus());

        return new CounselorStatusDTO(DEFAULT_COUNSELOR_ID, statusDTO.getStatus());
    }

    /**
     * WebSocket: 상담원 상태 업데이트
     */
    @MessageMapping("/counselor.updateStatus")
    @SendTo("/topic/counselor.status")
    public CounselorStatusDTO updateStatus(CounselorStatusDTO statusDTO) {
        log.info("WebSocket: 상담원 상태 업데이트 - {}", statusDTO);

        counselorService.updateCounselorStatus(
                statusDTO.getCounselorId(),
                statusDTO.getStatus());

        return statusDTO;
    }

    /**
     * WebSocket: 모든 상담원 상태 요청 처리
     */
    @MessageMapping("/counselor.getAllStatus")
    public void getAllCounselorStatus() {
        log.info("WebSocket: 모든 상담원 상태 요청");

        List<Counselor> counselors = counselorService.getAllCounselors();

        // 각 상담원의 상태를 개별적으로 전송
        for (Counselor counselor : counselors) {
            messagingTemplate.convertAndSend("/topic/counselor.status",
                    new CounselorStatusDTO(counselor.getCounselorId(),
                            counselor.getStatus().name()));
        }
    }

    /**
     * REST API: 기본 상담원(cs1) 조회
     */
    @GetMapping("/default")
    public ResponseEntity<Counselor> getDefaultCounselor() {
        return ResponseEntity.ok(counselorService.getDefaultCounselor());
    }

    /**
     * REST API: 모든 상담원 조회
     */
    @GetMapping
    public ResponseEntity<List<Counselor>> getAllCounselors() {
        return ResponseEntity.ok(counselorService.getAllCounselors());
    }

    /**
     * REST API: 특정 상태의 상담원 조회
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Counselor>> getCounselorsByStatus(
            @PathVariable String status) {
        return ResponseEntity.ok(counselorService.getCounselorsByStatus(status));
    }

    /**
     * REST API: 특정 상담원 조회
     */
    @GetMapping("/{counselorId}")
    public ResponseEntity<Counselor> getCounselor(
            @PathVariable String counselorId) {
        return ResponseEntity.ok(counselorService.getCounselor(counselorId));
    }
}