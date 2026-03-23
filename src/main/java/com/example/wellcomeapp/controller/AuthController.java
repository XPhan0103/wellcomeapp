package com.example.wellcomeapp.controller;

import com.example.wellcomeapp.dto.LoginRequest;
import com.example.wellcomeapp.dto.OtpRequest;
import com.example.wellcomeapp.dto.OtpVerifyRequest;
import com.example.wellcomeapp.dto.ResetPasswordRequest;
import com.example.wellcomeapp.model.OtpCode;
import com.example.wellcomeapp.model.User;
import com.example.wellcomeapp.repository.OtpCodeRepository;
import com.example.wellcomeapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // For mobile app access
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpCodeRepository otpCodeRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.example.wellcomeapp.security.JwtUtil jwtUtil;

    @Autowired
    private com.example.wellcomeapp.service.EmailService emailService;

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
            if (passwordEncoder.matches(pwd, user.getPassword()) && user.getIsActive()) {

                // Determine primary role
                String primaryRole = user.getRoles().stream()
                        .findFirst()
                        .map(r -> r.getName())
                        .orElse("ROLE_UNKNOWN");

                // Linked student id (for ROLE_PARENT or ROLE_STUDENT)
                Long studentId = null;
                if ("ROLE_STUDENT".equals(primaryRole)) {
                    studentId = user.getId();
                } else if ("ROLE_PARENT".equals(primaryRole)) {
                    studentId = user.getChildren().stream()
                            .findFirst()
                            .map(User::getId)
                            .orElse(null);
                }

                String token = jwtUtil.generateToken(phone, primaryRole);

                Map<String, Object> response = new HashMap<>();
                response.put("message",   "Đăng nhập thành công");
                response.put("userId",    user.getId());
                response.put("fullName",  user.getFullName());
                response.put("role",      primaryRole);
                response.put("studentId", studentId);
                response.put("token",     token);

                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tài khoản hoặc mật khẩu không chính xác");
    }


    @PostMapping("/forgot-password/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody OtpRequest request) {
        String phone = request.getPhoneNumber();
        Optional<User> userOpt = userRepository.findByPhoneNumber(phone);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Số điện thoại không tồn tại trong hệ thống");
        }

        // Sinh OTP ngẫu nhiên 6 số
        int randomNum = java.util.concurrent.ThreadLocalRandom.current().nextInt(100000, 1000000);
        String otp = String.valueOf(randomNum);

        OtpCode otpCode = new OtpCode();
        otpCode.setPhoneNumber(phone);
        otpCode.setOtpCode(otp);
        otpCode.setExpiresAt(LocalDateTime.now().plusMinutes(1)); // Hết hạn sau 1 phút
        otpCodeRepository.save(otpCode);

        System.out.println("========== MÃ OTP DÀNH CHO SĐT " + phone + " CHÍNH LÀ: " + otp + " ==========");

        // Gửi OTP qua Email
        if (userOpt.get().getEmail() != null && !userOpt.get().getEmail().isEmpty()) {
            try {
                emailService.sendOtpEmail(userOpt.get().getEmail(), otp);
                System.out.println("Đã gửi OTP qua mail tới: " + userOpt.get().getEmail());
            } catch (Exception e) {
                System.err.println("Lỗi khi gửi email: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Không thể gửi email OTP do lỗi cấu hình máy chủ.");
            }
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Mã OTP đã được gửi");
        response.put("otpCode", otp);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyRequest request) {
        Optional<OtpCode> otpOpt = otpCodeRepository
                .findTopByPhoneNumberAndIsUsedFalseOrderByExpiresAtDesc(request.getPhoneNumber());

        if (otpOpt.isPresent()) {
            OtpCode codeInfo = otpOpt.get();
            if (codeInfo.getExpiresAt().isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body("Mã OTP đã hết hạn");
            }
            if (codeInfo.getOtpCode().equals(request.getOtpCode())) {
                codeInfo.setIsUsed(true);
                otpCodeRepository.save(codeInfo);
                return ResponseEntity.ok(Collections.singletonMap("message", "Xác thực OTP thành công"));
            }
        }
        return ResponseEntity.badRequest().body("Mã OTP không chính xác");
    }

    @PostMapping("/forgot-password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        Optional<User> userOpt = userRepository.findByPhoneNumber(request.getPhoneNumber());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(request.getNewPassword())); // Cập nhật mật khẩu mới (đã mã hóa)
            userRepository.save(user);
            return ResponseEntity.ok(Collections.singletonMap("message", "Đổi mật khẩu thành công"));
        }
        return ResponseEntity.badRequest().body("Lỗi hệ thống khi đổi mật khẩu");
    }

    /**
     * POST /api/auth/change-password
     * Body: { "userId": 1, "oldPassword": "...", "newPassword": "..." }
     * Verifies old password against DB before saving new password.
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, Object> body) {
        Object userIdObj   = body.get("userId");
        String oldPassword = (String) body.get("oldPassword");
        String newPassword = (String) body.get("newPassword");

        if (userIdObj == null || oldPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body("Thiếu thông tin yêu cầu");
        }
        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest().body("Mật khẩu mới phải có ít nhất 6 ký tự");
        }

        long userId = ((Number) userIdObj).longValue();
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mật khẩu hiện tại không chính xác");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.ok(Collections.singletonMap("message", "Đổi mật khẩu thành công"));
    }
}

