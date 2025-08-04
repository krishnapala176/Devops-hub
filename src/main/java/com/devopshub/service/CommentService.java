package com.devopshub.service;

import com.devopshub.model.Comment;
import com.devopshub.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment addComment(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByPostId(String postId) {
        return commentRepository.findByPostId(postId);
    }

    // ✅ Secure delete: only author or admin can delete
    public boolean deleteCommentIfAuthorized(String commentId, String userEmail, String role) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);

        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            boolean isAuthor = comment.getAuthorEmail().equals(userEmail);
            boolean isAdmin = "ROLE_ADMIN".equals(role);

            if (isAuthor || isAdmin) {
                commentRepository.deleteById(commentId);
                return true;
            }
        }

        return false;
    }
}
