package com.realignwellness.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realignwellness.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String reason = (String) request.getAttribute("auth_error");
        String code;
        String message;

        if (reason == null) {
            // Fallback: infer from exception if available
            code = "UNAUTHORIZED";
            message = defaultMessage(ex);
        } else {
            switch (reason) {
                case "MISSING_TOKEN" -> { code = "MISSING_TOKEN"; message = "Authorization token is required."; }
                case "TOKEN_EXPIRED" -> { code = "TOKEN_EXPIRED"; message = "Token has expired. Please authenticate again."; }
                case "UNKNOWN_USER" -> { code = "UNKNOWN_USER"; message = "User referenced by token does not exist."; }
                case "INVALID_TOKEN" -> { code = "INVALID_TOKEN"; message = "Token is invalid."; }
                default -> { code = "UNAUTHORIZED"; message = "Unauthorized."; }
            }
        }

        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .success(false)
                .code(code)
                .message(message)
                .timestamp(Instant.now())
                .data(null)
                .build();

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }

    private String defaultMessage(AuthenticationException ex) {
        if (ex == null || ex.getMessage() == null) return "Unauthorized.";
        return ex.getMessage();
    }
}

