package com.devopshub.service;

import com.devopshub.model.Post;
import com.devopshub.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(String id) {
        return postRepository.findById(id);
    }

    public Post createPost(Post post) {
        // ✅ Set createdAt if not already set
        if (post.getCreatedAt() == null) {
            post.setCreatedAt(LocalDateTime.now());
        }

        // ✅ Ensure tags is not null
        if (post.getTags() == null) {
            post.setTags(new ArrayList<>());
        }

        return postRepository.save(post);
    }

    public Optional<Post> updatePost(String id, Post updatedPost) {
        return postRepository.findById(id).map(existing -> {
            existing.setTitle(updatedPost.getTitle());
            existing.setContent(updatedPost.getContent());
            existing.setTags(updatedPost.getTags());
            return postRepository.save(existing);
        });
    }

    public boolean deletePost(String id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
