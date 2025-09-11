package com.realignwellness.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyOtpDTO {
    @NotBlank
    @Email
    private String email;
    @NotBlank @Pattern(regexp="\\d{6}") private String otp;
}
