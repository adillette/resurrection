package marryus.studressmake.service;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marryus.studressmake.CounselorStatus;
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

    // 기본 상담원 ID 설정
    private static final String DEFAULT_COUNSELOR_ID = "cs1";

    /**
     * 상담원 상태 업데이트 (기본 상담원: cs1)
     */
    public Counselor updateDefaultCounselorStatus(String status) {
        return updateCounselorStatus(DEFAULT_COUNSELOR_ID, status);
    }

    /**
     * 특정 상담원 상태 업데이트
     */
    public Counselor updateCounselorStatus(String counselorId, String status) {
        log.info("상담원 상태 업데이트 - counselorId: {}, status: {}", counselorId, status);

        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상담원입니다."));

        // 유효한 상태인지 확인
        try {
            CounselorStatus newStatus = CounselorStatus.valueOf(status);
            counselor.setStatus(newStatus);
            counselorRepository.save(counselor);

            log.info("상담원 상태 변경 완료: id={}, status={}", counselorId, newStatus);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 상담원 상태: {}", status);
            throw new IllegalArgumentException("유효하지 않은 상담원 상태입니다.");
        }

        return counselor;
    }

    /**
     * 기본 상담원(cs1) 조회
     */
    public Counselor getDefaultCounselor() {
        return getCounselor(DEFAULT_COUNSELOR_ID);
    }

    /**
     * 모든 상담원 조회
     */
    public List<Counselor> getAllCounselors() {
        return counselorRepository.findAll();
    }

    /**
     * 특정 상태의 상담원 조회
     */
    public List<Counselor> getCounselorsByStatus(String status) {
        try {
            return counselorRepository.findByStatus(status);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 상담원 상태: {}", status);
            throw new IllegalArgumentException("유효하지 않은 상담원 상태입니다.");
        }
    }

    /**
     * 특정 상담원 조회
     */
    public Counselor getCounselor(String counselorId) {
        return counselorRepository.findById(counselorId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상담원입니다."));
    }

    /**
     * 기본 상담원(cs1) 로그인 처리 (상태를 AVAILABLE로 변경)
     */
    public Counselor defaultCounselorLogin() {
        return updateCounselorStatus(DEFAULT_COUNSELOR_ID, CounselorStatus.AVAILABLE.name());
    }

    /**
     * 기본 상담원(cs1) 로그아웃 처리 (상태를 OFFLINE으로 변경)
     */
    public Counselor defaultCounselorLogout() {
        return updateCounselorStatus(DEFAULT_COUNSELOR_ID, CounselorStatus.OFFLINE.name());
    }
}