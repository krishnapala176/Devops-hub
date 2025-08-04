package com.devopshub.repository;

import com.devopshub.model.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByEmailAndOtp(String email, String otp);
    List<PasswordResetToken> findByEmail(String email);
}
