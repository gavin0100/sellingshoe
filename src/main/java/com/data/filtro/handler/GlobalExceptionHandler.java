package com.data.filtro.handler;

import com.data.filtro.exception.ApiResponse;
import com.data.filtro.exception.EmptyException;
import com.data.filtro.exception.NotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex) {
        return ex.getMessage();
    }

    @ExceptionHandler({NotFoundException.class, EmptyException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<?> handleNotFoundException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("statusCode", "404");
        error.put("message", ex.getMessage());
        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.error(error);
        return apiResponse;
    }

    @ExceptionHandler({AccessDeniedException.class})
    public void handleAccessDeniedException(Exception ex, HttpServletResponse response) throws IOException {
        for (GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            if (!authority.getAuthority().equals("ROLE_USER")) {
                response.sendRedirect("/admin/dashboard");
            } else {
                response.sendRedirect("/");
            }
            return;
        }
    }




}
