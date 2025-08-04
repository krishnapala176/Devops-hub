package com.devopshub.service;

import com.devopshub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final TutorialRepository tutorialRepository;
    private final CommentRepository commentRepository;
    private final ProjectRepository projectRepository;

    // 👤 User-specific dashboard
    public Map<String, Object> getUserDashboard(String email) {
        Map<String, Object> data = new HashMap<>();
        data.put("totalComments", commentRepository.countByAuthorEmail(email));
        data.put("totalTutorials", tutorialRepository.countByAuthorEmail(email));
        data.put("totalProjects", projectRepository.countByAuthorEmail(email));

        int totalLikes = tutorialRepository.findAllByAuthorEmail(email)
                .stream()
                .mapToInt(t -> t.getLikedBy() != null ? t.getLikedBy().size() : 0)
                .sum();
        data.put("totalLikesReceived", totalLikes);

        return data;
    }

    // 🛠 Admin-level stats
    public Map<String, Object> getAdminDashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", userRepository.count());
        data.put("totalTutorials", tutorialRepository.count());
        data.put("totalProjects", projectRepository.count());
        data.put("totalComments", commentRepository.count());

        data.put("topLikedTutorials", tutorialRepository.findTop3ByOrderByLikedByDesc());

        return data;
    }
}
