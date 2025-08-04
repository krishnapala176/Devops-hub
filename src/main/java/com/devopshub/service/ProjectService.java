package com.devopshub.service;

import com.devopshub.model.Project;
import com.devopshub.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    private static final String IMAGE_UPLOAD_DIR = "uploads/projects/";

    // ✅ Save metadata (no file upload)
    public Project addProject(Project project) {
        project.setCreatedAt(LocalDateTime.now());
        return projectRepository.save(project);
    }

    // ✅ Save uploaded image files and return local paths
    public List<String> saveImages(List<MultipartFile> images) throws IOException {
        List<String> imagePaths = new ArrayList<>();

        // Ensure folder exists
        File dir = new File(IMAGE_UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (MultipartFile image : images) {
            String originalName = image.getOriginalFilename();
            String extension = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : "";

            String filename = System.currentTimeMillis() + "-" + image.getOriginalFilename();
            Path fullPath = Paths.get(IMAGE_UPLOAD_DIR + filename);
            Files.write(fullPath, image.getBytes(), StandardOpenOption.CREATE);

            imagePaths.add(fullPath.toString());
        }

        return imagePaths;
    }

    // ✅ Get all
    public List<Project> getAll() {
        return projectRepository.findAll();
    }

    // ✅ Get by ID
    public Optional<Project> getById(String id) {
        return projectRepository.findById(id);
    }

    // ✅ Get by Author
    public List<Project> getByAuthor(String email) {
        return projectRepository.findByAuthorEmail(email);
    }

    // ✅ Get by Tag
    public List<Project> getByTag(String tag) {
        return projectRepository.findByTagsContaining(tag);
    }

    // ✅ Delete
    public boolean delete(String id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
