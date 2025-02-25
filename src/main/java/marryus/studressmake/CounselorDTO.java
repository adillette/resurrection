package marryus.studressmake;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class CounselorDTO {
    private String counselorId; //cs1, cs2 ,cs3
    private String counselorName;
    private CounselorStatus status;

    public CounselorDTO(String counselorId, String counselorName, CounselorStatus status) {
        this.counselorId = counselorId;
        this.counselorName = counselorName;
        this.status = status;
    }
}
