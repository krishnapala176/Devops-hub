package com.devopshub.controller;

import com.devopshub.model.Rating;
import com.devopshub.model.User;
import com.devopshub.repository.UserRepository;
import com.devopshub.security.jwt.JwtService;
import com.devopshub.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;
    private final JwtService jwtService; // ✅ Correct service
    private final UserRepository userRepository;

    @PostMapping("/{projectId}")
    public ResponseEntity<Rating> rateProject(
            @PathVariable String projectId,
            @RequestParam int rating,
            HttpServletRequest request
    ) {
        // ✅ Extract token properly using JwtService
        String token = jwtService.extractTokenFromHeader(request);
        String email = jwtService.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Rating result = ratingService.addOrUpdateRating(user.getId(), projectId, rating);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{projectId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable String projectId) {
        double avg = ratingService.getAverageRating(projectId);
        return ResponseEntity.ok(avg);
    }

    @GetMapping("/{projectId}/all")
    public ResponseEntity<List<Rating>> getAllRatings(@PathVariable String projectId) {
        return ResponseEntity.ok(ratingService.getAllRatingsForProject(projectId));
    }
}
