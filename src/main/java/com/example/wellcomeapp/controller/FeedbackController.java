package com.example.wellcomeapp.controller;

import com.example.wellcomeapp.model.FeedbackMessage;
import com.example.wellcomeapp.model.Student;
import com.example.wellcomeapp.repository.FeedbackMessageRepository;
import com.example.wellcomeapp.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parents")
@CrossOrigin(origins = "*")
public class FeedbackController {

    private final FeedbackMessageRepository feedbackRepo;
    private final StudentRepository studentRepo;

    public FeedbackController(FeedbackMessageRepository feedbackRepo, StudentRepository studentRepo) {
        this.feedbackRepo = feedbackRepo;
        this.studentRepo = studentRepo;
    }

    /** GET /api/parents/students/{studentId}/feedback */
    @GetMapping("/students/{studentId}/feedback")
    public ResponseEntity<?> getFeedback(@PathVariable Long studentId) {
        List<FeedbackMessage> messages = feedbackRepo.findByStudentIdOrderByCreatedAtDesc(studentId);

        List<Map<String, Object>> result = messages.stream().map(m -> {
            Map<String, Object> map = new java.util.LinkedHashMap<>();
            map.put("id",          m.getId());
            map.put("subject",     m.getSubject());
            map.put("message",     m.getMessage());
            map.put("senderName",  m.getSenderName());
            map.put("status",      m.getStatus());
            map.put("reply",       m.getReply() != null ? m.getReply() : "");
            map.put("createdAt",   m.getCreatedAt() != null ? m.getCreatedAt().toString() : "");
            map.put("repliedAt",   m.getRepliedAt() != null ? m.getRepliedAt().toString() : "");
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    /** POST /api/parents/students/{studentId}/feedback */
    @PostMapping("/students/{studentId}/feedback")
    public ResponseEntity<?> sendFeedback(
            @PathVariable Long studentId,
            @RequestBody Map<String, String> body) {

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        FeedbackMessage msg = new FeedbackMessage();
        msg.setStudent(student);
        msg.setSenderName(body.getOrDefault("senderName", "Phụ huynh"));
        msg.setSubject(body.getOrDefault("subject", "Liên hệ"));
        msg.setMessage(body.getOrDefault("message", ""));
        msg.setStatus("PENDING");
        msg.setCreatedAt(LocalDateTime.now());

        feedbackRepo.save(msg);
        return ResponseEntity.ok(Map.of("message", "Phản hồi đã được gửi thành công!"));
    }
}
