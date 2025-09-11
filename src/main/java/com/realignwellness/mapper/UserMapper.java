package com.realignwellness.mapper;

import com.realignwellness.dto.RegisterRequestDTO;
import com.realignwellness.dto.UserProfileDTO;
import com.realignwellness.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target="id", ignore=true)
    @Mapping(target="roles", expression = "java(java.util.Set.of(com.realignwellness.enums.Role.USER))")
    @Mapping(target="createdAt", expression="java(java.time.Instant.now())")
    @Mapping(target="updatedAt", expression="java(java.time.Instant.now())")
    User toEntity(RegisterRequestDTO dto);

    UserProfileDTO toProfile(User user);
}
