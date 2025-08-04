package com.devopshub.service;

import com.devopshub.model.Rating;
import com.devopshub.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.OptionalDouble;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;

    public Rating addOrUpdateRating(String userId, String projectId, int ratingValue) {
        if (ratingValue < 1 || ratingValue > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Rating rating = ratingRepository
                .findByUserIdAndProjectId(userId, projectId)
                .map(existing -> {
                    existing.setRating(ratingValue);
                    return existing;
                })
                .orElse(
                        Rating.builder()
                                .userId(userId)
                                .projectId(projectId)
                                .rating(ratingValue)
                                .build()
                );

        return ratingRepository.save(rating);
    }

    public double getAverageRating(String projectId) {
        List<Rating> ratings = ratingRepository.findByProjectId(projectId);

        OptionalDouble average = ratings
                .stream()
                .mapToInt(Rating::getRating)
                .average();

        return average.orElse(0.0);
    }

    public List<Rating> getAllRatingsForProject(String projectId) {
        return ratingRepository.findByProjectId(projectId);
    }
}
