package com.realignwellness.dto;

import lombok.Data;

@Data
public class ClassScheduleItemDTO {
    private String id;
    private String day;
    private String startTime;
    private String endTime;
    private String className;
    private String instructor;
    private String joinUrl;
}

