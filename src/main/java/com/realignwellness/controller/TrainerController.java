package com.realignwellness.controller;

import java.time.Instant;
import java.util.List;

import com.realignwellness.dto.TrainerProfileDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.realignwellness.dto.ApiResponse;
import com.realignwellness.mapper.TrainerMapper;
import com.realignwellness.service.TrainerService;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/instructors")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService service;
    private final TrainerMapper mapper;

//    @PreAuthorize("hasAnyRole('USER','TEACHER','ADMIN')")
//    @GetMapping("/active")
//    public ApiResponse<List<TrainerProfileDTO>> active() {
//        var list = service.listActive().stream().map(mapper::toDto).toList();
//        return ApiResponse.<List<TrainerProfileDTO>>builder()
//                .success(true).code("OK").message("Active instructors")
//                .timestamp(Instant.now()).data(list).build();
//    }

    @PreAuthorize("hasAnyRole('USER','TEACHER','ADMIN')")
    @GetMapping("/active")
    public ApiResponse<List<TrainerProfileDTO>> active() {
        var list = service.listActive().stream().map(mapper::toDto).toList();
        return ApiResponse.<List<TrainerProfileDTO>>builder()
                .success(true).code("OK").message("Active instructors")
                .timestamp(Instant.now()).data(list).build();
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<TrainerProfileDTO>> create(
            @RequestParam @NotBlank String fullName,
            @RequestParam(defaultValue = "true") boolean active) {
        var saved = service.create(fullName, active);
        var body = ApiResponse.<TrainerProfileDTO>builder()
                .success(true).code("CREATED").message("Instructor created")
                .timestamp(Instant.now()).data(mapper.toDto(saved)).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<TrainerProfileDTO> update(
            @PathVariable String id,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) Boolean active) {
        var saved = service.update(id, fullName, active);
        return ApiResponse.<TrainerProfileDTO>builder()
                .success(true).code("UPDATED").message("Instructor updated")
                .timestamp(Instant.now()).data(mapper.toDto(saved)).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/toggle")
    public ApiResponse<TrainerProfileDTO> toggle(@PathVariable String id) {
        service.toggle(id);
        // Optional: return latest state if needed; else omit data or re-fetch
        return ApiResponse.<TrainerProfileDTO>builder()
                .success(true).code("TOGGLED").message("Instructor toggled")
                .timestamp(Instant.now()).data(null).build();
    }


}
