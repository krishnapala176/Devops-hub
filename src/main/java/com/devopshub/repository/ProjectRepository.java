package com.devopshub.repository;

import com.devopshub.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProjectRepository extends MongoRepository<Project, String> {

    // ✅ All projects by author
    List<Project> findByAuthorEmail(String email);

    // ✅ Filter projects by tag
    List<Project> findByTagsContaining(String tag);

    // ✅ Dashboard: Count total projects by author
    long countByAuthorEmail(String email);
}
