package com.example.wellcomeapp.controller;

import com.example.wellcomeapp.model.Student;
import com.example.wellcomeapp.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*") // Cho phép các domain khác (như Web, App) gọi API này
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    // API: Lấy danh sách tất cả sinh viên (Gọi bằng phương thức GET)
    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // API: Thêm một sinh viên mới (Gọi bằng phương thức POST)
    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentRepository.save(student);
    }
}
