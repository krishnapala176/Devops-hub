package com.devopshub.repository;

import com.devopshub.model.Tutorial;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface TutorialRepository extends MongoRepository<Tutorial, String> {

    // ✅ Filter by author
    List<Tutorial> findByAuthorEmail(String email);

    // ✅ Filter by tag
    List<Tutorial> findByTagsContaining(String tag);

    // ✅ Dashboard: Count total tutorials by author
    long countByAuthorEmail(String email);

    // ✅ Dashboard: Get all tutorials by author to sum likes in service
    @Query("{ 'authorEmail': ?0 }")
    List<Tutorial> findAllByAuthorEmail(String email);

    // ✅ Dashboard: Top 3 most liked tutorials
    List<Tutorial> findTop3ByOrderByLikedByDesc();
}
