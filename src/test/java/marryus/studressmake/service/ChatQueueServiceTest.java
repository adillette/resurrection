package marryus.studressmake.service;

import marryus.studressmake.CounselorStatus;
import marryus.studressmake.SessionStatus;
import marryus.studressmake.entity.ChatSession;
import marryus.studressmake.entity.Counselor;
import marryus.studressmake.repository.ChatSessionRepository;
import marryus.studressmake.repository.CounselorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.*;

import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatQueueServiceTest {

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @Mock
    private CounselorRepository counselorRepository;

    @InjectMocks
    @Spy
    private ChatQueueService chatQueueService;


    private ChatSession chatSession;
    private Counselor counselor;

    @BeforeEach
    void setUp(){
        Queue<ChatSession> emptyQueue = new LinkedList<>();
        ReflectionTestUtils.setField(chatQueueService,"requestQueue",emptyQueue);

        chatSession = new ChatSession();
        chatSession.setSessionId(1l);
        chatSession.setSessionStatus(SessionStatus.ACTIVE);

        counselor= new Counselor();
        counselor.setCounselorId("counselor-1");
        counselor.setCounselorName("Test Counselor");
        counselor.setStatus(CounselorStatus.AVAILABLE);
    }

    @Test
    @DisplayName("상담 요청이 큐에 추가되고 상태가 active로 설정되나?")
    void addToQueue_addSessiontoQueueAndSetStatusactive(){
        //given 준비
        when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(chatSession);
        //when 진행
        chatQueueService.addToQueue(chatSession);
        //then 검증
        assertEquals(SessionStatus.ACTIVE, chatSession.getSessionStatus());
        verify(chatSessionRepository, times(1)).save(chatSession);

        // Reset the queue for other tests
        ReflectionTestUtils.setField(chatQueueService, "requestQueue", new LinkedList<>());

    }

    @Test
    @DisplayName("사용 가능한 상담원이 없을 때 assigntocounselor가 false가 나오나 보자")
    void assignToCounselor_returnFalse_noavailablecounselors(){
        // Arrange
        Queue<ChatSession> queue = new LinkedList<>();
        queue.add(chatSession);
        ReflectionTestUtils.setField(chatQueueService, "requestQueue", queue);



        when(counselorRepository.findByStatus(CounselorStatus.AVAILABLE.name()))
                .thenReturn(Collections.emptyList());

        // Act
        boolean result = chatQueueService.assignToCounselor();

        // Assert
        assertFalse(result);
        verify(counselorRepository, times(1)).findByStatus(anyString());
        verify(chatSessionRepository, never()).save(any(ChatSession.class));
    }
//에러난다 3/28 수정필요
    @Test
    @DisplayName("사용 가능한 상담원이 있고 큐에 요청이 있을 때 assignToCounselor가 true를 반환하는지 테스트")
    void assignToCounselor_ShouldReturnTrue_WhenAvailableCounselorsAndRequestsInQueue() {
        // Arrange
        Queue<ChatSession> queue = new LinkedList<>();
        queue.add(chatSession);
        ReflectionTestUtils.setField(chatQueueService, "requestQueue", queue);

        when(counselorRepository.findByStatus(CounselorStatus.AVAILABLE.name()))
                .thenReturn(Collections.singletonList(counselor));
        when(chatSessionRepository.countByCounselorAndSessionStatus(any(Counselor.class), any(SessionStatus.class)))
                .thenReturn(0); // No current workload

        // Act
        boolean result = chatQueueService.assignToCounselor();

        // Assert
        assertTrue(result);
        verify(counselorRepository, times(1)).findByStatus(anyString());
        verify(chatSessionRepository, times(1)).save(any(ChatSession.class));
        verify(counselorRepository, times(1)).save(any(Counselor.class));
        assertEquals(counselor, chatSession.getCounselor());
        assertEquals(SessionStatus.ACTIVE, chatSession.getSessionStatus());
    }

    @Test
    @DisplayName("상담원이 최대 작업량에 도달했을 때 assignToCounselor가 false를 반환하는지 테스트")
    void assignToCounselor_ShouldReturnFalse_WhenCounselorReachedMaxWorkload() {
        // Arrange
        Queue<ChatSession> queue = new LinkedList<>();
        queue.add(chatSession);
        ReflectionTestUtils.setField(chatQueueService, "requestQueue", queue);

        // 상담원의 canAcceptMoreWork가 false 반환하도록 설정
        Counselor maxLoadCounselor = new Counselor();
        maxLoadCounselor.setCounselorId("counselor-max");
        maxLoadCounselor.setStatus(CounselorStatus.AVAILABLE);
        // Mockito를 사용하여 canAcceptMoreWork 메서드가 false를 반환하도록 설정
        Counselor spyCounselor = spy(maxLoadCounselor);
        when(spyCounselor.canAcceptMoreWork()).thenReturn(false);

        when(counselorRepository.findByStatus(CounselorStatus.AVAILABLE.name()))
                .thenReturn(Collections.singletonList(spyCounselor));

        // Act
        boolean result = chatQueueService.assignToCounselor();

        // Assert
        assertFalse(result);
        verify(counselorRepository, times(1)).findByStatus(anyString());
        verify(chatSessionRepository, never()).save(any(ChatSession.class));
    }

    @Test
    @DisplayName("작업량이 적은 상담원이 선택되는지 테스트")
    void assignToCounselor_ShouldSelectCounselorWithAvailableWorkload() {
        // Arrange
        Queue<ChatSession> queue = new LinkedList<>();
        queue.add(chatSession);

        // ChatQueueService를 스파이로 생성하되, getAvailableCounselorWithMinWorkload 메소드는 실제 구현을 사용
        ChatQueueService spyChatQueueService = spy(chatQueueService);
        ReflectionTestUtils.setField(spyChatQueueService, "requestQueue", queue);

        // 작업량이 많은 상담원과 적은 상담원 설정
        Counselor busyCounselor = new Counselor();
        busyCounselor.setCounselorId("counselor-busy");
        busyCounselor.setStatus(CounselorStatus.AVAILABLE);
        busyCounselor.setCurrentWorkload(5); // 작업량을 높게 설정

        Counselor availableCounselor = new Counselor();
        availableCounselor.setCounselorId("counselor-available");
        availableCounselor.setStatus(CounselorStatus.AVAILABLE);
        availableCounselor.setCurrentWorkload(1); // 작업량을 낮게 설정

        when(counselorRepository.findByStatus(CounselorStatus.AVAILABLE.name()))
                .thenReturn(Arrays.asList(busyCounselor, availableCounselor));

        // Act
        boolean result = spyChatQueueService.assignToCounselor();

        // Assert
        assertTrue(result);
        assertEquals(availableCounselor, chatSession.getCounselor()); // 작업량이 적은 availableCounselor가 선택되어야 함
        assertEquals(2, availableCounselor.getCurrentWorkload()); // 작업량이 1 증가했는지 확인
    }

    @Test
    @DisplayName("상담 세션 완료 처리가 올바르게 수행되는지 테스트")
    void completeSession_ShouldMarkSessionAsCompletedAndDecreaseCounselorWorkload() {
        // Arrange
        chatSession.setCounselor(counselor);

        when(chatSessionRepository.findById(anyLong())).thenReturn(Optional.of(chatSession));

        // Act
        chatQueueService.completeSession(1l);

        // Assert
        assertEquals(SessionStatus.COMPLETED, chatSession.getSessionStatus());
        verify(chatSessionRepository, times(1)).findById(1l);
        verify(chatSessionRepository, times(1)).save(chatSession);
        verify(counselorRepository, times(1)).save(counselor);
    }

    @Test
    @DisplayName("존재하지 않는 세션 ID로 completeSession 호출 시 예외가 발생하는지 테스트")
    void completeSession_ShouldThrowException_WhenSessionNotFound() {
        // Arrange
        when(chatSessionRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            chatQueueService.completeSession(2l);
        });

        assertEquals("존재하지 않는 상담 세션입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("라운드 로빈 방식으로 상담원이 순차적으로 선택되는지 테스트")
    void getNextAvailableCounselorInRoundRobin_ShouldSelectCounselorsInRoundRobin() {
        // Arrange - 테스트에 사용할 상담원 목록 생성
        List<Counselor> counselors = new ArrayList<>();

        // 3명의 상담원 생성
        for (int i = 1; i <= 3; i++) {
            Counselor counselor = new Counselor();
            counselor.setCounselorId("counselor-" + i);
            counselor.setStatus(CounselorStatus.AVAILABLE);
            counselor.setCurrentWorkload(0); // 모두 작업량 0으로 시작
            counselor.setDailyWorkload(0);   // 일일 작업량도 0으로 시작
            counselors.add(counselor);
        }

        // ReflectionTestUtils를 사용하여 lastAssignedCounselorIndex 필드를 -1로 설정
        ReflectionTestUtils.setField(chatQueueService, "lastAssignedCounselorIndex", -1);

        // Act & Assert - 여러 번 호출하여 라운드 로빈 동작 확인
        Counselor firstSelected = chatQueueService.getNextAvailableCounselorInRoundRobin(counselors);
        assertEquals("counselor-1", firstSelected.getCounselorId());
        assertEquals(0, ReflectionTestUtils.getField(chatQueueService, "lastAssignedCounselorIndex"));

        Counselor secondSelected = chatQueueService.getNextAvailableCounselorInRoundRobin(counselors);
        assertEquals("counselor-2", secondSelected.getCounselorId());
        assertEquals(1, ReflectionTestUtils.getField(chatQueueService, "lastAssignedCounselorIndex"));

        Counselor thirdSelected = chatQueueService.getNextAvailableCounselorInRoundRobin(counselors);
        assertEquals("counselor-3", thirdSelected.getCounselorId());
        assertEquals(2, ReflectionTestUtils.getField(chatQueueService, "lastAssignedCounselorIndex"));

        // 다시 처음으로 돌아가는지 확인
        Counselor fourthSelected = chatQueueService.getNextAvailableCounselorInRoundRobin(counselors);
        assertEquals("counselor-1", fourthSelected.getCounselorId());
        assertEquals(0, ReflectionTestUtils.getField(chatQueueService, "lastAssignedCounselorIndex"));
    }
    @Test
    @DisplayName("일일 작업량 제한에 도달한 상담원은 건너뛰고 다음 상담원이 선택되는지 테스트")
    void getNextAvailableCounselorInRoundRobin_ShouldSkipCounselorsWithMaxDailyWorkload() {
        // Arrange - 테스트에 사용할 상담원 목록 생성
        List<Counselor> counselors = new ArrayList<>();

        // 3명의 상담원 생성
        for (int i = 1; i <= 3; i++) {
            Counselor counselor = new Counselor();
            counselor.setCounselorId("counselor-" + i);
            counselor.setStatus(CounselorStatus.AVAILABLE);
            counselor.setCurrentWorkload(0);
            counselor.setDailyWorkload(0);

            // 두 번째 상담원은 일일 작업량 한도에 도달
            if (i == 2) {
                counselor.setDailyWorkload(20); // 최대 일일 작업량으로 설정
                // canAcceptMoreWork가 false를 반환하도록 스파이 설정
                counselor = spy(counselor);
                when(counselor.canAcceptMoreWork()).thenReturn(false);
            }

            counselors.add(counselor);
        }

        // ReflectionTestUtils를 사용하여 lastAssignedCounselorIndex 필드를 -1로 설정
        ReflectionTestUtils.setField(chatQueueService, "lastAssignedCounselorIndex", -1);

        // Act & Assert - 상담원이 순차적으로 선택되는지 확인
        Counselor firstSelected = chatQueueService.getNextAvailableCounselorInRoundRobin(counselors);
        assertEquals("counselor-1", firstSelected.getCounselorId());

        // 두 번째 상담원은 작업량 한도에 도달했으므로 세 번째 상담원이 선택되어야 함
        Counselor secondSelected = chatQueueService.getNextAvailableCounselorInRoundRobin(counselors);
        assertEquals("counselor-3", secondSelected.getCounselorId());

        // 다시 첫 번째 상담원으로 돌아가는지 확인
        Counselor thirdSelected = chatQueueService.getNextAvailableCounselorInRoundRobin(counselors);
        assertEquals("counselor-1", thirdSelected.getCounselorId());
    }

    @Test
    @DisplayName("모든 상담원이 일일 작업량 제한에 도달한 경우 전체 작업량이 가장 적은 상담원이 선택되는지 테스트")
    void getNextAvailableCounselorInRoundRobin_ShouldSelectCounselorWithMinWorkloadWhenAllReachDailyLimit() {
        // Arrange - 테스트에 사용할 상담원 목록 생성
        List<Counselor> counselors = new ArrayList<>();

        // 3명의 상담원 생성 (모두 일일 작업량 한도에 도달)
        for (int i = 1; i <= 3; i++) {
            Counselor counselor = new Counselor();
            counselor.setCounselorId("counselor-" + i);
            counselor.setStatus(CounselorStatus.AVAILABLE);
            // 다른 전체 작업량 설정
            counselor.setCurrentWorkload(10 * i); // 1: 10, 2: 20, 3: 30
            counselor.setDailyWorkload(20); // 모두 일일 한도에 도달

            // canAcceptMoreWork가 false를 반환하도록 스파이 설정
            counselor = spy(counselor);
            when(counselor.canAcceptMoreWork()).thenReturn(false);

            counselors.add(counselor);
        }

        // getAvailableCounselorWithMinWorkload 메소드가 첫 번째 상담원(작업량이 가장 적은)을 반환하도록 스파이 설정
        ChatQueueService spyService = spy(chatQueueService);
        when(spyService.getAvailableCounselorWithMinWorkload(anyList())).thenReturn(counselors.get(0));

        // Act
        Counselor selected = spyService.getNextAvailableCounselorInRoundRobin(counselors);

        // Assert
        assertEquals("counselor-1", selected.getCounselorId());
        // getAvailableCounselorWithMinWorkload 메소드가 호출되었는지 확인
        verify(spyService).getAvailableCounselorWithMinWorkload(counselors);
    }

}