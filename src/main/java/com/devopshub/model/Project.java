package com.devopshub.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "projects")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Project {

    @Id
    private String id;

    private String title;
    private String description;

    private String githubUrl;        // 🔗 GitHub repo link
    private String liveUrl;          // 🌐 Optional deployed site

    private List<String> tags;       // Tech stack: docker, kubernetes, etc.
    private List<String> imageUrls;  // Local upload paths or links

    private String authorEmail;
    private LocalDateTime createdAt;
}
