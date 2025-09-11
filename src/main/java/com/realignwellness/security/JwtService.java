package com.realignwellness.security;

import com.realignwellness.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${app.jwt.secret}") private String secret;
    @Value("${app.jwt.access-exp-min}") private long accessExpMin;

    private Key key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String userId, Set<Role> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(userId)
                .claim("roles", roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(Duration.ofMinutes(accessExpMin))))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
    }

    public String getSubject(String token) { return parse(token).getBody().getSubject(); }

    public Set<String> getRoles(String token) {
        Object roles = parse(token).getBody().get("roles");
        if (roles instanceof Collection<?> c) {
            return c.stream().map(Object::toString).collect(Collectors.toSet());
        }
        return Set.of();
    }
}
