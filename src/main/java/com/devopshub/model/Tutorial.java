package com.devopshub.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Document(collection = "tutorials")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tutorial {

    @Id
    private String id;

    private String title;
    private String description;

    private String videoUrl;     // ✅ For YouTube/Vimeo links
    private String videoPath;    // ✅ For uploaded videos (stored filename or path)

    private List<String> tags;

    private String authorEmail;
    private LocalDateTime createdAt;

    private Set<String> likedBy;  // ✅ Emails of users who liked the tutorial
}
