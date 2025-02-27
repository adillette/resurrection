package marryus.studressmake.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marryus.studressmake.CounselorDTO;
import marryus.studressmake.CounselorStatus;
import marryus.studressmake.entity.Counselor;
import marryus.studressmake.entity.CounselorStatusDTO;
import marryus.studressmake.service.ChatService;
import marryus.studressmake.service.CounselorService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/counselors")
@RequiredArgsConstructor
@Slf4j
public class CounselorController {

    private final CounselorService counselorService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    /**
     * REST API: 상담원 상태 업데이트
     */
    @PutMapping("/{counselorId}/status")
    public ResponseEntity<Counselor> updateCounselorStatus(
            @PathVariable String counselorId,
            @RequestParam String status,
            @RequestParam(required = false) String counselorName){
        Counselor counselor=
                counselorService.updateCounselorStatus(counselorId,status,counselorName);

        //websocket을 통해 상태 변경알림
        messagingTemplate.convertAndSend("/topic/counselor.status",
                new CounselorStatusDTO(counselorId, status,counselorName));

        return ResponseEntity.ok(counselor);
    }

    /**
     * WebSocket: 상담원 상태 업데이트
     */
    @MessageMapping("/counselor.updateStatus")
    public void updateCounselorStatus(Map<String, String> payload) {
        String counselorId = payload.get("counselorId");
        String statusStr = payload.get("status");

        System.out.println("상담원 상태 업데이트 요청: " + counselorId + ", 상태: " + statusStr);

        try {
            // 문자열을 대문자로 변환 후 Enum으로 변환
            CounselorStatus status = CounselorStatus.valueOf(statusStr.toUpperCase());

            // 서비스 호출
            chatService.updateCounselorStatus(counselorId, status);

            // 상태 변경을 다른 클라이언트에게 알림 (선택사항)
            Counselor updatedCounselor = new Counselor();
            updatedCounselor.setCounselorId(counselorId);
            updatedCounselor.setStatus(status);
            messagingTemplate.convertAndSend("/topic/counselor.status", updatedCounselor);

            System.out.println("상담원 상태 업데이트 성공: " + counselorId + ", 새 상태: " + status);

        } catch (IllegalArgumentException e) {
            System.err.println("잘못된 상담원 상태: " + statusStr);
            System.err.println("사용 가능한 상태: " + java.util.Arrays.toString(CounselorStatus.values()));
            e.printStackTrace(); // 상세한 오류 정보 출력
        }
    }
    /**
     * WebSocket : 모든 상담원 상태 요청 처리
     *
     */
    @MessageMapping("/counselor.getAllStatus")
    public void getAllCounselorStatus(){
        log.info("WebSocket: 모든 상담원 상태 요청");

        List<Counselor> counselors=
                counselorService.getAllCounselors();
        // 각 상담원의 상태를 개별적으로 전송
        for (Counselor counselor : counselors) {
            messagingTemplate.convertAndSend("/topic/counselor.status",
                    new CounselorStatusDTO(counselor.getCounselorId(), counselor.getCounselorName(), counselor.getStatus().name()));
            // 상담원 이름 추가
        }
    }
    /**
     * Rest API: 특정 상태의 상담원 조회
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Counselor>> getCounselorsByStatus(
            @PathVariable String status){
        return
                ResponseEntity.ok(counselorService.getCounselorByStatus(status));
    }

    /**
     * rest api : 특정 상담원 조회
     */
    @GetMapping("/{counselorId}")
    public ResponseEntity<Counselor> getCounselor(
            @PathVariable String counselorId){
        return ResponseEntity.ok(counselorService.getCounselor(counselorId));
    }

}