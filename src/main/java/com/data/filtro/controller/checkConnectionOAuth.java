package com.data.filtro.controller;

import com.data.filtro.model.AuthenticateResponse;
import com.data.filtro.service.AuthenticationService;
import com.data.filtro.service.CartService;
import com.data.filtro.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class checkConnectionOAuth {

    private final UserService userService;

    private final AuthenticationService authenticationService;

    private final CartService cartService;

    public checkConnectionOAuth(UserService userService, AuthenticationService authenticationService, CartService cartService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.cartService = cartService;
    }

    @GetMapping("/user_google_hihi")
    public String getUser(@AuthenticationPrincipal OAuth2User oAuth2User,
                          HttpServletResponse response,
                          Model model){
        String accountName = String.valueOf(oAuth2User.getAttributes().get("email"));
        String password = String.valueOf(oAuth2User.getAttributes().get("email"))+String.valueOf(oAuth2User.getAttributes().get("email")).split("@")[0] + "ABCDEF12@";
        if (userService.getUserByEmail(String.valueOf(oAuth2User.getAttributes().get("email"))) == null){
            userService.registerUser(String.valueOf(oAuth2User.getAttributes().get("name")),
                    String.valueOf(oAuth2User.getAttributes().get("email")),
                    String.valueOf(oAuth2User.getAttributes().get("email")),
                    password,
                    password);
        }

        AuthenticateResponse authenticateResponse = authenticationService.authenticate(accountName, password);
        Cookie cookie = new Cookie("fourleavesshoestoken", authenticateResponse.getAccessToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/"); // This makes the cookie valid for all routes on your domain
        response.addCookie(cookie);
        return "redirect:/";
    }
}
