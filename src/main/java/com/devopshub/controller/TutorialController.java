package com.devopshub.controller;

import com.devopshub.model.Tutorial;
import com.devopshub.security.jwt.JwtService;
import com.devopshub.service.TutorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/tutorials")
@RequiredArgsConstructor
public class TutorialController {

    private final TutorialService tutorialService;
    private final JwtService jwtService;

    // ✅ Upload via video URL
    @PostMapping
    public ResponseEntity<Tutorial> uploadTutorial(
            @RequestBody Tutorial tutorial,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = jwtService.extractTokenFromHeader(authHeader);
        String email = jwtService.extractEmail(token);

        tutorial.setAuthorEmail(email);
        tutorial.setCreatedAt(LocalDateTime.now());

        return ResponseEntity.ok(tutorialService.addTutorial(tutorial));
    }

    // ✅ Upload video file (multipart)
    @PostMapping("/upload")
    public ResponseEntity<Tutorial> uploadVideoTutorial(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "tags", required = false) String[] tags,
            @RequestParam("video") MultipartFile videoFile,
            @RequestHeader("Authorization") String authHeader
    ) throws IOException {

        String token = jwtService.extractTokenFromHeader(authHeader);
        String email = jwtService.extractEmail(token);

        Tutorial tutorial = tutorialService.saveVideoFile(videoFile);

        tutorial.setTitle(title);
        tutorial.setDescription(description);
        tutorial.setAuthorEmail(email);
        tutorial.setTags(tags != null ? Arrays.asList(tags) : List.of());
        tutorial.setCreatedAt(LocalDateTime.now());

        return ResponseEntity.ok(tutorialService.addTutorial(tutorial));
    }

    // ✅ Like a tutorial
    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeTutorial(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = jwtService.extractTokenFromHeader(authHeader);
        String email = jwtService.extractEmail(token);
        boolean liked = tutorialService.likeTutorial(id, email);
        return liked ? ResponseEntity.ok("Liked successfully") : ResponseEntity.notFound().build();
    }

    // ✅ Unlike a tutorial
    @PostMapping("/{id}/unlike")
    public ResponseEntity<?> unlikeTutorial(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = jwtService.extractTokenFromHeader(authHeader);
        String email = jwtService.extractEmail(token);
        boolean unliked = tutorialService.unlikeTutorial(id, email);
        return unliked ? ResponseEntity.ok("Unliked successfully") : ResponseEntity.notFound().build();
    }

    // ✅ Get all tutorials
    @GetMapping
    public ResponseEntity<List<Tutorial>> getAllTutorials() {
        return ResponseEntity.ok(tutorialService.getAllTutorials());
    }

    // ✅ Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTutorialById(@PathVariable String id) {
        return tutorialService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Get by author
    @GetMapping("/author/{email}")
    public ResponseEntity<List<Tutorial>> getByAuthor(@PathVariable String email) {
        return ResponseEntity.ok(tutorialService.getByAuthor(email));
    }

    // ✅ Get by tag
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<Tutorial>> getByTag(@PathVariable String tag) {
        return ResponseEntity.ok(tutorialService.getByTag(tag));
    }

    // ✅ Delete tutorial
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTutorial(@PathVariable String id) {
        boolean deleted = tutorialService.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
