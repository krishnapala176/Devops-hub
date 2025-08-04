package com.devopshub.controller;

import com.devopshub.exception.UserNotFoundException;
import com.devopshub.model.User;
import com.devopshub.security.jwt.JwtService;
import com.devopshub.service.DashboardService;
import com.devopshub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final DashboardService dashboardService;
    private final JwtService jwtService;

    // ✅ General dashboard (student/mentor/admin logic)
    @GetMapping
    public ResponseEntity<?> getDashboard(Principal principal) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        String role = user.getRole().name();
        Map<String, Object> response = new HashMap<>();

        switch (role) {
            case "STUDENT":
                response.put("message", "Welcome to the student dashboard");
                response.put("tools", List.of("CI/CD Basics", "Docker Labs"));
                response.put("progress", "20% completed");
                break;
            case "MENTOR":
                response.put("message", "Welcome to the mentor dashboard");
                response.put("tasksToReview", List.of("Docker Assignment", "K8s Quiz"));
                break;
            case "ADMIN":
                response.put("message", "Welcome to the admin dashboard");
                response.put("totalUsers", userService.countAllUsers());
                response.put("pendingApprovals", List.of("Mentor1", "Mentor2"));
                break;
            default:
                response.put("message", "Unknown role");
        }

        return ResponseEntity.ok(response);
    }

    // ✅ Stats dashboard for logged-in user (likes, tutorials, comments, projects)
    @GetMapping("/user")
    public ResponseEntity<?> getUserStats(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeader(authHeader); // ✅ FIXED
        String email = jwtService.extractEmail(token);

        Map<String, Object> userStats = dashboardService.getUserDashboard(email);
        return ResponseEntity.ok(userStats);
    }

    // ✅ Admin-level analytics/stats (total users, top liked tutorials, etc.)
    @GetMapping("/admin")
    public ResponseEntity<?> getAdminStats(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeader(authHeader); // ✅ FIXED
        String email = jwtService.extractEmail(token);

        // Optional: verify the email is admin (you can add a role check here)
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }
}
