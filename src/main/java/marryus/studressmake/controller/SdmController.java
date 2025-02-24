package marryus.studressmake.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marryus.studressmake.entity.SdmDTO;
import marryus.studressmake.entity.SdmImageDTO;
import marryus.studressmake.entity.SdmPageRequestDTO;
import marryus.studressmake.entity.SdmPageResponseDTO;
import marryus.studressmake.service.FileService;
import marryus.studressmake.service.SdmImageService;
import marryus.studressmake.service.SdmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/sdm")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class SdmController {
    private final SdmService sdmService;
    private final SdmImageService imageService;
    private final FileService fileService;

    /**
     * 등록
     */
    // 기존 sdm 등록
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> register(
            @RequestPart("sdmData") String sdmData,
            @RequestPart("files") List<MultipartFile> files) {
        try {

            log.info("Received sdmData: {}", sdmData);
            log.info("Received files count: {}", files.size());
            // 1. Sdm 데이터 저장
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SdmDTO sdmDTO = objectMapper.readValue(sdmData, SdmDTO.class);
            Long sdmId = sdmService.register(sdmDTO);  // Sdm만 저장

            // 2. 이미지 저장 - 별도 호출
            if (files != null && !files.isEmpty()) {
                imageService.saveImages(sdmId, files);
            }


            Map<String, String> result = new HashMap<>();
            result.put("result", "success");
            result.put("id", sdmId.toString());

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            log.error("등록 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     *
     * @param sdmId
     * @param files
     * @return

    // 이미지 업로드를 위한 새로운 엔드포인트
    @PostMapping("/images/upload")
    public ResponseEntity<Map<String, String>> uploadImages(
            @RequestParam("sdmId") Long sdmId,
            @RequestParam("files") List<MultipartFile> files) {
        try {

            imageService.saveImages(sdmId, files);

            Map<String, String> result = new HashMap<>();
            result.put("result", "success");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            log.error("이미지 업로드 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
     */
    // 이미지 삭제를 위한 엔드포인트
    @DeleteMapping("/images/{sdmId}")
    public ResponseEntity<Map<String, String>> deleteImages(
            @PathVariable Long sdmId) {
        try {
            imageService.deleteImages(sdmId);

            Map<String, String> result = new HashMap<>();
            result.put("result", "success");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            log.error("이미지 삭제 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    /**
     * 수정
     */
    @PutMapping("/modify/{id}")
    public ResponseEntity<Map<String, String>> modify(
            @PathVariable Long id,
            @RequestPart("sdmData") String sdmData,
            @RequestPart(value="files", required = false)
            List<MultipartFile> files){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SdmDTO sdmDTO = objectMapper.readValue(sdmData, SdmDTO.class);
            sdmDTO.setId(id);

            sdmService.modify(id, sdmDTO, files);

            Map<String, String> result = new HashMap<>();
            result.put("result", "success");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            log.error("수정 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    /**
     * 삭제
     */
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Map<String, String>> remove(@PathVariable Long id){
        try{
            sdmService.remove(id);

            Map<String, String> result = new HashMap<>();
            result.put("result","success");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            log.error("삭제 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    /**
     * 목록 조회
     *
     */
    @GetMapping("/list")
    public ResponseEntity<SdmPageResponseDTO<SdmDTO>>
                                list(SdmPageRequestDTO pageRequestDTO){
        try{
            log.info("페이지 요청 정보: {}", pageRequestDTO);

            SdmPageResponseDTO<SdmDTO> result =
                    sdmService.getList(pageRequestDTO);


            for(SdmDTO dto: result.getDtoList()){
                //이미지 정보 조회 로직 추가 필요
                List<String> fileNames = imageService.getImagesBySdmId(dto.getId());

                dto.setUploadFileNames(fileNames);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            log.error("목록 조회중 오류 발생: ",e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }
/**
 * 단일 항목 조회
 */
    @GetMapping("/{id}")
    public ResponseEntity<SdmDTO> getOneList(@PathVariable Long id){
        try{
            SdmDTO result = sdmService.get(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("조회 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }



}
