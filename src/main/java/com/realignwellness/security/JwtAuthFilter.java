package com.realignwellness.security;

import com.realignwellness.entity.User;
import com.realignwellness.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String auth = req.getHeader("Authorization");

        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                String userId = jwtService.getSubject(token);
                Optional<User> opt = userRepo.findById(userId);
                if (opt.isPresent()) {
                    User user = opt.get();
                    Set<GrantedAuthority> auths = user.getRoles().stream()
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                            .collect(Collectors.toSet());
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user.getId(), null, auths);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // user id from token not found
                    req.setAttribute("auth_error", "UNKNOWN_USER");
                }
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                req.setAttribute("auth_error", "TOKEN_EXPIRED");
            } catch (io.jsonwebtoken.security.SignatureException e) {
            } catch (io.jsonwebtoken.JwtException e) {
                req.setAttribute("auth_error", "INVALID_TOKEN");
            }
        } else if (auth == null || auth.isBlank()) {
            // No Authorization header
            req.setAttribute("auth_error", "MISSING_TOKEN");
        }

        chain.doFilter(req, res);
    }
}
