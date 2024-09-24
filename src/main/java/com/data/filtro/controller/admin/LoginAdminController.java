package com.data.filtro.controller.admin;

import com.data.filtro.exception.AuthenticationAccountException;
import com.data.filtro.model.AuthenticateResponse;
import com.data.filtro.model.Role;
import com.data.filtro.model.User;
import com.data.filtro.service.AuthenticationService;
import com.data.filtro.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/login")
public class LoginAdminController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public LoginAdminController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @GetMapping
    public String show() {
        User user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            user = (User) authentication.getPrincipal();
            if (user.getUserPermission().getRole() == Role.ADMIN){
                return "redirect:/admin/dashboard";
            }
        }
        return "admin/boot1/login";
    }

    @PostMapping
    public String login(@RequestParam("accountName") String accountName,
                        @RequestParam("password") String password,
                        HttpServletResponse response,
                        Model model) {

        try {
            AuthenticateResponse authenticateResponse = authenticationService.authenticate(accountName, password);
            Cookie cookie = new Cookie("fourleavesshoestoken", authenticateResponse.getAccessToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/"); // This makes the cookie valid for all routes on your domain
            response.addCookie(cookie);
            return "redirect:/admin/dashboard";
        } catch (AuthenticationAccountException exception) {
            model.addAttribute("message", "Tên tài khoản hoặc mật khẩu không chính xác");
        } catch (Exception err){
            model.addAttribute("message", "Tên tài khoản hoặc mật khẩu không chính xác");
        }
        return "admin/boot1/login";
    }

}
