package com.devopshub.controller;

import com.devopshub.model.Comment;
import com.devopshub.service.CommentService;
import com.devopshub.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<Comment> addComment(
            @RequestBody Comment comment,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);

        comment.setAuthorEmail(email);
        comment.setCreatedAt(LocalDateTime.now());

        return ResponseEntity.ok(commentService.addComment(comment));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable String postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        String role = jwtService.extractRole(token);

        boolean deleted = commentService.deleteCommentIfAuthorized(id, email, role);

        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(403).build();
    }
}
