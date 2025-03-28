package marryus.studressmake.service;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marryus.studressmake.CounselorStatus;
import marryus.studressmake.entity.ChatSession;
import marryus.studressmake.entity.Counselor;
import marryus.studressmake.repository.CounselorRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CounselorService {

    private final CounselorRepository counselorRepository;

    /**
     * counselor 상태 업데이트
     *
     */

    public Counselor updateCounselorStatus(String counselorId, String status,String counselorName){
        log.info("상담원 상태 업데이트- counselorId:{}, status:{}", counselorId, status,counselorName);
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 상담원입니다."));

        try {
            CounselorStatus newStatus = CounselorStatus.valueOf(status.toUpperCase());
            counselor.setStatus(newStatus);
            if (counselorName != null && !counselorName.isEmpty()) {
                counselor.setCounselorName(counselorName); // 상담원 이름 업데이트 (Counselor 클래스에 counselorName 필드가 있다고 가정)
            }
            counselorRepository.save(counselor);

            log.info("상담원 상태 변경완료: id={}, status={}, name={}", counselorId, newStatus, counselorName);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 상담원 상태: {}", status);
            throw new IllegalArgumentException("유효하지 않은 상담원 상태 입니다.");
        }
        return counselor;
    }

    /**
     * 모든 상담원 조회
     */
    public List<Counselor> getAllCounselors(){
        return counselorRepository.findAll();
    }

    /**
     * 특정 상태의 상담원 조회
     */
    public List<Counselor> getCounselorByStatus(String status){
        try {
            CounselorStatus counselorStatus = CounselorStatus.valueOf(status.toUpperCase());
            return counselorRepository.findByStatus(counselorStatus.name());
        }catch (IllegalArgumentException e){
            log.error("잘못된 상담원 상태: {}", status);
            throw new IllegalArgumentException("유효하지 않은 상담원 상태입니다.");
        }
    }
    /**
     * 세션 정보와 함께 상담원 조회
     */
    public Counselor getCounselorWithSessions(String counselorId) {
        return counselorRepository.findByIdWithSessions(counselorId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상담원입니다."));
    }

    /**
     * 특정 상담원 조회
     */
    public Counselor getCounselor(String counselorId){
        return counselorRepository.findById(counselorId).orElseThrow(()->new IllegalArgumentException("존재하지 않는 상담원입니다."));
    }

    /**
     * 상담원 로그인 처리(상태를 available로 변경)
     *
     */

    /**
     * 상담원 로그아웃 처리(상태를 offline 으로 변경)
     *
     */
    /**
     * 상담 요청 큐 관리 서비스
     */



}