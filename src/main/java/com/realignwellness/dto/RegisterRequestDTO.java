package com.realignwellness.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    @NotBlank
    private String fullName;
    @NotBlank @Email
    private String email;
    @NotBlank private String phone;
}
