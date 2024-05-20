package com.data.filtro.controller.admin;

import com.data.filtro.exception.AuthenticationAccountException;
import com.data.filtro.model.*;
import com.data.filtro.service.AuthenticationService;
import com.data.filtro.service.CartService;
import com.data.filtro.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/login")
public class LoginAdminController {

    @Autowired
    UserService userService;
    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping
    public String show(HttpSession session) {
        if (session.getAttribute("admin") != null){
            return "redirect:/admin/dashboard";
        }
        return "admin/boot1/login";
    }

    @PostMapping
    public String login(@RequestParam("accountName") String accountName,
                        @RequestParam("password") String password,
                        HttpServletResponse response,
                        HttpSession session,
                        Model model) {

        try {
            AuthenticateResponse authenticateResponse = authenticationService.authenticate(accountName, password, session);
            session.setAttribute("admin", authenticateResponse.getUser());
            Cookie cookie = new Cookie("token", authenticateResponse.getAccessToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/"); // This makes the cookie valid for all routes on your domain
            response.addCookie(cookie);
//            System.out.println(account.getAccountName());
            return "redirect:/admin";
        } catch (AuthenticationAccountException exception) {
            exception.printStackTrace();
            model.addAttribute("message", "Tên tài khoản hoặc mật khẩu không chính xác");
        } catch (Exception err){
            model.addAttribute("message", "Tên tài khoản hoặc mật khẩu không chính xác");
        }
        return "admin/boot1/login";
    }

}
