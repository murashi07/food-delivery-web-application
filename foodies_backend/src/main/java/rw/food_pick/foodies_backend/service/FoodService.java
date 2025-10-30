package rw.food_pick.foodies_backend.service;

import org.springframework.web.multipart.MultipartFile;
import rw.food_pick.foodies_backend.io.FoodRequest;
import rw.food_pick.foodies_backend.io.FoodResponse;

import java.util.List;

public interface FoodService {
    String uploadFile(MultipartFile file);

    FoodResponse addFood(FoodRequest request, MultipartFile file);

    List<FoodResponse> readFoods();
}
