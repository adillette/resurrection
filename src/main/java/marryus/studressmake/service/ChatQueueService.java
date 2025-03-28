package marryus.studressmake.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marryus.studressmake.CounselorStatus;
import marryus.studressmake.SessionStatus;
import marryus.studressmake.entity.ChatSession;
import marryus.studressmake.entity.Counselor;
import marryus.studressmake.repository.ChatSessionRepository;
import marryus.studressmake.repository.CounselorRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;
import javax.transaction.Transactional;
import java.util.List;

import static marryus.studressmake.SessionStatus.ACTIVE;
import static marryus.studressmake.SessionStatus.COMPLETED;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatQueueService {

    private final ChatSessionRepository chatSessionRepository;
    private final CounselorRepository counselorRepository;

    //전체 상담 요청 큐
    private Queue<ChatSession> requestQueue = new LinkedList<>();
    private int lastAssignedCounselorIndex=-1; //마지막으로 배정된 상담원 인덱스

    // 라운드 로빈 방식으로 상담원 선택
    protected Counselor getNextAvailableCounselorInRoundRobin(List<Counselor> availableCounselors){
        if(availableCounselors.isEmpty()){
            return null;
        }
        int size = availableCounselors.size();
        int startIndex =(lastAssignedCounselorIndex+1)%size;// 다음 상담원부터 시작

        for(int i=0; i<size; i++){
            int currentIndex = (startIndex+i)%size;
            Counselor counselor = availableCounselors.get(currentIndex);

            if(counselor.canAcceptMoreWork()){
                lastAssignedCounselorIndex=currentIndex;//현재 인덱스 저장
                return counselor;
            }
        }

        // 일일 작업량 제한에 모두 도달했다면, 전체 작업량이 가장 적은 상담원 선택
        return getAvailableCounselorWithMinWorkload(availableCounselors);
    }


    //상담 요청을 큐에 추가(클라이언트가 상담버튼 클릭시 호출)
    public void addToQueue(ChatSession chatSession){
        requestQueue.add(chatSession);

        chatSession.setSessionStatus(ACTIVE);
        chatSessionRepository.save(chatSession);

        log.info("새로운 상담 요청이 큐에 추가됨");
        assignToCounselor();
    }

    //대기 중인 상담을 상담원에게 배정

    public boolean assignToCounselor(){
        if(requestQueue.isEmpty()){
            log.info("대기중인 상담요청 없어요");
            return false;
        }

        List<Counselor> availableCounselors = counselorRepository
                .findByStatus(CounselorStatus.AVAILABLE.name());
        if (availableCounselors.isEmpty()) {
            log.info("사용 가능한 상담원이 없음");
            return false;
        }
        //작업량이 가장 적은 상담원 선택
        Counselor counselor = getNextAvailableCounselorInRoundRobin(availableCounselors);
       // if (counselor != null && canAcceptMoreWork(counselor)) {
        if(counselor!=null){
            //큐에서 가장 오래된 요청 가져오기
            ChatSession chatSession= requestQueue.poll();

            //선택된 상담원에게 배정
            chatSession.setCounselor(counselor);
            chatSession.setSessionStatus(ACTIVE);

            //상담원 작업량 증가
            counselor.increaseWorkload();

            chatSessionRepository.save(chatSession);
            counselorRepository.save(counselor);
            log.info("상담요청이 상담원에게 배정됨");

           return true;

        }
        return false;
    }
//상담 세션 완료 처리
    public void completeSession(Long sessionId){
        ChatSession chatSession= chatSessionRepository.findById(sessionId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 상담 세션입니다."));
        Counselor counselor = chatSession.getCounselor();

        chatSession.setSessionStatus(SessionStatus.COMPLETED);
        counselor.decreaseWorkload();

        chatSessionRepository.save(chatSession);
        counselorRepository.save(counselor);

        assignToCounselor();

    }

    protected Counselor getAvailableCounselorWithMinWorkload
            (List<Counselor> availableCounselors){
        if(availableCounselors.isEmpty()){
            return null;
        }
        Counselor minWorkloadCounselor = availableCounselors.get(0);
        for(Counselor counselor: availableCounselors){
            if(getWorkload(counselor)<getWorkload(minWorkloadCounselor)){
                minWorkloadCounselor = counselor;
            }
        }
        return minWorkloadCounselor;
    }
    //상담원 작업량 관련 메서드
    private boolean canAcceptMoreWork(Counselor counselor) {
        // Counselor 클래스의 메서드 호출
        return counselor.canAcceptMoreWork();
    }

    private int getWorkload(Counselor counselor) {
        return counselor.getCurrentWorkload();
    }
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    public void resetDailyWorkloads() {
        List<Counselor> allCounselors = counselorRepository.findAll();
        for (Counselor counselor : allCounselors) {
            counselor.resetDailyWorkload();
            counselorRepository.save(counselor);
        }
        log.info("모든 상담원의 일일 작업량이 초기화되었습니다.");
    }


}
