package rw.food_pick.foodies_backend.service;

import org.springframework.web.multipart.MultipartFile;
import rw.food_pick.foodies_backend.io.FoodRequest;
import rw.food_pick.foodies_backend.io.FoodResponse;

public interface FoodService {
    String uploadFile(MultipartFile file);

    FoodResponse addFood(FoodRequest request, MultipartFile file);
}
