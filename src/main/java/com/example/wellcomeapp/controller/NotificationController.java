package com.example.wellcomeapp.controller;

import com.example.wellcomeapp.model.Notification;
import com.example.wellcomeapp.repository.NotificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationRepository notifRepo;

    public NotificationController(NotificationRepository notifRepo) {
        this.notifRepo = notifRepo;
    }

    // GET /api/notifications?category=HỌC_TẬP&unreadOnly=false
    @GetMapping
    public ResponseEntity<?> getNotifications(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {

        List<Notification> list;
        if (unreadOnly) {
            list = notifRepo.findByIsReadFalseOrderByCreatedAtDesc();
        } else if (category != null && !category.isBlank()) {
            list = notifRepo.findByCategoryOrderByCreatedAtDesc(category);
        } else {
            list = notifRepo.findAllByOrderByCreatedAtDesc();
        }

        long unreadCount = notifRepo.countByIsReadFalse();

        List<Map<String, Object>> dtos = list.stream().map(n -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id",        n.getId());
            m.put("title",     n.getTitle() != null     ? n.getTitle()    : "");
            m.put("content",   n.getContent() != null   ? n.getContent()  : "");
            m.put("category",  n.getCategory() != null  ? n.getCategory() : "SỰ_KIỆN");
            m.put("isRead",    Boolean.TRUE.equals(n.getIsRead()));
            m.put("createdAt", n.getCreatedAt() != null ? n.getCreatedAt().toInstant().toString() : "");
            return m;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "unreadCount",   unreadCount,
                "notifications", dtos
        ));
    }

    // PATCH /api/notifications/{id}/read
    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        return notifRepo.findById(id).map(n -> {
            n.setIsRead(true);
            notifRepo.save(n);
            return ResponseEntity.ok(Map.of("message", "Đã đánh dấu đã đọc"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // PATCH /api/notifications/read-all
    @PatchMapping("/read-all")
    public ResponseEntity<?> markAllAsRead() {
        List<Notification> unread = notifRepo.findByIsReadFalseOrderByCreatedAtDesc();
        unread.forEach(n -> n.setIsRead(true));
        notifRepo.saveAll(unread);
        return ResponseEntity.ok(Map.of("message", "Đã đọc tất cả"));
    }
}

