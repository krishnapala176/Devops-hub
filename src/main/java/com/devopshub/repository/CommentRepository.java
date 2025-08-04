package com.devopshub.repository;

import com.devopshub.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByPostId(String postId);

    // ✅ Dashboard usage
    long countByAuthorEmail(String email);
}
