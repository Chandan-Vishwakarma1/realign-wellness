package com.realignwellness.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String code;
    private String message;
    private Instant timestamp;
    private T data;
}