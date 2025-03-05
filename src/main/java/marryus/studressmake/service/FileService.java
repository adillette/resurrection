package marryus.studressmake.service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 원래 메서드 (기존 코드와의 호환성을 위해 유지)
    public void saveFile(Long sdmId, MultipartFile file) throws IOException {
        // 랜덤 파일명 생성
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFileName = sdmId + "_" + UUID.randomUUID().toString() + extension;

        saveFile(sdmId, file, savedFileName);
    }

    // 새로운 메서드 (파일명을 지정할 수 있음)
    public void saveFile(Long sdmId, MultipartFile file, String savedFileName) throws IOException {
        // 디렉토리 생성
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 파일 저장
        Path filePath = uploadPath.resolve(savedFileName);
        Files.copy(file.getInputStream(), filePath);

        log.info("파일 저장 완료: {}", filePath);
    }

    // 모든 파일 삭제
    public void deleteAllFiles(Long sdmId) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (Files.exists(uploadPath)) {
                Files.list(uploadPath)
                        .filter(path -> path.getFileName().toString().startsWith(sdmId + "_"))
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                                log.info("파일 삭제 완료: {}", path);
                            } catch (IOException e) {
                                log.error("파일 삭제 중 오류 발생: {}", path, e);
                            }
                        });
            }
        } catch (IOException e) {
            log.error("파일 삭제 중 오류 발생", e);
            throw new RuntimeException("파일 삭제 중 오류가 발생했습니다.", e);
        }
    }

    // 단일 파일 삭제
    public void deleteFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("파일 삭제 완료: {}", filePath);
            } else {
                log.warn("삭제할 파일을 찾을 수 없습니다: {}", filePath);
            }
        } catch (IOException e) {
            log.error("파일 삭제 중 오류 발생: {}", fileName, e);
            throw new RuntimeException("파일 삭제 중 오류가 발생했습니다.", e);
        }
    }

}
