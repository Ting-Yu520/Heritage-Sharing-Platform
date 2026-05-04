package com.heritage.platform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Send a plain-text verification code email
     * @param to recipient email
     * @param code 6-digit verification code
     */
    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("3357279913@qq.com"); // Must match spring.mail.username
        message.setTo(to);
        message.setSubject("Heritage Platform - Password Reset Code");
        message.setText("Your verification code is: " + code + "\n\nThis code is valid for 24 hours.");
        mailSender.send(message);
    }
}
