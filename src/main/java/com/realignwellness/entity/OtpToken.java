package com.realignwellness.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

// entity/OtpToken.java
@Document("otp_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(name="user_purpose_idx", def="{ 'userId': 1, 'purpose': 1 }")
public class OtpToken {
    @Id
    private String id;
    private String userId;
    private String purpose; // e.g., "LOGIN"
    private String otpHash;
    private Instant expiresAt;
    private Instant consumedAt;
}

