package com.realignwellness.mapper;

import org.mapstruct.Mapper;

import com.realignwellness.dto.InstructorProfileDTO;
import com.realignwellness.entity.InstructorProfile;

@Mapper(componentModel = "spring")
public interface InstructorMapper {
    InstructorProfileDTO toDto(InstructorProfile src);
}
