package com.example.wellcomeapp.controller;

import com.example.wellcomeapp.dto.DashboardResponse;
import com.example.wellcomeapp.model.Grade;
import com.example.wellcomeapp.model.User;
import com.example.wellcomeapp.repository.GradeRepository;
import com.example.wellcomeapp.repository.UserRepository;
import com.example.wellcomeapp.repository.AssignmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentDashboardController {

    private final UserRepository userRepo;
    private final GradeRepository gradeRepo;
    private final AssignmentRepository assignmentRepo;

    public StudentDashboardController(UserRepository userRepo, GradeRepository gradeRepo, AssignmentRepository assignmentRepo) {
        this.userRepo = userRepo;
        this.gradeRepo = gradeRepo;
        this.assignmentRepo = assignmentRepo;
    }

    @GetMapping("/{studentId}/dashboard")
    public ResponseEntity<?> getStudentDashboard(@PathVariable Long studentId) {
        User student = userRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        DashboardResponse response = new DashboardResponse();
        
        // 1. Student Info
        response.setStudent(new DashboardResponse.StudentInfo(
                student.getId(),
                student.getFullName(),
                student.getClassName(),
                student.getSchoolYear()
        ));

        // 2. Today's Schedules (Optional)
        response.setTodaySchedules(new ArrayList<>()); 

        // 3. Pending Assignments
        List<com.example.wellcomeapp.model.Assignment> assignments = assignmentRepo.findByClassNameWithSubject(student.getClassName());
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm - dd/MM");
        List<DashboardResponse.AssignmentDTO> assignmentDTOs = assignments.stream()
                .map(a -> new DashboardResponse.AssignmentDTO(
                        a.getTitle(),
                        a.getDueDate() != null ? a.getDueDate().format(formatter) : "",
                        a.getDescription(),
                        a.getSubject() != null ? a.getSubject().getName() : "",
                        a.getSubject() != null ? a.getSubject().getId() : null,
                        a.isCompleted()
                ))
                .collect(Collectors.toList());
        response.setPendingAssignments(assignmentDTOs);

        // 4. Grouped Grades
        List<Grade> allGrades = gradeRepo.findByStudentIdWithSubject(studentId);
        System.out.println("DEBUG (Student): Found " + allGrades.size() + " grades for studentId: " + studentId);

        Map<String, List<Grade>> bySubject = allGrades.stream()
                .collect(Collectors.groupingBy(g -> g.getSubject().getName()));

        List<DashboardResponse.SubjectSummaryDTO> subjectSummaries = bySubject.entrySet().stream().map(entry -> {
            String subjectName = entry.getKey();
            List<Grade> grades = entry.getValue();

            double weightedSum = grades.stream().mapToDouble(g -> g.getScore() * g.getWeight()).sum();
            double totalWeight = grades.stream().mapToInt(Grade::getWeight).sum();
            double gpa = totalWeight > 0 ? Math.round((weightedSum / totalWeight) * 10.0) / 10.0 : 0.0;

            List<DashboardResponse.GradeDetailDTO> details = grades.stream().map(g -> 
                new DashboardResponse.GradeDetailDTO(g.getType(), g.getScore(), g.getSemester(), g.getWeight())
            ).collect(Collectors.toList());

            return new DashboardResponse.SubjectSummaryDTO(subjectName, gpa, details);
        }).collect(Collectors.toList());

        double overallGpa = 0;
        if (!subjectSummaries.isEmpty()) {
            overallGpa = subjectSummaries.stream().mapToDouble(DashboardResponse.SubjectSummaryDTO::getGpa).average().orElse(0);
            overallGpa = Math.round(overallGpa * 10.0) / 10.0;
        }

        response.setSubjects(subjectSummaries);
        response.setOverallGpa(overallGpa);

        return ResponseEntity.ok(response);
    }
}
