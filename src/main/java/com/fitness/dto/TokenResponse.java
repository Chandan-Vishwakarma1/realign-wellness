package com.fitness.dto;

import java.time.Instant;

public record TokenResponse(String token, Instant expiresAt) {}
