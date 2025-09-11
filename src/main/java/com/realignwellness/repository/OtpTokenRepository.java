package com.realignwellness.repository;

import com.realignwellness.entity.OtpToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OtpTokenRepository extends MongoRepository<OtpToken, String> {
    Optional<OtpToken> findTopByUserIdAndPurposeAndConsumedAtIsNullOrderByExpiresAtDesc(String userId, String purpose);
}
