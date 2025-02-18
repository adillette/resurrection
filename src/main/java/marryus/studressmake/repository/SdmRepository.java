package marryus.studressmake.repository;

import marryus.studressmake.entity.Sdm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SdmRepository extends JpaRepository<Sdm,Long> {
    List<Sdm> getAllSdmList();


}
