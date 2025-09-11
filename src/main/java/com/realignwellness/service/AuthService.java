package com.realignwellness.service;

import com.realignwellness.dto.RegisterRequestDTO;
import com.realignwellness.entity.User;
import com.realignwellness.exception.DuplicateResourceException;
import com.realignwellness.exception.NotFoundException;
import com.realignwellness.exception.UnauthorizedException;
import com.realignwellness.mapper.UserMapper;
import com.realignwellness.repository.UserRepository;
import com.realignwellness.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final UserMapper userMapper;
    private final OtpService otpService;
    private final JwtService jwtService;

    public User register(RegisterRequestDTO dto) {
        String email = dto.getEmail().trim().toLowerCase();
        if (userRepo.existsByEmail(email)) throw new DuplicateResourceException("Email already registered");
        if (userRepo.existsByPhone(dto.getPhone())) throw new DuplicateResourceException("Phone already registered");
        User user = userMapper.toEntity(dto);
        user.setEmail(email);
        return userRepo.save(user);
    }

    public void requestLoginOtp(String email) {
        User user = userRepo.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new NotFoundException("User not found"));
        otpService.sendLoginOtp(user);
    }

    public String verifyLoginOtp(String email, String otp) {
        User user = userRepo.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new NotFoundException("User not found"));
        boolean ok = otpService.verifyAndConsume(user, otp);
        if (!ok) throw new UnauthorizedException("Invalid or expired OTP");
        return jwtService.generateAccessToken(user.getId(), user.getRoles());
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) throw new UnauthorizedException("Unauthenticated");
        String userId = auth.getPrincipal().toString();
        return userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }
}

