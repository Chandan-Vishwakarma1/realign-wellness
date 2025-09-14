package com.realignwellness.security;

import java.io.IOException;
import java.time.Instant;

import com.realignwellness.dto.ApiResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException {
        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .success(false).code("FORBIDDEN").message("Access denied").timestamp(Instant.now()).data(null).build();
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType("application/json");
		response.getWriter().write(mapper.writeValueAsString(body));
	}
}