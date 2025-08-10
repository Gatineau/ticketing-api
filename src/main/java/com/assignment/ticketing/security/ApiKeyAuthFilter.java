package com.assignment.ticketing.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.assignment.ticketing.config.ApiKeyConfig;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-KEY";

    private final ApiKeyConfig apiKeyConfig;

    public ApiKeyAuthFilter(ApiKeyConfig apiKeyConfig) {
        this.apiKeyConfig = apiKeyConfig;
    }

    /*
     * This filter checks for the presence of an API key in the request header.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String apiKey = request.getHeader(API_KEY_HEADER);

        Map<String, String> apiKeyToRole = Map.of(
                apiKeyConfig.getUser(), "ROLE_USER",
                apiKeyConfig.getAgent(), "ROLE_AGENT");

        if (apiKey == null || !apiKeyToRole.containsKey(apiKey)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Missing or invalid API key");
            return;
        }

        String role = apiKeyToRole.get(apiKey);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                apiKey, null, List.of(new SimpleGrantedAuthority(role)));

        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }

    /*
     * This method checks if the filter should be applied to the request.
     * It skips filtering for Swagger and API documentation endpoints.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui") ||
                path.equals("/swagger-ui.html") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars");
    }
}