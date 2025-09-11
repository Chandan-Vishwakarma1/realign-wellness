package com.realignwellness.entity;

import com.realignwellness.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@Document("users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes({
        @CompoundIndex(name="email_unique", def="{ 'email': 1 }", unique = true),
        @CompoundIndex(name="phone_unique", def="{ 'phone': 1 }", unique = true)
})
public class User {
    @Id
    private String id;
    private String fullName;
    private String email;
    private String phone;
    private Set<Role> roles;
    private Instant createdAt;
    private Instant updatedAt;
}

