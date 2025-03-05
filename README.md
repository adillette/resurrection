# 다시 만드는 첫번째 파이널 프로젝트
![Image](https://github.com/user-attachments/assets/37384ed3-d396-4c10-b184-4d30e85b0910)

## 왜 다시 만드는가?

파이널 프로젝트 당시 부족한 Spring 실력과 코드의 중복, 난해한 메서드의 사용으로 코드 가독성 및 코드 재사용성 저하되어있었음.

다른 팀원과 코드 리뷰를 하면서 가독성이 너무 떨어져서 이해가 어렵다는 피드백을 받아서... 국비학원 수료 후 김영한님 인강과 함께

죽어버린 코드에 Resurrection(부활)을 주기로 했음.

## 왜 채팅 프로그램인가? 

이전 회사에서 고객과 온라인 상담 프로그램으로 제품 상담을 했을때 업무 분담 및 책임이 제대로 이루어지지 않아서 힘들었던 경험이 있어서 

이를 해결할수 있는 프로그램을 만들고 싶었음. 

프로젝트 기간에는 게시판이 너무 늦어져서 못 만들었으나 취준 중에 다시 만들고 싶어서 만들었음.

## 사용기술
WebSocket, Stomp.Js, Spring-Boot , Oracle, Spring Data Jpa


## 25/01/21 요구사항 분석, DB 설계 ERD 작성
![Image](https://github.com/user-attachments/assets/fc7452bc-65af-4771-b9ec-79e29261b748)

### [1] 요구사항 분석
----
-게시판에 필요한 기능 정리

1. 글쓰기 (관리자만) /수정 (관리자만) /삭제(관리자만)/조회(전부가능)

2. 화면 흐름

ⓐ게시판 누르고 들어가면 →

ⓑ카테고리별로 정렬되고

ⓒ검색하면  →  업체명 나오고

ⓓ카테고리로 들어가면 →  관리자일경우에는 글쓰기 / 수정/삭제 가능

ⓕ댓글 게시판 - 구매한 사람의 경우에는 구매자 라고 따로 이름이 붙음

     -대댓글 기능 가능

ⓖ찜하기 기능( 좋아요 버튼 누르면 DB에 찜하기 카운트 되게)

![Image](https://github.com/user-attachments/assets/50a00261-8f30-4e15-81e1-2417d1c5436f)
***
### [2] 데이터베이스 설계
----
테이블 구조 설계 (게시글, 댓글, 파일 첨부 등)


SQL 스크립트 작성


도메인 객체(Entity) 설계


테이블과 매핑될 객체 생성


DTO 클래스 설계


DAO(Repository) 계층 구현


데이터베이스 접근 로직 구현


CRUD 기능 구현

##고객센터 채팅 프로그램 만들기
----
1. 요구사항 분석

고객 문의와 상담하는 직원이 일대일로 매치되지 않다보니 상담이 고르게 진행되지 않는 문제, 
익명으로 진행되어 누가 담당인지 확인이 어렵다는 문제 
담당자마다 다른 답변으로 혼선을 빚어서 통일된 답변을 위해 담당자가 필요.


2.  ERD 작성
3.  
![Image](https://github.com/user-attachments/assets/fc7452bc-65af-4771-b9ec-79e29261b748)
4.  테이블 구조 설계- 25.03.05 수정정
COUNSELOR {
        string counselorId PK
        string counselorName
        CounselorStatus status
    }

    CHAT_SESSION {
        Long sessionId PK
        string customerId
        string counselorId FK
        LocalDateTime startTime
        LocalDateTime endTime
        string counselorName
        SessionStatus sessionStatus
    }

    CHAT_MESSAGE {
        Long messageId PK
        Long sessionId FK
        string senderId
        string messageContent
        LocalDateTime sendTime
        MessageType messageType
    }

    SDM {
        Long id PK
        string itemName
        string shopName
        string address
        string phoneNumber
        string description
        int price
        LocalDateTime createAt
        LocalTime openTime
        LocalTime closeTime
        ShopCategory category
    }

    SDM_IMAGE {
        Long id PK
        Long sdmId FK
        string fileName
        string originalFileName
        string fileType
        Long fileSize
        LocalDateTime createdAt
    }

    CHAT_RESPONSE {
        string type
        Long sessionId FK
        string counselorName
        string message
    }
6.  도메인 객체(Entity) 설계
-2/6 오후 완료
7.  테이블과 맵핑될 객체 생성
-2/6 오후 완료
8.  DTO 클래스 설계
-2/6 오후 완료
9.  DAO 계층 구형
-2/6 오후 완료
10.  데이터 베이스 접근 로직 구현

11.  CRUD 기능 구현

12.  Service 계층 구현

13.  비즈니스 로직 구형

14.  트랜잭션 처리

15.  Controller 구현

16.  API 엔드 포인트 정의, 요청/응답 처리

17.  입력값 검증

18.  view 구현

19.  ![Image](https://github.com/user-attachments/assets/7a375294-2c73-4a10-8ba6-770ad13a611f)
20.  문제점 발견 상태에서 행동을 하려고 하니까 자꾸 일이 늘어난다.

    상담원 상태 변경하는거 원래 안만들기로 했는데 만들려다가 시간 다 보냄

22.  화면 레이아웃 작성

23.  ![Image](https://github.com/user-attachments/assets/80217cd0-c020-4a9e-981d-83dc0da5c147)

24.  사용자 인터페이스 구현
