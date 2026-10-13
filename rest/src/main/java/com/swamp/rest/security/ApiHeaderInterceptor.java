package com.swamp.rest.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiHeaderInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String apiVersion = request.getHeader(ApiHeaders.VERSION_HEADER);
        String acceptHeader = request.getHeader(ApiHeaders.ACCEPT_HEADER);

        if (!ApiHeaders.CURRENT_VERSION.equals(apiVersion)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return false;
        }

        if (acceptHeader == null || !acceptHeader.contains(ApiHeaders.ACCEPT_JSON)) {
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
            return false;
        }

        return true;
    }
}
