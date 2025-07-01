package com.fitness.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

@Service
public class JwtService {
    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.issuer}") private String issuer;
    @Value("${jwt.access-token-expiry-minutes}") private int expiry;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String userId, String email, Set<String> roles) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("email", email)
                .claim("roles", roles)
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusSeconds(expiry * 60)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractUserId(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey())
                .build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
}

