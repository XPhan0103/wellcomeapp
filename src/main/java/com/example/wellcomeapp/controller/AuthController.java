package com.example.wellcomeapp.controller;

import com.example.wellcomeapp.dto.LoginRequest;
import com.example.wellcomeapp.model.User;
import com.example.wellcomeapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // For mobile app access
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String phone = loginRequest.getPhoneNumber();
        String pwd = loginRequest.getPassword();

        if (phone == null || pwd == null) {
            return ResponseEntity.badRequest().body("Số điện thoại và mật khẩu không được để trống");
        }

        Optional<User> userOptional = userRepository.findByPhoneNumber(phone);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // In a real app, use BCryptPasswordEncoder to verify hashed passwords
            // Since this is a simple prototype, we do plain text match
            if (user.getPassword().equals(pwd) && user.getIsActive()) {

                // Return success and user info (without password)
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Đăng nhập thành công");
                response.put("userId", user.getId());
                response.put("fullName", user.getFullName());
                response.put("roles", user.getRoles());

                // Usually returns a JWT token here.
                response.put("token", "fake-jwt-token-replace-later");

                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tài khoản hoặc mật khẩu không chính xác");
    }
}
