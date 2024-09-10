package com.data.filtro.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Chuyển tiếp yêu cầu đến URL /login với phương thức POST
//        request.getRequestDispatcher("/login").forward(request, response);
//        System.out.println("SecurityContextHolder chứa thông tin: " +
//                SecurityContextHolder.getContext().getAuthentication().getCredentials() +
//                SecurityContextHolder.getContext().getAuthentication().getPrincipal() +
//                SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        request.getRequestDispatcher("/login/logincontroller").forward(request, response);
    }
}

