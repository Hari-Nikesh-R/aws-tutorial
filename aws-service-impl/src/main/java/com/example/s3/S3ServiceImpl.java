package com.example.s3;

import com.example.s3.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class S3ServiceImpl implements S3Service {

    @Autowired
    private S3Client s3Client;

    @Override
    public ResponseEntity<String> uploadImage(MultipartFile file) {
        try {
            String bucketName = "imageploadsqw";
            String fileName = file.getOriginalFilename();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .contentLength(file.getResource().contentLength())
                    .key(fileName)
                    .build();
            System.out.println(file.getOriginalFilename());
            System.out.println("Content length: "+file.getResource().contentLength());
            System.out.println(file.getContentType());
            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), putObjectRequest.contentLength()));
            if (response != null) {
                String imageURL = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(fileName)).toString();
                return new ResponseEntity<>(imageURL, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Error uploading image", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error uploading image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    public ResponseEntity<InputStreamResource> getImage(String fileName) {
      try {
        String bucketName = "imageploadsqw";
          GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        ResponseInputStream<GetObjectResponse> getObjectResponse = s3Client.getObject(getObjectRequest);
        InputStream imageStream = new ByteArrayInputStream(getObjectResponse.readAllBytes());

        MediaType mediaType = MediaType.IMAGE_JPEG;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(new InputStreamResource(imageStream));
    } catch (IOException e) {
        e.printStackTrace();
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
}
