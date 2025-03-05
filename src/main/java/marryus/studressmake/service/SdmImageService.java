package marryus.studressmake.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import marryus.studressmake.entity.SdmImage;
import marryus.studressmake.entity.SdmImageDTO;
import marryus.studressmake.repository.SdmImageRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class SdmImageService {

    private final SdmImageRepository imageRepository;
    private final FileService fileService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public void saveImages(Long sdmId, List<MultipartFile> files) {
        files.forEach(file -> {
            try {
                //** 변경: savedFileName 변수 직접 생성
                String originalFilename = file.getOriginalFilename();
                String fileExtension = FilenameUtils.getExtension(originalFilename);
                String savedFileName = sdmId + "_" + UUID.randomUUID().toString() + "." + fileExtension;


                fileService.saveFile(sdmId, file, savedFileName);

                //** 기존 코드 유지: DB에 이미지 정보 저장
                SdmImage image = SdmImage.builder()
                        .sdmId(sdmId)
                        .fileName(savedFileName)
                        .originalFileName(file.getOriginalFilename())
                        .fileType(file.getContentType())
                        .fileSize(file.getSize())
                        .build();

                imageRepository.save(image);
            } catch (IOException e) {
                throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
            }
        });
    }


    public void deleteImages(Long sdmId) {
        // 1. DB에서 해당 sdmId의 이미지 정보 조회
        List<SdmImage> images = imageRepository.findBySdmId(sdmId);

        // 2. 실제 파일 삭제
        fileService.deleteAllFiles(sdmId);

        // 3. DB에서 이미지 정보 삭제
        imageRepository.deleteAllBySdmId(sdmId);
    }

    // 이미지 파일명만 조회
    public List<String> getImagesBySdmId(Long sdmId) {
        List<SdmImage> images = imageRepository.findBySdmId(sdmId);
        return images.stream()
                .map(SdmImage::getFileName)
                .collect(Collectors.toList());
    }

    // 이미지 상세 정보 조회 (DTO 반환)
    public List<SdmImageDTO> getImageDetailsBySdmId(Long sdmId) {
        List<SdmImage> images = imageRepository.findBySdmId(sdmId);
        return images.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private SdmImageDTO  convertToDTO(SdmImage image) {
        return SdmImageDTO.builder()
                .id(image.getId())
                .sdmId(image.getSdmId())
                .fileName(image.getFileName())
                .originalFileName(image.getOriginalFileName())
                .fileType(image.getFileType())
                .fileSize(image.getFileSize())
                .fullPath("/api/sdm/view/" + image.getFileName()) // 이미지 URL 경로 설정
                .build();
    }

    public Resource loadImageAsResource(String fileName) {
        try {
            Path imagePath = Paths.get(uploadDir).resolve(fileName).normalize();
            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("이미지 파일을 찾을 수 없거나 읽을 수 없습니다: " + fileName);
            }
        } catch (Exception e) {
            log.error("이미지 파일 로드 중 오류 발생: {}", fileName, e);
            throw new RuntimeException("이미지 파일 로드 중 오류 발생: " + fileName, e);
        }
    }
}