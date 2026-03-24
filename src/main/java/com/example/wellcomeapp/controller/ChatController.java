package com.example.wellcomeapp.controller;

import com.example.wellcomeapp.model.Message;
import com.example.wellcomeapp.model.User;
import com.example.wellcomeapp.repository.MessageRepository;
import com.example.wellcomeapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    // Lấy danh sách liên hệ (Nếu là Parent -> list Teachers. Nếu là Teacher -> list Parents)
    @GetMapping("/contacts/{userId}")
    public ResponseEntity<?> getContacts(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("Người dùng không tồn tại");

        User user = userOpt.get();
        boolean isTeacher = user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_TEACHER"));

        List<User> contacts;
        if (isTeacher) {
            contacts = userRepository.findByRoleName("ROLE_PARENT");
        } else {
            contacts = userRepository.findByRoleName("ROLE_TEACHER");
        }

        List<Map<String, Object>> response = new ArrayList<>();
        for (User c : contacts) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("fullName", c.getFullName());
            map.put("phoneNumber", c.getPhoneNumber());
            map.put("role", isTeacher ? "Phụ huynh" : "Giảng viên");
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    // Lấy lịch sử tin nhắn
    @GetMapping("/history/{user1}/{user2}")
    public ResponseEntity<?> getHistory(@PathVariable Long user1, @PathVariable Long user2) {
        List<Message> messages = messageRepository.findChatHistory(user1, user2);
        
        List<Map<String, Object>> response = new ArrayList<>();
        for (Message m : messages) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", m.getId());
            map.put("senderId", m.getSender().getId());
            map.put("receiverId", m.getReceiver().getId());
            map.put("content", m.getContent());
            map.put("timestamp", m.getTimestamp().toString());
            map.put("isRead", m.getIsRead());
            response.add(map);
            
            // Nếu user1 (người request) là người nhận thì chuyển isRead = true
            if (m.getReceiver().getId().equals(user1) && !m.getIsRead()) {
                m.setIsRead(true);
                messageRepository.save(m);
            }
        }
        return ResponseEntity.ok(response);
    }

    // Gửi tin nhắn
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, Object> body) {
        Long senderId = Long.valueOf(body.get("senderId").toString());
        Long receiverId = Long.valueOf(body.get("receiverId").toString());
        String content = body.get("content").toString();

        Optional<User> sender = userRepository.findById(senderId);
        Optional<User> receiver = userRepository.findById(receiverId);

        if (sender.isEmpty() || receiver.isEmpty()) {
            return ResponseEntity.badRequest().body("Người gửi hoặc nhận không tồn tại");
        }

        Message message = new Message();
        message.setSender(sender.get());
        message.setReceiver(receiver.get());
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setIsRead(false);

        Message saved = messageRepository.save(message);

        Map<String, Object> response = new HashMap<>();
        response.put("id", saved.getId());
        response.put("senderId", saved.getSender().getId());
        response.put("receiverId", saved.getReceiver().getId());
        response.put("content", saved.getContent());
        response.put("timestamp", saved.getTimestamp().toString());
        response.put("isRead", saved.getIsRead());

        return ResponseEntity.ok(response);
    }
}
