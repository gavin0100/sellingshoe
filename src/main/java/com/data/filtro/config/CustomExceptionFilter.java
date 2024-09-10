package com.data.filtro.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class CustomExceptionFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        }
        catch (AccessDeniedException accessDeniedException){
            System.out.println("AccessDeniedException trong CustomExceptionFilter  được gọi");
            ((HttpServletResponse) servletResponse).sendRedirect("/");
        } catch(JwtNullOrEmptyException jwtNullOrEmptyException){
            System.out.println("bat JwtNullOrEmptyException trong CustomExceptionFilter");
            ((HttpServletResponse) servletResponse).sendRedirect("/logout_to_login/fromJwtEmptyOrNullException");
        }
        // chỗ này bắt luôn AccessDeniedException
        catch (RuntimeException ex) {
            // Handle your custom exception here
            System.out.println("bat RuntimeException trong CustomExceptionFilter");
            ((HttpServletResponse) servletResponse).sendRedirect("/logout_to_login/fromJwtEmptyOrNullException");
        }
    }
}
