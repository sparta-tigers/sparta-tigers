package com.sparta.spartatigers.global.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(wrappedRequest, wrappedResponse);

        logRequest(wrappedRequest);
        logResponse(wrappedResponse);

        wrappedResponse.copyBodyToResponse();
    }

    private static final Logger requestLogger = LoggerFactory.getLogger("RequestLogger");

    private void logRequest(ContentCachingRequestWrapper request) {
        String method = request.getMethod();
        String url = request.getRequestURI();
        String body = getBody(request.getContentAsByteArray());

        requestLogger.info("[Request] {}{} ", method, url);
        if (!body.isBlank()) {
            requestLogger.info("  └ 요청바디 -  {} ", body);
        }
    }

    private void logResponse(ContentCachingResponseWrapper response) {
        int status = response.getStatus();
        String body = getBody(response.getContentAsByteArray());

        requestLogger.info("[Response] 상태코드 : {} ", status);
        if (!body.isBlank()) {
            requestLogger.info("  └ 응답바디 -  {} ", body);
        }
    }

    private String getBody(byte[] content) {
        if (content == null || content.length == 0) return "";
        return new String(content, StandardCharsets.UTF_8);
    }
}
