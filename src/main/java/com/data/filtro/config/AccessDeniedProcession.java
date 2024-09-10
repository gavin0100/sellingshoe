package com.data.filtro.config;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@Component
@ControllerAdvice
public class AccessDeniedProcession {
//    @ExceptionHandler(value = AccessDeniedException.class)
//    public ModelAndView accessDenied() {
//        System.out.println("AccessDeniedProcession được gọi");
//        return new ModelAndView("error/accessDenied");
//    }

    // AccessDeniedException là do AuthorizationFilter có order 3600 thấp nhất quăng ra
    // nên mấy lớp khác global mới bắt được

    // cách này không bắt AccessDeniedException trong docker được
    @ExceptionHandler(value = AccessDeniedException.class)
    public void accessDenied(HttpServletResponse response) throws IOException, MyServletException {
        System.out.println("AccessDeniedProcession được gọi");
        response.sendRedirect("/");
//        throw new MyServletException("từ chối truy cập", null, false, false);
//        throw new AccessDeniedException("không có quyền truy cập");
    }

//    @ExceptionHandler(value = MyServletException.class)
//    public void jwtTokenNotValid(HttpServletResponse response) throws IOException, MyServletException {
//        System.out.println("jwtTokenNotValidProcession được gọi");
//        response.sendRedirect("/");
//    }
}