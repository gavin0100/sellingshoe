package com.data.filtro.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/logout")
public class LogoutController {
    @GetMapping
    public String logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String sessionAccount = "";
        if (session.getAttribute("user") != null){
            sessionAccount = "user";
        }
        if (session.getAttribute("admin") != null){
            sessionAccount = "admin";
        }
        System.out.println(sessionAccount);
        session.invalidate();
        // Clear the security context
        SecurityContextHolder.clearContext();

        // Create a new cookie with the same name as the JWT cookie and set its max age to 0
        Cookie jwtCookie = new Cookie("token", null);
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true); // Make sure to set the HttpOnly flag as when the cookie was set
        jwtCookie.setMaxAge(0); // Set expiry to 0 to delete the cookie

        // If your cookie is also secure, make sure to set this flag as well
        if (request.isSecure()) {
            jwtCookie.setSecure(true);
        }

        // Add the cookie to the response to instruct the browser to delete it
        response.addCookie(jwtCookie);
        if (sessionAccount.equals("user")){
            return "redirect:/login";
        }
        if (sessionAccount.equals("admin")){
            return "redirect:/admin/login";
        }
        return "redirect:/login";
    }
}
