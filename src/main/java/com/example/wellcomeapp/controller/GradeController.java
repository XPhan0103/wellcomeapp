package com.example.wellcomeapp.controller;

import com.example.wellcomeapp.model.Grade;
import com.example.wellcomeapp.repository.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students/{studentId}/grades")
@CrossOrigin(origins = "*")
public class GradeController {

    @Autowired
    private GradeRepository gradeRepository;

    @GetMapping
    public ResponseEntity<List<Grade>> getStudentGrades(@PathVariable Long studentId) {
        List<Grade> grades = gradeRepository.findByStudentIdWithSubject(studentId);
        return ResponseEntity.ok(grades);
    }
}
