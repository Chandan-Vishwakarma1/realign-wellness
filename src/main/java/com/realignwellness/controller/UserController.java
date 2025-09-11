package com.realignwellness.controller;

import com.realignwellness.dto.ApiResponse;
import com.realignwellness.dto.UserProfileDTO;
import com.realignwellness.entity.User;
import com.realignwellness.mapper.UserMapper;
import com.realignwellness.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','TEACHER','ADMIN')")
    public ApiResponse<UserProfileDTO> me() {
        User u = authService.getCurrentUser();
        return ApiResponse.<UserProfileDTO>builder()
                .success(true).code("PROFILE").message("Current user")
                .timestamp(Instant.now()).data(userMapper.toProfile(u)).build();
    }

    @GetMapping("/admin/ping")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> adminPing() {
        return ApiResponse.<String>builder()
                .success(true).code("ADMIN_OK").message("Admin route")
                .timestamp(Instant.now()).data("pong").build();
    }
}

