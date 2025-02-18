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
        String savedFilename= UUID.randomUUID().toString()+ "." + fileExtension;

        //sdmId 별로 디렉토리 생성
        String dirPath = uploadPath + File.separator + sdmId;
        File directory = new File(dirPath);
        if(!directory.exists()){
            directory.mkdirs();
        }

        //파일 저장
        File savedFile = new File(dirPath+ File.separator + savedFilename );
        file.transferTo(savedFile);

        log.info("파일 저장 File saved: "+ savedFile.getAbsolutePath());

    }

    public void deleteAllFiles(Long sdmId){
        String dirPath = uploadPath + File.separator + sdmId;
        File directory = new File(dirPath);

        if(directory.exists()){
            File[] files = directory.listFiles();
            if(files!=null){
                for(File file:files){
                    file.delete();
                }
            }
            directory.delete();
        }
    }

}
