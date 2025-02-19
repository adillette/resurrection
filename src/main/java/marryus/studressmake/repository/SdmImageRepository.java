package marryus.studressmake.repository;

import marryus.studressmake.entity.SdmImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SdmImageRepository extends JpaRepository<SdmImage,Long> {
    List<SdmImage> findBySdmId(Long sdmId);
    void deleteAllBySdmId(Long sdmId);
}
