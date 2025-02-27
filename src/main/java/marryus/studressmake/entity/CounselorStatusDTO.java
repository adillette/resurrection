package marryus.studressmake.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CounselorStatusDTO {

    private String counselorId;
    private String status;
    private String counselorName;

    public CounselorStatusDTO(){

    }
    // 두 개의 매개변수를 받는 생성자 추가 (이전 코드와의 호환성 유지)
    public CounselorStatusDTO(String counselorId, String status) {
        this.counselorId = counselorId;
        this.status = status;
        this.counselorName = ""; // 기본값 설정
    }

    // 세 개의 매개변수를 받는 생성자
    public CounselorStatusDTO(String counselorId, String counselorName, String status) {
        this.counselorId = counselorId;
        this.counselorName = counselorName;
        this.status = status;
    }

}
