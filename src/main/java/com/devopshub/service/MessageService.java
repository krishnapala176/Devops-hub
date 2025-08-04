package com.devopshub.service;

import com.devopshub.model.Message;
import com.devopshub.model.MessageStatus;
import com.devopshub.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepo;

    public Message sendMessage(String senderId, String receiverId, String content) {
        Message message = Message.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .timestamp(LocalDateTime.now())
                .status(MessageStatus.SENT)
                .build();
        return messageRepo.save(message);
    }

    public List<Message> getChat(String senderId, String receiverId) {
        return messageRepo.findBySenderIdAndReceiverId(senderId, receiverId);
    }

    public List<Message> getInbox(String userId) {
        return messageRepo.findByReceiverId(userId);
    }
}
