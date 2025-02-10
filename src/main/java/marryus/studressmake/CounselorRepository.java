package marryus.studressmake;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CounselorRepository extends JpaRepository<Counselor, String> {


    @Query("SELECT c FROM Counselor c WHERE c.status=:status")
    List<Counselor> findByStatus(@Param("status") CounselorStatus status);



    List<Counselor> findByStatusNot(CounselorStatus status);
}
