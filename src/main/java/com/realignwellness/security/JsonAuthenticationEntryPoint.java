package com.realignwellness.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.realignwellness.dto.ApiResponse;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;

    public JsonAuthenticationEntryPoint() {
        this.mapper = new ObjectMapper();
        // Optional but recommended for Instant ISO output
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
            throws IOException {

        if (response.isCommitted()) {
            return; // avoid double-write
        }

        response.resetBuffer(); // clear any partial buffer from previous filters
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String reason = (String) request.getAttribute("auth_error");
        String code;
        String message;

        if (reason == null) {
            code = "UNAUTHORIZED";
            message = ex != null && ex.getMessage() != null ? ex.getMessage() : "Unauthorized.";
        } else {
            switch (reason) {
                case "MISSING_TOKEN" -> { code = "MISSING_TOKEN"; message = "Authorization token is required."; }
                case "TOKEN_EXPIRED" -> { code = "TOKEN_EXPIRED"; message = "Token has expired. Please authenticate again."; }
                case "BAD_SIGNATURE" -> { code = "INVALID_SIGNATURE"; message = "Token signature is invalid."; }
                case "MALFORMED_TOKEN" -> { code = "MALFORMED_TOKEN"; message = "Token format is invalid."; }
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

        try (ServletOutputStream out = response.getOutputStream()) {
            mapper.writeValue(out, body);
            out.flush();
        }
    }
}



