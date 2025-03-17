package marryus.studressmake.repository;

import marryus.studressmake.entity.Sdm;
import marryus.studressmake.entity.SdmDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SdmRepository extends JpaRepository<Sdm,Long> {

 Page<SdmDTO> findAll(Specification<SdmDTO> spec, Pageable pageable);

 Page<SdmDTO> findByCategory(String category,Pageable pageable);

 Page<SdmDTO> findByOrderByPriceAsc(Pageable pageable);


 @Query("select s from Sdm s where lower(s.itemName) like lower(concat('%',:itemName,'%'))")
 Page<Sdm> searchByItemName(@Param("itemName") String itemName, Pageable pageable);
}
