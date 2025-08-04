package com.devopshub.controller;

import com.devopshub.dto.SendMessageRequest;
import com.devopshub.model.Message;
import com.devopshub.model.User;
import com.devopshub.repository.UserRepository;
import com.devopshub.security.jwt.JwtService;
import com.devopshub.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final MessageService messageService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(
            @RequestParam String receiverId,
            @Valid @RequestBody SendMessageRequest request,
            HttpServletRequest httpRequest
    ) {
        String token = jwtService.extractTokenFromHeader(httpRequest);
        String senderEmail = jwtService.extractEmail(token);
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Message message = messageService.sendMessage(sender.getId(), receiverId, request.getContent());
        return ResponseEntity.ok(message);
    }

    @GetMapping("/chat")
    public ResponseEntity<List<Message>> getChat(
            @RequestParam String senderId,
            @RequestParam String receiverId
    ) {
        return ResponseEntity.ok(messageService.getChat(senderId, receiverId));
    }

    @GetMapping("/inbox")
    public ResponseEntity<List<Message>> getInbox(HttpServletRequest request) {
        String token = jwtService.extractTokenFromHeader(request);
        String email = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(messageService.getInbox(user.getId()));
    }
}
