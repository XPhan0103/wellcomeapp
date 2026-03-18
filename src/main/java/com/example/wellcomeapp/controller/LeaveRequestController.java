package com.example.wellcomeapp.controller;

import com.example.wellcomeapp.model.LeaveRequest;
import com.example.wellcomeapp.model.Student;
import com.example.wellcomeapp.repository.LeaveRequestRepository;
import com.example.wellcomeapp.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parents")
public class LeaveRequestController {

    private final LeaveRequestRepository leaveRepo;
    private final StudentRepository studentRepo;

    public LeaveRequestController(LeaveRequestRepository leaveRepo, StudentRepository studentRepo) {
        this.leaveRepo = leaveRepo;
        this.studentRepo = studentRepo;
    }

    /** Danh sách đơn xin nghỉ của học sinh */
    @GetMapping("/students/{studentId}/leave-requests")
    public ResponseEntity<?> getLeaveRequests(@PathVariable Long studentId) {
        List<LeaveRequest> requests = leaveRepo.findByStudentIdOrderByCreatedAtDesc(studentId);
        List<Map<String, Object>> items = requests.stream().map(r -> Map.<String, Object>of(
                "id",         r.getId(),
                "reason",     r.getReason(),
                "leaveDate",  r.getLeaveDate().toString(),
                "status",     r.getStatus().name(),
                "parentName", r.getParentName() != null ? r.getParentName() : "",
                "createdAt",  r.getCreatedAt() != null ? r.getCreatedAt().toString() : ""
        )).collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }

    /** Gửi đơn xin nghỉ mới */
    @PostMapping("/students/{studentId}/leave-requests")
    public ResponseEntity<?> createLeaveRequest(
            @PathVariable Long studentId,
            @RequestBody Map<String, String> body) {

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        LeaveRequest request = new LeaveRequest();
        request.setStudent(student);
        request.setReason(body.getOrDefault("reason", ""));
        request.setParentName(body.getOrDefault("parentName", "Phụ huynh"));
        request.setLeaveDate(LocalDate.parse(body.get("leaveDate")));
        request.setStatus(LeaveRequest.Status.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        leaveRepo.save(request);
        return ResponseEntity.ok(Map.of("message", "Đơn xin nghỉ đã được gửi thành công!"));
    }
}
