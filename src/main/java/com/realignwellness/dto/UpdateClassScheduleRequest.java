package com.realignwellness.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateClassScheduleRequest {
    private String day;

    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "startTime must be HH:mm")
    private String startTime;

//    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "endTime must be HH:mm")
//    private String endTime;

    @Size(max = 100)
    private String className;

    private String instructorId;
    private String joinUrl;
    private Boolean active;
}


