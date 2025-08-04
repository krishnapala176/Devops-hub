package com.devopshub.repository;

import com.devopshub.model.Rating;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends MongoRepository<Rating, String> {

    Optional<Rating> findByUserIdAndProjectId(String userId, String projectId);

    List<Rating> findByProjectId(String projectId);
}
