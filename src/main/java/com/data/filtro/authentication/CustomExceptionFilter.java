package com.data.filtro.authentication;

import com.data.filtro.authentication.exception.JwtNullOrEmptyException;
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
            ((HttpServletResponse) servletResponse).sendRedirect("/");
        } catch(JwtNullOrEmptyException jwtNullOrEmptyException){
            ((HttpServletResponse) servletResponse).sendRedirect("/logout_to_login/fromJwtEmptyOrNullException");
        } catch (RuntimeException ex) {
            ((HttpServletResponse) servletResponse).sendRedirect("/logout_to_login/fromJwtEmptyOrNullException");
        }
    }
}
