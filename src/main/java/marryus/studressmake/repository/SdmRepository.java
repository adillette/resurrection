package marryus.studressmake.repository;

import marryus.studressmake.entity.Sdm;
import marryus.studressmake.entity.SdmDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SdmRepository extends JpaRepository<Sdm,Long> {

 Page<SdmDTO> findAll(Specification<SdmDTO> spec, Pageable pageable);

}
