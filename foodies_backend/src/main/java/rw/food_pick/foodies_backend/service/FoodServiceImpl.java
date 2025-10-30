package rw.food_pick.foodies_backend.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import rw.food_pick.foodies_backend.entity.FoodEntity;
import rw.food_pick.foodies_backend.io.FoodRequest;
import rw.food_pick.foodies_backend.io.FoodResponse;
import rw.food_pick.foodies_backend.repository.FoodRepository;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService{

    private final S3Client s3Client;
    private final FoodRepository foodRepository;

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



    @Override
    public FoodResponse addFood(FoodRequest request, MultipartFile file) {
        FoodEntity newFoodEntity = convertToEntity(request);
        String imageUrl = uploadFile(file);
        newFoodEntity.setImageUrl(imageUrl);
        newFoodEntity = foodRepository.save(newFoodEntity);
        return convertToResponse(newFoodEntity);


    }

    @Override
    public List<FoodResponse> readFoods() {
        List<FoodEntity> databaseEntries = foodRepository.findAll();
        return databaseEntries.stream().map(object -> convertToResponse(object)).collect(Collectors.toList());
    }


    private FoodEntity convertToEntity(FoodRequest request){
        return FoodEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .build();

    }

    private FoodResponse convertToResponse(FoodEntity entity){
        return FoodResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .price(entity.getPrice())
                .category(entity.getCategory())
                .build();

    }
}
