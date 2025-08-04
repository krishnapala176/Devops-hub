package com.devopshub.controller;

import com.devopshub.model.Project;
import com.devopshub.security.jwt.JwtService;
import com.devopshub.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final JwtService jwtService;

    // ✅ Upload via GitHub URL + image URLs
    @PostMapping
    public ResponseEntity<Project> uploadProjectViaUrls(
            @RequestBody Project project,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = jwtService.extractTokenFromHeader(authHeader);
        String email = jwtService.extractEmail(token);

        project.setAuthorEmail(email);
        project.setCreatedAt(LocalDateTime.now());

        return ResponseEntity.ok(projectService.addProject(project));
    }

    // ✅ Upload with image files
    @PostMapping("/upload")
    public ResponseEntity<Project> uploadProjectWithImages(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("githubUrl") String githubUrl,
            @RequestParam(value = "tags", required = false) String[] tags,
            @RequestParam("images") List<MultipartFile> images,
            @RequestHeader("Authorization") String authHeader
    ) throws IOException {
        String token = jwtService.extractTokenFromHeader(authHeader);
        String email = jwtService.extractEmail(token);

        List<String> imagePaths = projectService.saveImages(images);

        Project project = Project.builder()
                .title(title)
                .description(description)
                .githubUrl(githubUrl)
                .tags(tags != null ? Arrays.asList(tags) : List.of())
                .imageUrls(imagePaths)
                .authorEmail(email)
                .createdAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(projectService.addProject(project));
    }

    // ✅ Get all
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAll());
    }

    // ✅ Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return projectService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Get by Author
    @GetMapping("/author/{email}")
    public ResponseEntity<List<Project>> getByAuthor(@PathVariable String email) {
        return ResponseEntity.ok(projectService.getByAuthor(email));
    }

    // ✅ Get by Tag
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<Project>> getByTag(@PathVariable String tag) {
        return ResponseEntity.ok(projectService.getByTag(tag));
    }

    // ✅ Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean deleted = projectService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
