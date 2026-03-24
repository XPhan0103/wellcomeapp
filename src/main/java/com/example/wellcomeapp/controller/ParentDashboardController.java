package com.example.wellcomeapp.controller;

import com.example.wellcomeapp.dto.DashboardResponse;
import com.example.wellcomeapp.model.Assignment;
import com.example.wellcomeapp.model.Schedule;
import com.example.wellcomeapp.model.User;
import com.example.wellcomeapp.repository.AssignmentRepository;
import com.example.wellcomeapp.repository.ScheduleRepository;
import com.example.wellcomeapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parents")
@CrossOrigin(origins = "*")
public class ParentDashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private com.example.wellcomeapp.repository.GradeRepository gradeRepository;

    @GetMapping("/students/{studentId}/dashboard")
    public ResponseEntity<?> getStudentDashboard(@PathVariable Long studentId) {
        Optional<User> studentOpt = userRepository.findById(studentId);
        if (studentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User student = studentOpt.get();
        DashboardResponse response = new DashboardResponse();
        
        // 1. Student Info
        response.setStudent(new DashboardResponse.StudentInfo(
                student.getId(),
                student.getFullName(),
                student.getClassName(),
                student.getSchoolYear()
        ));

        // 2. Today's Schedules
        int currentDow = java.time.LocalDate.now().getDayOfWeek().getValue() + 1; // Mon=2, ... Sun=8
        List<Schedule> schedules = scheduleRepository.findByClassNameAndDayOfWeekOrderByStartTimeAsc(student.getClassName(), currentDow);
        List<DashboardResponse.ScheduleDTO> scheduleDTOs = schedules.stream()
                .map(s -> new DashboardResponse.ScheduleDTO(
                        s.getStartTime() != null ? s.getStartTime().toString() : "",
                        s.getSubject().getName(),
                        s.getRoom()
                ))
                .collect(Collectors.toList());
        response.setTodaySchedules(scheduleDTOs);

        // 3. Pending Assignments
        List<Assignment> assignments = assignmentRepository.findByClassNameWithSubject(student.getClassName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm - dd/MM");
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
        List<com.example.wellcomeapp.model.Grade> allGrades = gradeRepository.findByStudentIdWithSubject(studentId);
        System.out.println("DEBUG: Found " + allGrades.size() + " grades for studentId: " + studentId);
        
        // Group grades by subject
        java.util.Map<String, List<com.example.wellcomeapp.model.Grade>> bySubject = allGrades.stream()
                .collect(Collectors.groupingBy(g -> g.getSubject().getName()));

        // Build subject summary
        List<DashboardResponse.SubjectSummaryDTO> subjectSummaries = bySubject.entrySet().stream().map(entry -> {
            String subjectName = entry.getKey();
            List<com.example.wellcomeapp.model.Grade> grades = entry.getValue();

            double weightedSum = grades.stream().mapToDouble(g -> g.getScore() * g.getWeight()).sum();
            double totalWeight = grades.stream().mapToInt(com.example.wellcomeapp.model.Grade::getWeight).sum();
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
