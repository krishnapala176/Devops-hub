package com.devopshub.service;

import com.devopshub.model.PasswordResetToken;
import com.devopshub.model.User;
import com.devopshub.repository.PasswordResetTokenRepository;
import com.devopshub.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // ✅ Step 1: Send OTP to email
    public void sendResetOtp(String email) {
        if (!userRepo.existsByEmail(email)) {
            throw new RuntimeException("No user registered with this email");
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        PasswordResetToken token = PasswordResetToken.builder()
                .email(email)
                .otp(otp)
                .createdAt(LocalDateTime.now())
                .isVerified(false)
                .build();

        tokenRepo.save(token);
        emailService.sendOtpEmail(email, otp);
    }

    // ✅ Step 2: Verify OTP
    public boolean verifyOtp(String email, String otp) {
        Optional<PasswordResetToken> tokenOpt = tokenRepo.findByEmailAndOtp(email, otp);
        if (tokenOpt.isPresent() && tokenOpt.get().getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(10))) {
            PasswordResetToken token = tokenOpt.get();
            token.setVerified(true);
            tokenRepo.save(token);
            return true;
        }
        return false;
    }

    // ✅ Step 3: Reset password
    public void resetPassword(String email, String newPassword) {
        List<PasswordResetToken> tokens = tokenRepo.findByEmail(email);

        // Step 1: Get the most recent verified token
        PasswordResetToken verifiedToken = tokens.stream()
                .filter(PasswordResetToken::isVerified)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())) // newest first
                .findFirst()
                .orElseThrow(() -> new RuntimeException("OTP not verified"));

        // Step 2: Find user and update password
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        // Step 3: Delete all tokens for cleanup
        tokenRepo.deleteAll(tokens);
    }
}
