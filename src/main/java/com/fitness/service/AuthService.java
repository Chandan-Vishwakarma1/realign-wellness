package com.fitness.service;

import com.fitness.Enums.AuthProvider;
import com.fitness.Enums.Roles;
import com.fitness.dto.LoginRequest;
import com.fitness.dto.RegisterRequest;
import com.fitness.dto.TokenResponse;
import com.fitness.dto.VerifyOtpRequest;
import com.fitness.entities.User;
import com.fitness.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final OtpService otpService;
    private final JwtService jwtService;

    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    public void register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.email())) throw new RuntimeException("Email exists");
        User user = User.builder()
                .fullName(req.fullName())
                .email(req.email())
                .phone(req.phone())
                .gender(req.gender())
                .dob(req.dob())
                .passwordHash(encoder.encode(req.password()))
                .roles(Set.of(Roles.USER))
                .enabled(false)
                .provider(AuthProvider.LOCAL)
                .build();
        userRepo.save(user);
        otpStore.put(user.getEmail(), otpService.sendOtp(user.getEmail()));
    }

    public TokenResponse verifyOtp(VerifyOtpRequest req) {
        String code = otpStore.get(req.email());
        if (code == null || !code.equals(req.code())) throw new RuntimeException("Invalid OTP");

        User user = userRepo.findByEmail(req.email()).orElseThrow();
        user.setEnabled(true);
        userRepo.save(user);
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
        return new TokenResponse(token, Instant.now().plusSeconds(15 * 60));
    }

    public TokenResponse login(LoginRequest req) {
        User user = userRepo.findByEmail(req.email()).orElseThrow();
        if (!encoder.matches(req.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
        return new TokenResponse(token, Instant.now().plusSeconds(15 * 60));
    }
}

