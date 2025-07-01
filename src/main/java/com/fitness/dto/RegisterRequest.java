package com.fitness.dto;

import com.fitness.Enums.Gender;

import java.time.LocalDate;

public record RegisterRequest(String fullName, String email, String phone, Gender gender, LocalDate dob, String password) {}
