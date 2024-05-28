package com.data.filtro.controller;

import com.data.filtro.model.AuthenticateResponse;
import com.data.filtro.model.Cart;
import com.data.filtro.model.GuestCart;
import com.data.filtro.service.AuthenticationService;
import com.data.filtro.service.CartService;
import com.data.filtro.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Controller
public class checkConnectionOAuth {

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    CartService cartService;
    @GetMapping("/user_google_hihi")
    public String getUser(@AuthenticationPrincipal OAuth2User oAuth2User,
                          HttpServletResponse response,
                          HttpSession session,
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

        AuthenticateResponse authenticateResponse = authenticationService.authenticate(accountName, password, session);
//            User user = userService.authenticateUser(accountName, password);
//            System.out.println(user.getName());
        session.setAttribute("user", authenticateResponse.getUser());
        Cookie cookie = new Cookie("token", authenticateResponse.getAccessToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/"); // This makes the cookie valid for all routes on your domain
        response.addCookie(cookie);
        Cart cart = cartService.getCurrentCartByUserId(authenticateResponse.getUser().getId());
        GuestCart guestCart = (GuestCart) session.getAttribute("guestCart");
        if (guestCart != null) {
            cart = cartService.convertGuestCartToCart(guestCart,  authenticateResponse.getUser());
            session.removeAttribute("guestCart");
        }
        session.setAttribute("cart", cart);
        return "redirect:/";
    }
}
