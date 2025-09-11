package com.realignwellness.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestOtpDTO {
    @NotBlank
    @Email
    private String email;
}
