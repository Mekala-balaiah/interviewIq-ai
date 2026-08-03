package com.interviewiq.common.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    
    /**
     * Uploads a file to storage and returns its URL/path.
     */
    String uploadFile(MultipartFile file, String directory);
    
    /**
     * Deletes a file from storage given its URL/path.
     */
    void deleteFile(String fileUrl);
}
