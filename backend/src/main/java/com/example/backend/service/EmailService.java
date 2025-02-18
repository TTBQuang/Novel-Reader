package com.example.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Mã xác thực đăng ký tài khoản");

            String emailContent =
                    "<div style='font-family: Arial, sans-serif; padding: 20px;'>" +
                            "<h2>Xác nhận đăng ký tài khoản</h2>" +
                            "<p>Mã xác thực OTP của bạn là: <strong>" + otp + "</strong></p>" +
                            "<p>Mã này sẽ hết hạn sau 10 phút.</p>" +
                            "<p>Vui lòng không chia sẻ mã này với bất kỳ ai.</p>" +
                            "</div>";

            helper.setText(emailContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email: " + e.getMessage());
        }
    }
}
