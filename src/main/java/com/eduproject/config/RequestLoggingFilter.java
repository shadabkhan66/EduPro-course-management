package com.eduproject.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Debug filter that logs every incoming request and outgoing response.
 * <p>
 * Use this to visualize the request flow when debugging. Runs first in the filter chain
 * (HIGHEST_PRECEDENCE) so you see the full round-trip.
 * <p>
 * To disable: remove @Component or add a profile condition like @Profile("debug").
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String sessionId = request.getSession(false) != null
                ? request.getSession().getId()
                : "none";
        String auth = request.getRemoteUser();

        log.info(">>> REQUEST: {} {} {} | Session: {} | User: {}",
                method,
                uri,
                queryString != null ? "?" + queryString : "",
                sessionId,
                auth != null ? auth : "anonymous");

        long startTime = System.currentTimeMillis();
        filterChain.doFilter(request, response);
        long duration = System.currentTimeMillis() - startTime;

        log.info("<<< RESPONSE: {} {} | Status: {} | Duration: {}ms",
                method, uri, response.getStatus(), duration);
    }
}
