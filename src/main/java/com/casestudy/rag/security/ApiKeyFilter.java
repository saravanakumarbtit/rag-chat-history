package com.casestudy.rag.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${API_KEY:}")
    private String apiKey;

    private static final String[] WHITELIST = {
            "/swagger-ui",
            "/swagger-ui.html",
            "/swagger-resources",
            "/v3/api-docs",
            "/webjars",
            "/actuator",
            "/actuator/*",
            "/error"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // ✅ Skip API key validation for Swagger/OpenAPI routes
        for (String allowed : WHITELIST) {
            if (path.startsWith(allowed)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 🔒 Enforce API key for all other endpoints
        String header = request.getHeader("X-API-KEY");
        if (apiKey == null || apiKey.isBlank() || !apiKey.equals(header)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid API key");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
