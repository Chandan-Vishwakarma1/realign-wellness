package com.realignwellness.service;

import com.realignwellness.entity.OtpToken;
import com.realignwellness.entity.User;
import com.realignwellness.repository.OtpTokenRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OtpService {
    @Value("${app.otp.length}")
    private int length;
    @Value("${app.otp.ttl-min}")
    private long ttlMin;
    private final OtpTokenRepository otpRepo;
    private final PasswordEncoder encoder;
    private final JavaMailSender mailSender;
    @Value("${app.otp.email.subject}")
    private String subject;

    public void sendLoginOtp(User user) {
        String otp = generateOtp();
        String hash = encoder.encode(otp);
        OtpToken token = OtpToken.builder()
                .userId(user.getId())
                .purpose("LOGIN")
                .otpHash(hash)
                .expiresAt(Instant.now().plus(Duration.ofMinutes(ttlMin)))
                .build();
        otpRepo.save(token);
        try {
            sendEmail(user.getEmail(), subject, "Your OTP is: " + otp + " (valid for " + ttlMin + " minutes)");
        } catch (MailException e) {
                throw new MailSendException("Failed to send email", e);
        }
    }

    public boolean verifyAndConsume(User user, String otp) {
        OtpToken latest = otpRepo.findTopByUserIdAndPurposeAndConsumedAtIsNullOrderByExpiresAtDesc(user.getId(), "LOGIN")
                .orElse(null);
        if (latest == null || latest.getExpiresAt().isBefore(Instant.now())) return false;
        if (!encoder.matches(otp, latest.getOtpHash())) return false;
        latest.setConsumedAt(Instant.now());
        otpRepo.save(latest);
        return true;
    }

    private String generateOtp() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - 1;
        return String.format("%0" + length + "d", rnd.nextInt(min, max + 1));
    }

    private void sendEmail(String to, String subject, String body) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }
}
