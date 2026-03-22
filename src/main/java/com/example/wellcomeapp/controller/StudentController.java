package com.example.wellcomeapp.controller;

import com.example.wellcomeapp.model.User;
import com.example.wellcomeapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*") // Cho phép các domain khác (như Web, App) gọi API này
public class StudentController {

    @Autowired
    private UserRepository userRepository;

    // API: Lấy danh sách tất cả sinh viên (Gọi bằng phương thức GET)
    @GetMapping
    public List<User> getAllStudents() {
        return userRepository.findAll();
    }

    // API: Thêm một sinh viên mới (Gọi bằng phương thức POST)
    @PostMapping
    public User createStudent(@RequestBody User student) {
        return userRepository.save(student);
    }
}
