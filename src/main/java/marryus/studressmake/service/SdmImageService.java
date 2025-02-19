package marryus.studressmake.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import marryus.studressmake.entity.SdmImage;
import marryus.studressmake.repository.SdmImageRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Log4j2
public class SdmImageService {
    private final SdmImageRepository imageRepository;
    private final FileService fileService;

    @Transactional
    public void saveImages(Long sdmId, List<MultipartFile> files) {
        files.forEach(file -> {
            try {
                //** 변경: savedFileName 변수 직접 생성
                String originalFilename = file.getOriginalFilename();
                String fileExtension = FilenameUtils.getExtension(originalFilename);
                String savedFileName = UUID.randomUUID().toString() + "." + fileExtension;  // ** 추가

                //** 변경: fileService 호출 시 저장될 파일명도 전달하도록 수정 필요
                fileService.saveFile(sdmId, file);

                //** 기존 코드 유지: DB에 이미지 정보 저장
                SdmImage image = SdmImage.builder()
                        .sdmId(sdmId)
                        .fileName(savedFileName)
                        .originalFileName(file.getOriginalFilename())
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
}