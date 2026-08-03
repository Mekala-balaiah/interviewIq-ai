package com.interviewiq.common.storage;

import com.interviewiq.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class LocalFileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;

    public LocalFileStorageServiceImpl(@Value("${file.upload-dir:./uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String directory) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        String extension = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) {
            extension = originalFilename.substring(i);
        }
        
        String newFilename = UUID.randomUUID().toString() + extension;
        
        try {
            if (originalFilename.contains("..")) {
                throw new BusinessException("Sorry! Filename contains invalid path sequence " + originalFilename);
            }

            Path targetDir = this.fileStorageLocation.resolve(directory);
            Files.createDirectories(targetDir);

            Path targetLocation = targetDir.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("File stored locally: {}", targetLocation);
            
            // Return a local URL or path
            return "/api/v1/files/download/" + directory + "/" + newFilename;
        } catch (IOException ex) {
            throw new BusinessException("Could not store file " + originalFilename + ". Please try again!");
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        // fileUrl is like: /api/v1/files/download/resumes/uuid.pdf
        try {
            String[] parts = fileUrl.split("/");
            if (parts.length >= 2) {
                String filename = parts[parts.length - 1];
                String directory = parts[parts.length - 2];
                Path filePath = this.fileStorageLocation.resolve(directory).resolve(filename).normalize();
                Files.deleteIfExists(filePath);
                log.info("File deleted locally: {}", filePath);
            }
        } catch (IOException e) {
            log.error("Failed to delete local file {}", fileUrl, e);
        }
    }
}
