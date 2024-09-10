package com.data.filtro.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/logout_to_login")
@Controller
public class LogoutToLoginController {
    @GetMapping("/fromErrorPage")
    public String loginFromErrorPage(HttpSession session, HttpServletResponse response){
        session.invalidate();
        // Clear the security context
        SecurityContextHolder.clearContext();

        // Create a new cookie with the same name as the JWT cookie and set its max age to 0
        Cookie jwtCookie = new Cookie("fourleavesshoestoken", null);
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true); // Make sure to set the HttpOnly flag as when the cookie was set
        jwtCookie.setMaxAge(0); // Set expiry to 0 to delete the cookie

        // Add the cookie to the response to instruct the browser to delete it
        response.addCookie(jwtCookie);
        return "redirect:/login";
    }

    @GetMapping("/fromJwtEmptyOrNullException")
    public String loginFromJwtEmptyOrNullException(HttpSession session, HttpServletResponse response){
        session.invalidate();
        // Clear the security context
        SecurityContextHolder.clearContext();

        // Create a new cookie with the same name as the JWT cookie and set its max age to 0
        Cookie jwtCookie = new Cookie("fourleavesshoestoken", null);
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true); // Make sure to set the HttpOnly flag as when the cookie was set
        jwtCookie.setMaxAge(0); // Set expiry to 0 to delete the cookie


        // Add the cookie to the response to instruct the browser to delete it
        response.addCookie(jwtCookie);
        return "redirect:/";
    }

}
