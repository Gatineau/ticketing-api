package com.assignment.ticketing.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class MDCFilter extends OncePerRequestFilter {

    private static final String TRACE_ID = "traceId";

    /*
     * This filter intercepts every HTTP request and adds a unique trace ID to the Mapped Diagnostic Context.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String traceId = request.getHeader(TRACE_ID);
            if (traceId == null || traceId.isBlank()) {
                traceId = UUID.randomUUID().toString();
            }
            MDC.put(TRACE_ID, traceId);

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}