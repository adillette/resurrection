package marryus.studressmake.repository;

import marryus.studressmake.Counselor;
import marryus.studressmake.CounselorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CounselorRepository extends JpaRepository<Counselor, String> {

    @Query(value="select * from marryus_counselor where status = :status",nativeQuery = true)
    List<Counselor> findByStatus(@Param("status")String status);

    @Query(value = "SELECT * FROM MARRYUS_COUNSELOR", nativeQuery = true)
    List<Counselor> findAllWithNativeQuery();


    List<Counselor> findByStatusNot(CounselorStatus status);
}
