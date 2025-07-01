package com.fitness.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final JavaMailSender mailSender;

    public String sendOtp(String email) {
        String code = String.format("%06d", new Random().nextInt(999999));
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("OTP Verification");
        message.setText("Your OTP is: " + code);
        mailSender.send(message);
        return code;
    }
}

