package rw.food_pick.foodies_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import rw.food_pick.foodies_backend.entity.FoodEntity;

public interface FoodRepository extends MongoRepository<FoodEntity, String> {
}
