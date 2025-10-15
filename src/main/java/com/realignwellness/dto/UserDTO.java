package com.realignwellness.dto;

import com.realignwellness.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserDTO {
    private String id;
    private String fullName;
    private String email;
    private String phone;
    private Set<Role> roles;
}
