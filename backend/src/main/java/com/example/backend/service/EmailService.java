package com.example.backend.service;

import com.example.backend.enums.OtpType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String toEmail, String otp, OtpType otpType) {
        String subject;
        String emailContent;

        if (otpType == OtpType.REGISTRATION) {
            subject = "Mã xác thực đăng ký tài khoản";
            emailContent =
                    "<div style='font-family: Arial, sans-serif; padding: 20px;'>" +
                            "<h2>Xác nhận đăng ký tài khoản</h2>" +
                            "<p>Mã xác thực OTP của bạn là: <strong>" + otp + "</strong></p>" +
                            "<p>Mã này sẽ hết hạn sau 10 phút.</p>" +
                            "<p>Vui lòng không chia sẻ mã này với bất kỳ ai.</p>" +
                            "</div>";
        } else {  // PASSWORD_RESET
            subject = "Mã xác thực đặt lại mật khẩu";
            emailContent =
                    "<div style='font-family: Arial, sans-serif; padding: 20px;'>" +
                            "<h2>Đặt lại mật khẩu</h2>" +
                            "<p>Bạn đã yêu cầu đặt lại mật khẩu.</p>" +
                            "<p>Mã xác thực OTP của bạn là: <strong>" + otp + "</strong></p>" +
                            "<p>Mã này sẽ hết hạn sau 10 phút.</p>" +
                            "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>" +
                            "</div>";
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(emailContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email: " + e.getMessage());
        }
    }
}
