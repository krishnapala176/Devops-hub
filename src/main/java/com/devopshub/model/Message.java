package com.devopshub.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    private String id;

    private String senderId;
    private String receiverId;
    private String content;
    private LocalDateTime timestamp;

    private MessageStatus status; // Enum: SENT, READ, etc.
}
