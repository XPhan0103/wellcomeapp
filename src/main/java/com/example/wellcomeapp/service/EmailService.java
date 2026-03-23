package com.example.wellcomeapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("WellcomeApp <no-reply@wellcomeapp.com>");
        message.setTo(toEmail);
        message.setSubject("Mã xác thực OTP (WellcomeApp)");
        message.setText("Xin chào,\n\n"
                      + "Mã OTP để lấy lại mật khẩu của bạn là: " + otpCode + "\n\n"
                      + "Mã này sẽ có hiệu lực trong vòng 1 phút. Vui lòng tuyệt đối không chia sẻ mã này cho bất kỳ ai.\n\n"
                      + "Trân trọng,\nWellcomeApp Team");
        
        mailSender.send(message);
    }
}
