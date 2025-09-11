package com.realignwellness.controller;

import com.realignwellness.dto.*;
import com.realignwellness.entity.User;
import com.realignwellness.mapper.UserMapper;
import com.realignwellness.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserProfileDTO>> register(@Valid @RequestBody RegisterRequestDTO dto) {
        User user = authService.register(dto);
        UserProfileDTO profile = userMapper.toProfile(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<UserProfileDTO>builder()
                .success(true).code("USER_CREATED").message("User registered").timestamp(Instant.now()).data(profile).build());
    }

    @PostMapping("/request-otp")
    public ResponseEntity<ApiResponse<Void>> requestOtp(@Valid @RequestBody RequestOtpDTO dto) {
        authService.requestLoginOtp(dto.getEmail());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).code("OTP_SENT").message("OTP sent to email").timestamp(Instant.now()).data(null).build());
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Map<String, String>>> verifyOtp(@Valid @RequestBody VerifyOtpDTO dto) {
        String token = authService.verifyLoginOtp(dto.getEmail(), dto.getOtp());
        Map<String, String> data = Map.of("accessToken", token, "tokenType", "Bearer");
        return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
                .success(true).code("LOGIN_OK").message("Authenticated").timestamp(Instant.now()).data(data).build());
    }
}

