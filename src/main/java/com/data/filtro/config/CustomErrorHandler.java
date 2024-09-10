package com.data.filtro.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class CustomErrorHandler implements AccessDeniedHandler, AuthenticationEntryPoint {
//public class CustomErrorHandler implements AuthenticationEntryPoint {
    // handle là của AccessDeniedHandler, được thực thi nếu ng dùng không có quyền,
    // lúc này request đã đi qua hầu hết bộ lọc filter, bị kẹt lại ở AuthorizationFilter chỉ còn bước lấy Authentication
    // từ SecurityContextHolder lên để so sáng với @PreAuthorized nhưng không khớp
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        System.out.println("CustomErrorHandler AccessDeniedHandler");
        response.sendRedirect("/");
    }

    // commence là của AuthenticationEntryPoint, được thực thi nếu ng dùng chưa được xác thực
    // mà muốn thực hiện request cần PreAuthorized
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        System.out.println("CustomErrorHandler AuthenticationEntryPoint");
        response.sendRedirect("/access-denied");
    }
}
