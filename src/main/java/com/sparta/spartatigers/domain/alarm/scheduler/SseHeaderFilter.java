package com.sparta.spartatigers.domain.alarm.scheduler;

import java.io.IOException;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import org.springframework.stereotype.Component;

@Component
public class SseHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResp = (HttpServletResponse) response;

        HttpServletResponseWrapper wrappedResp =
                new HttpServletResponseWrapper(httpResp) {

                    @Override
                    public void setHeader(String name, String value) {
                        if (!"Content-Length".equalsIgnoreCase(name)) {
                            super.setHeader(name, value);
                        }
                    }

                    @Override
                    public void setIntHeader(String name, int value) {
                        if (!"Content-Length".equalsIgnoreCase(name)) {
                            super.setIntHeader(name, value);
                        }
                    }

                    @Override
                    public void setContentLength(int len) {
                        // 무시
                    }

                    @Override
                    public void setContentLengthLong(long len) {
                        // 무시
                    }
                };

        chain.doFilter(request, wrappedResp);
    }
}
