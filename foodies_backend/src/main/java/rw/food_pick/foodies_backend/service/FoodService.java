package rw.food_pick.foodies_backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FoodService {
    String uploadFile(MultipartFile file);
}
