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
            // Trong thực tế, dùng BCryptPasswordEncoder
            if (user.getPassword().equals(pwd) && user.getIsActive()) {

                // Return success and user info
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Đăng nhập thành công");
                response.put("userId", user.getId());
                response.put("fullName", user.getFullName());
                response.put("roles", user.getRoles());
                response.put("token", "fake-jwt-token-replace-later");

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
            user.setPassword(request.getNewPassword()); // Cập nhật mật khẩu mới
            userRepository.save(user);
            return ResponseEntity.ok(Collections.singletonMap("message", "Đổi mật khẩu thành công"));
        }
        return ResponseEntity.badRequest().body("Lỗi hệ thống khi đổi mật khẩu");
    }
}
