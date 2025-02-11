# 다시 만드는 첫번째 파이널 프로젝트
![Image](https://github.com/user-attachments/assets/37384ed3-d396-4c10-b184-4d30e85b0910)
## 25/01/21 요구사항 분석, DB 설계 ERD 작성
![Image](https://github.com/user-attachments/assets/b9f7a8ec-2627-4675-a29d-7f0dd247566f)
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
![Image](https://github.com/user-attachments/assets/a0e04253-2a52-4e24-8a4e-3760951caa0d)

4.  테이블 구조 설계
   ChatSession {

        Long sessionId PK
    
        String customerId
    
        String counselorId FK
    
        DateTime startTime
    
        DateTime endTime
    
        String sessionStatus
    
    }

    
    ChatMessage {
    
        Long messageId PK
    
        Long sessionId FK
    
        String senderId
    
        String messageContent
    
        DateTime sendTime
    
        String messageType
    
    }
    
    Counselor {
    
        String counselorId PK
    
        String counselorName
    
        String status
    
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

20.  화면 레이아웃 작성

21.  

22.  사용자 인터페이스 구현
