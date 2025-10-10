package rw.food_pick.foodies_backend.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService{

    private final S3Client s3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;
    @Override
    public String uploadFile(MultipartFile file) {
       String filenameExtension=  file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
       String key = UUID.randomUUID().toString()+"."+filenameExtension;

       try{
           PutObjectRequest putObjectRequest= PutObjectRequest.builder()
                   .bucket(bucketName)
                   .key(key)
                   .acl("public-read")
                   .contentType(file.getContentType())
                   .build();

           PutObjectResponse response =  s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

           if(response.sdkHttpResponse().isSuccessful()){
               return "https://"+bucketName+".s3.amazonaws.com/"+key;
           } else{
               throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "file upload failed");
           }
       }catch(IOException ex){
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occured while uploading the file");

       }
    }
}
