package marryus.studressmake.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.stream.Collectors;

import marryus.studressmake.entity.Sdm;
import marryus.studressmake.entity.SdmDTO;
import marryus.studressmake.entity.SdmPageRequestDTO;
import marryus.studressmake.entity.SdmPageResponseDTO;
import marryus.studressmake.repository.SdmRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;

@Service
@Slf4j
@Builder
@RequiredArgsConstructor
public class SdmService {

    private final SdmRepository sdmRepository;
    private final SdmImageService imageService;

    @Transactional
    public Long register(SdmDTO sdmDTO){

        // Sdm 저장만 수행
        return sdmRepository.save(sdmDTO.toEntity()).getId();

    }

    @Transactional
    public void modify(Long id, SdmDTO sdmDTO , List<MultipartFile> newFiles){
        //기존 엔티티 조회
        Sdm sdm = sdmRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("해당 게시물을 찾을 수 없습니다."));

        // 기본 정보 업데이트
        updateEntityFromDTO(sdm, sdmDTO);

        //새로운 이미지 파일이 있다면 처리
        if(newFiles !=null && !newFiles.isEmpty()){
            imageService.deleteImages(id);// 기존 이미지 삭제
            imageService.saveImages(id, newFiles);//새이미지 생성
        }

    }

    @Transactional
    public void remove(Long id){
        //이미지 파일 먼저 삭제
       imageService.deleteImages(id);
       sdmRepository.deleteById(id);

    }

    /**
     * 페이징 처리된 목록 조회
     *
     */
    public SdmPageResponseDTO<SdmDTO> getList(SdmPageRequestDTO pageRequestDTO){
        Pageable pageable= pageRequestDTO.getPageable();
        Page<Sdm> result = sdmRepository.findAll(pageable);

        List<SdmDTO> dtoList = result.getContent().stream()
                .map(this::entityToDTO).collect(Collectors.toList());
    return SdmPageResponseDTO.of(dtoList,pageRequestDTO, result.getTotalElements());

    }

    /**
     * 단일 항목 조회
     * @param
     * @return
     */
    public SdmDTO get(Long id){
        Sdm sdm = sdmRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("해당 게시물을 찾을수 없습니다."));

        return entityToDTO(sdm);
    }
    private Sdm dtoToEntity(SdmDTO sdmDTO) {
        return Sdm.builder()
                .itemName(sdmDTO.getItemName())
                .shopName(sdmDTO.getShopName())
                .address(sdmDTO.getAddress())
                .phoneNumber(sdmDTO.getPhoneNumber())
                .description(sdmDTO.getDescription())
                .price(sdmDTO.getPrice())
                .openTime(sdmDTO.getOpenTime())
                .closeTime(sdmDTO.getCloseTime())
                .category(sdmDTO.getCategory())
                .build();
    }
    // Entity -> DTO 변환
    private SdmDTO entityToDTO(Sdm entity) {
        return SdmDTO.builder()
                .id(entity.getId())
                .itemName(entity.getItemName())
                .shopName(entity.getShopName())
                .address(entity.getAddress())
                .phoneNumber(entity.getPhoneNumber())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .createAt(entity.getCreateAt())
                .openTime(entity.getOpenTime())
                .closeTime(entity.getCloseTime())
                .category(entity.getCategory())
                .build();
    }
    // Entity 업데이트
    private void updateEntityFromDTO(Sdm entity, SdmDTO dto) {
        entity.setItemName(dto.getItemName());
        entity.setShopName(dto.getShopName());
        entity.setAddress(dto.getAddress());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setOpenTime(dto.getOpenTime());
        entity.setCloseTime(dto.getCloseTime());
        entity.setCategory(dto.getCategory());
    }

}
