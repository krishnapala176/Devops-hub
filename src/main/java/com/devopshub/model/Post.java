package com.devopshub.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "posts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {

    @Id
    private String id;

    private String title;
    private String content;
    private String authorEmail;

    private LocalDateTime createdAt;  // ✅ Make sure this is set in service

    private List<String> tags;
}
