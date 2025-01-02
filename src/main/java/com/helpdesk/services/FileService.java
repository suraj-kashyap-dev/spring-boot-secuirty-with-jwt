package com.helpdesk.services;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService {
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    
    @Value("${app.upload.allowed-types:image/jpeg,image/png,image/gif}")
    private String[] allowedTypes;
    
    @Value("${app.upload.max-size:5242880}")
    private long maxFileSize;

    public String uploadFile(MultipartFile file, String subdirectory) throws IOException {
        this.validateFile(file);
    
        String fileName = generateFileName(file);
        Path uploadPath = createUploadPath(subdirectory);
    
        saveFile(file, uploadPath, fileName);
    
        return uploadPath.resolve(fileName).toString();
    }

    private void validateFile(MultipartFile file) throws FileUploadException {
        if (file.isEmpty()) {
            throw new FileUploadException("File is empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new FileUploadException("File size exceeds maximum limit");
        }

        if (! isAllowedFileType(file.getContentType())) {
            throw new FileUploadException("File type not allowed");
        }
    }

    private boolean isAllowedFileType(String contentType) {
        for (String type : allowedTypes) {
            if (type.equals(contentType)) {
                return true;
            }
        }

        return false;
    }

    private String generateFileName(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();

        @SuppressWarnings("null")
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        return UUID.randomUUID().toString() + extension;
    }

    private Path createUploadPath(String subdirectory) throws IOException {
        Path uploadPath = Paths.get(uploadDir, subdirectory);

        if (! Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        return uploadPath;
    }

    private String saveFile(MultipartFile file, Path uploadPath, String fileName) throws IOException {
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    public void deleteFile(String fileName, String subdirectory) throws IOException {
        Path filePath = Paths.get(uploadDir, subdirectory, fileName);

        Files.deleteIfExists(filePath);
    }
}