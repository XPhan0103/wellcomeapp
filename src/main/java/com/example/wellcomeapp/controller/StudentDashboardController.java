package com.example.wellcomeapp.controller;

import com.example.wellcomeapp.model.Grade;
import com.example.wellcomeapp.model.Student;
import com.example.wellcomeapp.repository.GradeRepository;
import com.example.wellcomeapp.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
public class StudentDashboardController {

    private final StudentRepository studentRepo;
    private final GradeRepository gradeRepo;

    public StudentDashboardController(StudentRepository studentRepo, GradeRepository gradeRepo) {
        this.studentRepo = studentRepo;
        this.gradeRepo = gradeRepo;
    }

    /**
     * GET /api/students/{studentId}/dashboard
     * Returns full student info + grade summary grouped by subject & semester.
     */
    @GetMapping("/{studentId}/dashboard")
    public ResponseEntity<?> getStudentDashboard(@PathVariable Long studentId) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Grade> allGrades = gradeRepo.findByStudentIdOrderByCreatedAtDesc(studentId);

        // Group grades by subject
        Map<String, List<Grade>> bySubject = allGrades.stream()
                .collect(Collectors.groupingBy(g -> g.getSubject().getName()));

        // Build subject summary with GPA
        List<Map<String, Object>> subjectSummaries = bySubject.entrySet().stream().map(entry -> {
            String subjectName = entry.getKey();
            List<Grade> grades = entry.getValue();

            // Weighted average
            double weightedSum = grades.stream().mapToDouble(g -> g.getScore() * g.getWeight()).sum();
            double totalWeight = grades.stream().mapToInt(Grade::getWeight).sum();
            double gpa = totalWeight > 0 ? Math.round((weightedSum / totalWeight) * 10.0) / 10.0 : 0.0;

            // Grade details list
            List<Map<String, Object>> details = grades.stream().map(g -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("type",     g.getType());
                m.put("score",    g.getScore());
                m.put("semester", g.getSemester());
                m.put("weight",   g.getWeight());
                return m;
            }).collect(Collectors.toList());

            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("subject", subjectName);
            summary.put("gpa",     gpa);
            summary.put("grades",  details);
            return summary;
        }).collect(Collectors.toList());

        // Overall GPA across all subjects
        double overallGpa = 0;
        if (!subjectSummaries.isEmpty()) {
            overallGpa = subjectSummaries.stream()
                    .mapToDouble(s -> (double) s.get("gpa")).average().orElse(0);
            overallGpa = Math.round(overallGpa * 10.0) / 10.0;
        }

        // Student info
        Map<String, Object> studentInfo = new LinkedHashMap<>();
        studentInfo.put("id",           student.getId());
        studentInfo.put("fullName",      student.getFullName());
        studentInfo.put("studentCode",   student.getStudentCode());
        studentInfo.put("className",     student.getClassName());
        studentInfo.put("schoolYear",    student.getSchoolYear());
        studentInfo.put("dateOfBirth",   student.getDateOfBirth() != null ? student.getDateOfBirth().toString() : "");
        studentInfo.put("address",       student.getAddress() != null ? student.getAddress() : "");

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("student",    studentInfo);
        response.put("overallGpa", overallGpa);
        response.put("subjects",   subjectSummaries);

        return ResponseEntity.ok(response);
    }
}
