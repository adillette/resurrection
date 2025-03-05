package marryus.studressmake.controller;

import org.springframework.core.io.Resource;
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
import org.springframework.http.HttpHeaders;
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
     * sdm의 아이디로 이미지 정보 조회하기
     * @param pageRequestDTO
     * @return
     */
    @GetMapping("/image/{sdmId}")
    public ResponseEntity<List<SdmImageDTO>> getImagesBySdmId(@PathVariable Long sdmId){
        try {
            List<SdmImageDTO> images = imageService.getImageDetailsBySdmId(sdmId);

            if(images.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(images, HttpStatus.OK);
        } catch (Exception e) {
            log.error("이미지 조회 중 오류 발생: {}", sdmId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    /**
     * 이미지 파일 조회 엔드포인트
     */
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        try {
            Resource resource = imageService.loadImageAsResource(fileName);

            // 파일 확장자에 따라 적절한 Content-Type 설정
            String contentType = "image/jpeg"; // 기본값
            String fileNameLower = fileName.toLowerCase();

            if (fileNameLower.endsWith(".png")) {
                contentType = "image/png";
            } else if (fileNameLower.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (fileNameLower.endsWith(".bmp")) {
                contentType = "image/bmp";
            } else if (fileNameLower.endsWith(".webp")) {
                contentType = "image/webp";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("이미지 로드 중 오류 발생: {}", fileName, e);
            return ResponseEntity.notFound().build();
        }
    }



    /**
     * 목록 조회- 이미지 정보 포함
     *
     */
    @GetMapping("/list")
    public ResponseEntity<SdmPageResponseDTO<SdmDTO>>
                                list(SdmPageRequestDTO pageRequestDTO){
        try{
            log.info("리스트페이지 요청 정보: {}", pageRequestDTO);

            SdmPageResponseDTO<SdmDTO> result =
                    sdmService.getList(pageRequestDTO);


            for(SdmDTO dto: result.getDtoList()){
                //이미지 정보 조회 로직 추가 필요
                List<String> fileNames = imageService.getImagesBySdmId(dto.getId());

                dto.setUploadFileNames(fileNames);

                //이미지 전체 경로 설정
                List<SdmImageDTO> imageDetails= imageService.getImageDetailsBySdmId(dto.getId());
                List<String> imagePaths = imageDetails.stream()
                                .map(SdmImageDTO::getFullPath)
                                        .collect(Collectors.toList());

                dto.setImageUrls(imagePaths);
                log.info("이미지 파일명: {}", fileNames);
                log.info("이미지 경로: {}", imagePaths);
                log.info("SDM ID: {}", dto.getId());
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
           if(result !=null){
               //이미지 정보 조회 및 설정
               List<String> fileNames = imageService.getImagesBySdmId(result.getId());

               log.info("이미지 정보 조회 및 설정 fileNames:{}",fileNames);
               result.setUploadFileNames(fileNames);

               // 이미지 URL 설정 (프론트엔드에서 직접 사용 가능한 URL)
               List<SdmImageDTO> imageDetails = imageService.getImageDetailsBySdmId(result.getId());
               log.info("이미지 URL 설정 imageDetails:{}",imageDetails);

               List<String> imagePaths = imageDetails.stream()
                       .map(SdmImageDTO::getFullPath)
                       .collect(Collectors.toList());

               result.setImageUrls(imagePaths);
               return new ResponseEntity<>(result, HttpStatus.OK);
           }else{
               return new ResponseEntity<>(HttpStatus.NOT_FOUND);
           }

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("상세창 조회 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }



}
