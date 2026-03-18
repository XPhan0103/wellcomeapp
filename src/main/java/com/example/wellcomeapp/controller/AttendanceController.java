package com.example.wellcomeapp.controller;

import com.example.wellcomeapp.model.Attendance;
import com.example.wellcomeapp.repository.AttendanceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
public class AttendanceController {

    private final AttendanceRepository attendanceRepo;

    public AttendanceController(AttendanceRepository attendanceRepo) {
        this.attendanceRepo = attendanceRepo;
    }

    @GetMapping("/{studentId}/attendance")
    public ResponseEntity<?> getAttendance(@PathVariable Long studentId) {
        List<Attendance> records = attendanceRepo.findByStudentIdOrderByAttendanceDateDesc(studentId);

        long totalDays = records.size();
        long absentDays = records.stream().filter(a -> a.getStatus() == Attendance.Status.ABSENT).count();
        long lateDays   = records.stream().filter(a -> a.getStatus() == Attendance.Status.LATE).count();
        long presentDays = records.stream().filter(a -> a.getStatus() == Attendance.Status.PRESENT).count();

        List<Map<String, Object>> items = records.stream().map(a -> Map.<String, Object>of(
                "id",     a.getId(),
                "date",   a.getAttendanceDate().toString(),
                "status", a.getStatus().name(),
                "note",   a.getNote() != null ? a.getNote() : ""
        )).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "totalDays",   totalDays,
                "presentDays", presentDays,
                "absentDays",  absentDays,
                "lateDays",    lateDays,
                "records",     items
        ));
    }
}
