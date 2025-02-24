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

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    @Value("${file.upload.path}")
    private String uploadPath;

    public void saveFile(Long sdmId, MultipartFile file) throws IOException{
        if(file.isEmpty()) return;

        String originalFilename = file.getOriginalFilename();
        String fileExtension = FilenameUtils.getExtension(originalFilename);
        // sdmId를 파일명에 포함시켜 구분할 수 있게 함
        String savedFilename = sdmId + "_" + UUID.randomUUID().toString() + "." + fileExtension;

        //파일을 uploadPath 에 직접 저장
        File saveFile = new File(uploadPath + File.separator+ savedFilename);

        // 파일을 uploadPath 직접 저장
        File savedFile = new File(uploadPath + File.separator + savedFilename);
        file.transferTo(savedFile);

        log.info("파일 저장 File saved: "+ savedFile.getAbsolutePath());

    }

    public void deleteAllFiles(Long sdmId){

        File directory = new File(uploadPath);
        File[] files = directory.listFiles((dir,name)->name.startsWith(sdmId + "_"));

        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

}
