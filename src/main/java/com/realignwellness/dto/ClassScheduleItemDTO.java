package com.realignwellness.dto;

import lombok.Data;

@Data
public class ClassScheduleItemDTO {
    private String id;
    private String day;
    private String time;
    private String className;
    private String instructor;
    private String joinUrl;
}

