package com.devopshub.dto;

import com.devopshub.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Pattern(
            regexp = "^[a-zA-Z ]+$",
            message = "Name must contain only letters and spaces (no numbers or special characters)"
    )
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid and contain '@' and '.'")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$",
            message = "Password must include uppercase, lowercase, digit, and special character"
    )
    private String password;

    private Role role; // No validation needed for enum, optional
}
