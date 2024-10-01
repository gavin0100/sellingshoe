package com.data.filtro.controller;

import com.data.filtro.Util.Utility;
import com.data.filtro.model.*;
import com.data.filtro.service.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {


    private final CartService cartService;
    private final UserService userService;

    private final AuthenticationService authenticationService;

    private final ProductService productService;

    private final MailSenderService mailSenderService;

    public LoginController(CartService cartService, UserService userService, AuthenticationService authenticationService, ProductService productService, MailSenderService mailSenderService) {
        this.cartService = cartService;
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.productService = productService;
        this.mailSenderService = mailSenderService;
    }


    @GetMapping
    public String show(Model model, HttpSession session) {
        User user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            user = (User) authentication.getPrincipal();
        }
        if (user != null){
            return "redirect:/";
        }
        if (user == null) {
//            sendSMSViaSpeedSMS(Utility.getRandomNumberString());
            return "user/boot1/login";
        }
        else {
            return "redirect:/";
        }
    }

    @GetMapping("/login-failure")
    public String loginFailure(Model model, HttpSession session) {
        model.addAttribute("message", "Thông tin đăng nhập không đúng");
        return "user/boot1/login";
    }

    @PostMapping
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam("_csrfParameterName") String csrfTokenForm,
                        HttpServletResponse response,
                        Authentication authentication,
                        HttpSession session,
                        Model model) {
        model.addAttribute("message", "Thông tin đăng nhập không đúng");
        return "user/boot1/login";
    }

    @GetMapping("/login_failure")
    public String login(HttpServletResponse response,
                        Authentication authentication,
                        HttpSession session,
                        Model model) {
        model.addAttribute("message", "Thông tin đăng nhập không đúng");
        return "user/boot1/login";
    }

    @GetMapping("/oauth_failure")
    public String loginOauth(
            @RequestParam String error,
            HttpServletResponse response,
            Authentication authentication,
            HttpSession session,
            Model model) {
        model.addAttribute("message", "Xác thực qua FaceBook hoặc Google thất bại!");
        return "user/boot1/login";
    }

    @PostMapping("/logincontroller")
    public String login(
            HttpServletResponse response,
            HttpSession session,
            Model model) {
        AuthenticateResponse authenticateResponse = authenticationService.authenticate(SecurityContextHolder.getContext().getAuthentication());
        model.addAttribute("accountName", authenticateResponse.getUser().getAccountName());
        model.addAttribute("password", authenticateResponse.getUser().getPassword());
        String newOtp = Utility.getRandomNumberString();
        userService.updateOtpUser(newOtp, authenticateResponse.getUser().getId());
        String to = authenticateResponse.getUser().getEmail();
        String from = "voduc0100@gmail.com";
        String host = "smtp.gmail.com";
        String subject = "SHOP BÁN GIÀY FOUR LEAVES SHOE - OTP CHO TÀI KHOẢN!";
        mailSenderService.sendEmailGetOtpLogin(to, from, host, subject, newOtp );
        return "user/boot1/otpLogin";
    }
    @PostMapping("/otp")
    public String otpLogin( @RequestParam("accountName") String accountName,
                            @RequestParam("password") String password,
                            @RequestParam("otp") String otp,
                            HttpServletResponse response,
                            HttpSession session,
                            Model model) {
        try{
            User user = userService.getUserFromOtp(accountName, password, otp);
            if (user == null){
                throw new Exception("User not found");
            }
            AuthenticateResponse authenticateResponse = authenticationService.authenticateWithOnlyAccountName(accountName);
            String newOtp = Utility.getRandomNumberString();
            userService.updateOtpUser(newOtp, authenticateResponse.getUser().getId());
            Cookie cookie = new Cookie("fourleavesshoestoken", authenticateResponse.getAccessToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/"); // This makes the cookie valid for all routes on your domain
            response.addCookie(cookie);
            return "redirect:/";
        } catch (Exception ex){
            model.addAttribute("message", "Thông tin đăng nhập không đúng");
            return "user/boot1/login";
        }


    }

    @GetMapping("/session")
    public String check(HttpSession session) {
        return "session";
    }
    public String generateRandomString() {
        return UUID.randomUUID().toString();
    }

    @PostMapping("/oauth2/code/google")
    public String loginViaGoogle(){
        return "loginViaGoogle";
    }



    //    @PostMapping
//    public String login(@RequestParam("username") String username,
//                        @RequestParam("password") String password,
//                        @RequestParam("_csrfParameterName") String csrfTokenForm,
//                        HttpServletResponse response,
//                        HttpSession session,
//                        Model model) {
//        String accountName = username;
////        System.out.println("Da vao ham post logging");
//        if(!containsAllowedCharacters(accountName) || !containsAllowedCharacters(password)){
//            String message = "Username and password can only contain lowercase letters, and the characters (), @.";
//            model.addAttribute("errorMessage", message);
////            throw new InputNotInvalidException("Tên tài khoản, mật khẩu chỉ được chứa các ký tự thường và dấu (), @");
//            return "redirect:/login";
//        }
////        System.out.println("Sau khi nhan nut dang ky thi csrf token la: " + csrfToken);
//        if (!csrfTokenForm.equals(csrfToken)) {
//            String message = "Anti-CSRF token is not correct!";
//            model.addAttribute("errorMessage", message);
//            return "redirect:/login";
//        }
//        try {
//            AuthenticateResponse authenticateResponse = authenticationService.authenticate(accountName, password, session);
//            session.setAttribute("user", authenticateResponse.getUser());
//            Cookie cookie = new Cookie("fourleavesshoestoken", authenticateResponse.getAccessToken());
//            cookie.setHttpOnly(true);
//            cookie.setPath("/"); // This makes the cookie valid for all routes on your domain
//            response.addCookie(cookie);
//            Cart cart = cartService.getCurrentCartByUserId(authenticateResponse.getUser().getId());
//            GuestCart guestCart = (GuestCart) session.getAttribute("guestCart");
//            if (guestCart != null) {
//                cart = cartService.convertGuestCartToCart(guestCart,  authenticateResponse.getUser());
//                session.removeAttribute("guestCart");
//            }
//            session.setAttribute("cart", cart);
//            return "redirect:/";
//        } catch (AuthenticationAccountException exception) {
//            exception.printStackTrace();
//            model.addAttribute("message", "Tên tài khoản hoặc mật khẩu không đúng!");
//            String _csrfToken = generateRandomString();
//            csrfToken = _csrfToken;
//            model.addAttribute("_csrfToken", _csrfToken);
//        } catch (Exception err){
//            model.addAttribute("message", "Tên tài khoản hoặc mật khẩu không đúng!");
//            String _csrfToken = generateRandomString();
//            csrfToken = _csrfToken;
//            model.addAttribute("_csrfToken", _csrfToken);
//        }
//        return "user/boot1/login";
//    }

}
