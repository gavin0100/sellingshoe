package com.data.filtro.controller;

import com.data.filtro.model.Role;
import com.data.filtro.model.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/logout")
public class LogoutController {
    @GetMapping
    public String logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        User user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            user = (User) authentication.getPrincipal();
        }
        String sessionAccount = "";
        if (user.getUserPermission().getRole() == Role.USER){
            sessionAccount = "user";
        }
        if (user.getUserPermission().getRole() == Role.USER){
            sessionAccount = "admin";
        }
        session.invalidate();
        // Clear the security context
        SecurityContextHolder.clearContext();

        // Create a new cookie with the same name as the JWT cookie and set its max age to 0
        Cookie jwtCookie = new Cookie("fourleavesshoestoken", null);
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
