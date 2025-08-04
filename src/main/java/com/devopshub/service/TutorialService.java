package com.devopshub.service;

import com.devopshub.model.Tutorial;
import com.devopshub.repository.TutorialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TutorialService {

    private final TutorialRepository tutorialRepository;

    private static final String VIDEO_UPLOAD_DIR = "uploads/videos/";

    // ✅ Create a new tutorial
    public Tutorial addTutorial(Tutorial tutorial) {
        if (tutorial.getCreatedAt() == null) {
            tutorial.setCreatedAt(LocalDateTime.now());
        }
        return tutorialRepository.save(tutorial);
    }

    // ✅ Save uploaded video file to disk and return Tutorial object with videoUrl set
    public Tutorial saveVideoFile(MultipartFile videoFile) throws IOException {
        // Ensure directory exists
        File uploadDir = new File(VIDEO_UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Generate a unique filename
        String originalFilename = videoFile.getOriginalFilename();
        String fileExtension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFilename = System.currentTimeMillis() + fileExtension;

        // Save file
        Path filePath = Paths.get(VIDEO_UPLOAD_DIR + uniqueFilename);
        Files.write(filePath, videoFile.getBytes(), StandardOpenOption.CREATE);

        // Create a tutorial object with video path set
        return Tutorial.builder()
                .videoUrl(filePath.toString()) // You can map this to a public URL later if needed
                .build();
    }

    // ✅ Get all tutorials
    public List<Tutorial> getAllTutorials() {
        return tutorialRepository.findAll();
    }

    // ✅ Get by ID
    public Optional<Tutorial> getById(String id) {
        return tutorialRepository.findById(id);
    }

    // ✅ Get by author
    public List<Tutorial> getByAuthor(String email) {
        return tutorialRepository.findByAuthorEmail(email);
    }

    // ✅ Get by tag
    public List<Tutorial> getByTag(String tag) {
        return tutorialRepository.findByTagsContaining(tag);
    }

    // ✅ Delete tutorial
    public boolean deleteById(String id) {
        if (tutorialRepository.existsById(id)) {
            tutorialRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // ✅ Like tutorial
    public boolean likeTutorial(String id, String userEmail) {
        Optional<Tutorial> optional = tutorialRepository.findById(id);
        if (optional.isPresent()) {
            Tutorial tutorial = optional.get();
            if (tutorial.getLikedBy() == null) {
                tutorial.setLikedBy(new HashSet<>());
            }
            tutorial.getLikedBy().add(userEmail);
            tutorialRepository.save(tutorial);
            return true;
        }
        return false;
    }

    // ✅ Unlike tutorial
    public boolean unlikeTutorial(String id, String userEmail) {
        Optional<Tutorial> optional = tutorialRepository.findById(id);
        if (optional.isPresent()) {
            Tutorial tutorial = optional.get();
            if (tutorial.getLikedBy() != null) {
                tutorial.getLikedBy().remove(userEmail);
                tutorialRepository.save(tutorial);
                return true;
            }
        }
        return false;
    }
}
