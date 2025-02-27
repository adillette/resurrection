package marryus.studressmake.repository;

import marryus.studressmake.entity.Counselor;
import marryus.studressmake.CounselorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CounselorRepository extends JpaRepository<Counselor, String> {
    //available 대문자 이슈를 없애기 위해
    @Query("SELECT c FROM Counselor c WHERE LOWER(c.status) = LOWER(:status)")
    List<Counselor> findByStatusIgnoreCase(@Param("status") CounselorStatus status);
    
    @Query(value="select * from marryus_counselor where status = :status",nativeQuery = true)
    List<Counselor> findByStatus(@Param("status")String status);

    @Query(value = "SELECT * FROM MARRYUS_COUNSELOR", nativeQuery = true)
    List<Counselor> findAllWithNativeQuery();

    @Query("SELECT c FROM Counselor c LEFT JOIN FETCH c.sessions WHERE c.counselorId = :counselorId")
    Optional<Counselor> findByIdWithSessions(@Param("counselorId") String counselorId);

    List<Counselor> findByStatusNot(CounselorStatus status);


}
