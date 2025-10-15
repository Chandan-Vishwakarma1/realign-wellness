package com.realignwellness.mapper;

import com.realignwellness.dto.TrainerProfileDTO;
import org.mapstruct.Mapper;

import com.realignwellness.entity.TrainerProfile;

@Mapper(componentModel = "spring")
public interface TrainerMapper {
    TrainerProfileDTO toDto(TrainerProfile src);
}
