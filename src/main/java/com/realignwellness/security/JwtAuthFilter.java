package com.realignwellness.security;

import com.realignwellness.entity.User;
import com.realignwellness.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

//    private final JwtService jwtService;
//    private final UserRepository userRepo;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
//            throws ServletException, IOException {
//
//        String auth = req.getHeader("Authorization");
//
//        if (auth != null && auth.startsWith("Bearer ")) {
//            String token = auth.substring(7);
//
//            // This will throw ExpiredJwtException, SignatureException, etc. if invalid
//            String userId = jwtService.getSubject(token);
//
//            var userOpt = userRepo.findById(userId);
//            if (userOpt.isPresent()) {
//                var user = userOpt.get();
//                var authorities = user.getRoles().stream()
//                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
//                        .collect(Collectors.toSet());
//
//                var authentication = new UsernamePasswordAuthenticationToken(
//                        user.getId(), null, authorities);
//
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            } else {
//                SecurityContextHolder.clearContext();
//                throw new JwtException("Unknown user for token: " + userId);
//            }
//        }
//
//        // continue filter chain (exceptions bubble up to global handler)
//        chain.doFilter(req, res);
//    }

    private final JwtService jwtService;
    private final UserRepository userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // Validate token
                String userId = jwtService.getSubject(token);

                var userOpt = userRepo.findById(userId);
                if (userOpt.isPresent()) {
                    var user = userOpt.get();
                    var authorities = user.getRoles().stream()
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                            .collect(Collectors.toSet());

                    var authentication = new UsernamePasswordAuthenticationToken(
                            user.getId(), null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    SecurityContextHolder.clearContext();
                    throw new JwtException("Unknown user for token: " + userId);
                }

            } catch ( ExpiredJwtException e) {
                // Let GlobalExceptionHandler handle expired/invalid tokens
                throw e;
            }catch (JwtException e){
                throw e;
            }
        }
        // If authHeader is null → missing token → handled by AuthenticationEntryPoint
        chain.doFilter(request, response);
    }
}
