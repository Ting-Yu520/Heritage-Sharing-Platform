package com.heritage.platform.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. Store current user in ThreadLocal
        String username = request.getHeader("X-User-Username");
        if (username != null && !username.isEmpty()) {
            UserContext.setCurrentUser(username);
        } else {
            UserContext.setCurrentUser("System");
        }

        // 2. Role check
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RoleCheck roleCheck = handlerMethod.getMethodAnnotation(RoleCheck.class);

        if (roleCheck == null) {
            roleCheck = handlerMethod.getBeanType().getAnnotation(RoleCheck.class);
        }

        if (roleCheck == null) {
            return true;
        }

        String userRole = request.getHeader("X-User-Role");
        List<String> allowedRoles = Arrays.asList(roleCheck.value());

        if (userRole == null || !allowedRoles.contains(userRole)) {
            response.setStatus(403);
            response.getWriter().write("{\"success\":false,\"message\":\"Insufficient permissions\"}");
            return false;
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Clear ThreadLocal to prevent memory leaks
        UserContext.clear();
    }
}
