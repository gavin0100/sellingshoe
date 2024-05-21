package com.data.filtro.config;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Component
@ControllerAdvice
public class AccessDeniedProcession {
    @ExceptionHandler(value = AccessDeniedException.class)
    public ModelAndView accessDenied() {
        return new ModelAndView("error/accessDenied");
    }
}
