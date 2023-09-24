package com.example.controller.s3;

import com.example.s3.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/s3")
public class S3Controller {
    @Autowired
    private S3Service service;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return service.uploadImage(file);
    }
    @GetMapping("/download/{filename}")
    public ResponseEntity<InputStreamResource> getImage(@PathVariable("filename") String fileName){
        return service.getImage(fileName);
    }
}
