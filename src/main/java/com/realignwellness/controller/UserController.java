package com.realignwellness.controller;

import com.realignwellness.dto.ApiResponse;
import com.realignwellness.dto.UserDTO;
import com.realignwellness.entity.User;
import com.realignwellness.mapper.UserMapper;
import com.realignwellness.service.AuthService;
import com.realignwellness.service.UserService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;
    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','TRAINER','ADMIN')")
    public ApiResponse<UserDTO> me() {
        User u = authService.getCurrentUser();
        return ApiResponse.<UserDTO>builder()
                .success(true).code("PROFILE").message("Current user")
                .timestamp(Instant.now()).data(userMapper.toUserDto(u)).build();
    }

    @GetMapping("/admin/ping")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> adminPing() {
        return ApiResponse.<String>builder()
                .success(true).code("ADMIN_OK").message("Admin route")
                .timestamp(Instant.now()).data("pong").build();
    }

    @GetMapping
    public ApiResponse<Page<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page number must be non-negative")
            int page,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Page size must be at least 1")
            @Max(value = 100, message = "Page size must not exceed 100")
            int size,
            @RequestParam(defaultValue = "id")
            String sortBy) {

        Page<UserDTO> users = userService.getAllUsers(page, size, sortBy);
        return ApiResponse.<Page<UserDTO>>builder()
                .success(true)
                .code("USERS_RETRIEVED")
                .message("Users retrieved successfully")
                .timestamp(Instant.now())
                .data(users)
                .build();
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/filter/role/{role}")
    public ApiResponse<Page<UserDTO>> listUsersByRole(
            @PathVariable String role,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy) {

        Page<UserDTO> users = userService.getUsersByRole(role, page, size, sortBy);

        return ApiResponse.<Page<UserDTO>>builder()
                .success(true).code("OK").message("Users with role: " + role.toUpperCase())
                .timestamp(Instant.now()).data(users).build();
    }

}

