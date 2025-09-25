package com.realignwellness.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.realignwellness.dto.ClassScheduleItemDTO;
import com.realignwellness.entity.ClassSchedule;

@Mapper(componentModel = "spring")
public interface ClassScheduleMapper {
    @Mapping(target = "day", source = "dayOfWeek")
    @Mapping(target = "instructor", source = "instructorName")
    ClassScheduleItemDTO toItemDTO(ClassSchedule src);
}
