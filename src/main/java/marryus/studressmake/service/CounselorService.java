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

    /**
     * counselor 상태 업데이트
     *
     */

    public Counselor updateCounselorStatus(String counselorId, String status){
        log.info("상담원 상태 업데이트- counselorId:{}, status:{}", counselorId, status);
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 상담원입니다."));

        //유효한 상태인지 확인
        try {
            CounselorStatus newStatus =
                    CounselorStatus.valueOf(status);
            counselor.setStatus(newStatus);
            counselorRepository.save(counselor);

            log.info("상담원 상태 변경완료: id={},status={}",counselorId,newStatus);

        } catch (IllegalArgumentException e) {
            log.error("잘못된 상담원 상태: {}",status);
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
            CounselorStatus counselorStatus = CounselorStatus.valueOf(status);
            return counselorRepository.findByStatus(status);
        }catch (IllegalArgumentException e){
            log.error("잘못된 상담원 상태: {}", status);
            throw new IllegalArgumentException("유효하지 않은 상담원 상태입니다.");
        }
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
    public Counselor counselorLogin(String counselorId){
        return updateCounselorStatus(counselorId, CounselorStatus.AVAILABLE.name());
    }

    /**
     * 상담원 로그아웃 처리(상태를 offline 으로 변경)
     *
     */
    public Counselor counselorLogout(String counselorId){
        return updateCounselorStatus(counselorId, CounselorStatus.OFFLINE.name());
    }

}