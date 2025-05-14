package com.cabinet360.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String token) {
        String subject = "Cabinet360 Email Verification";
        String content = """
                Hello,
                
                Please verify your email by clicking the link below:
                http://localhost:8091/api/v1/auth/verify-email?token=%s
                
                If you did not register, ignore this email.
                """.formatted(token);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
        System.out.println("✅ Verification email sent to: " + to);
    }
}
