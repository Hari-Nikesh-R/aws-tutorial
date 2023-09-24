package com.example.s3;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    ResponseEntity<String> uploadImage(MultipartFile multipartFile);
        ResponseEntity<InputStreamResource> getImage(String file);
}
