package com.devopshub.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // ✅ For email verification
    public void sendVerificationEmail(String toEmail, String token) {
        String subject = "Verify your email for DevOps Hub";
        String verificationLink = "http://localhost:8080/api/v1/auth/verify?token=" + token;

        String body = "<h2>Welcome to DevOps Hub!</h2>"
                + "<p>Click the link below to verify your email:</p>"
                + "<a href=\"" + verificationLink + "\">Verify Email</a>";

        sendHtmlEmail(toEmail, subject, body);
    }

    // ✅ For OTP password reset
    public void sendOtpEmail(String toEmail, String otp) {
        String subject = "Your OTP for Password Reset - DevOps Hub";
        String htmlBody = "<p>Your OTP is:</p>"
                + "<h2>" + otp + "</h2>"
                + "<p>This OTP is valid for 10 minutes.</p>";

        sendHtmlEmail(toEmail, subject, htmlBody);
    }

    // ✅ Common internal method
    private void sendHtmlEmail(String toEmail, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}
