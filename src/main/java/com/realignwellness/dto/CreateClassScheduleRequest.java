package com.realignwellness.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateClassScheduleRequest {

    @NotBlank
    private String day; // MONDAY,SUNDAY
//    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "startTime must be HH:mm")
    private String time;
//    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "endTime must be HH:mm")
//    private String endTime;
    @NotBlank
    @Size(max = 100)
    private String className;
    @NotBlank
    private String instructorId;
    @NotBlank
    private String joinUrl;
    private Boolean active = Boolean.TRUE;
}
