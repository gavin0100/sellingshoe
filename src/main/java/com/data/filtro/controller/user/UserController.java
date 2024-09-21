package com.data.filtro.controller.user;

import com.data.filtro.exception.AuthenticationAccountException;
import com.data.filtro.exception.PasswordDoNotMatchException;
import com.data.filtro.model.*;
import com.data.filtro.model.payment.OrderStatus;
import com.data.filtro.service.AuthenticationService;
import com.data.filtro.service.CartService;
import com.data.filtro.service.OrderService;
import com.data.filtro.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;


    @Autowired
    OrderService orderService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    CartService cartService;


    @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF', 'USER') and hasAnyAuthority('VIEW_USER', 'FULL_ACCESS_USER')")
    public String show() {
        String out = "<h1>!!</h1>";
        return out;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF', 'USER') and hasAnyAuthority('VIEW_USER', 'FULL_ACCESS_USER')")
    public String showProfile(HttpSession session, Model model) {
        User temp = (User) session.getAttribute("user");
        if (temp == null){
            model.addAttribute("message", "Please Login");
            return "user/boot1/user-profile";
        }
        User user = userService.getByUserId(temp.getId());
        model.addAttribute("user", user);
        return "user/boot1/user-profile";
    }

    @PostMapping("/profile/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF', 'USER') and hasAnyAuthority('VIEW_USER', 'FULL_ACCESS_USER')")
    public String processProfile(@PathVariable("id") int id, @ModelAttribute("user") User updatedUser, HttpSession session, Model model) {
        try {
            userService.updateUser(updatedUser);
            session.setAttribute("user", updatedUser);
            model.addAttribute("user", updatedUser);
            model.addAttribute("message", "Cập nhật thông tin thành công!");
        } catch (NotFoundException | ParseException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/user/profile";
    }


    @GetMapping("/billing")
    @PreAuthorize("hasAnyAuthority('FULL_ACCESS_PLACE_ORDER')")
    public String showBilling(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null){
            model.addAttribute("message", "Please Login");
            return "user/boot1/user-billing";
        }
        List<Order> orderList;
        try {
            orderList = orderService.getOrderByUserId(user.getId());
            Collections.reverse(orderList);
        } catch (Exception e){
            orderList = new ArrayList<>();
        }
        List<OrderStatus> orderStatusList = returnListOrderStatus();

        model.addAttribute("orderList", orderList);
        model.addAttribute("orderStatusList", orderStatusList);
        return "user/boot1/user-billing";
    }

    @GetMapping("/security")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF', 'USER') and hasAnyAuthority('VIEW_USER', 'FULL_ACCESS_USER')")
    public String showSecurity(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null){
            model.addAttribute("message", "Please Login");
            return "user/boot1/user-security";
        }
        return "user/boot1/user-security";
    }


    @PostMapping("/security")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF', 'USER') and hasAnyAuthority('VIEW_USER', 'FULL_ACCESS_USER')")
    public String processSecurity(HttpSession session, Model model,
                                  @RequestParam("currentPassword") String currentPassword,
                                  @RequestParam("newPassword") String newPassword,
                                  @RequestParam("repeatNewPassword") String repeatNewPassword) {
        try {
            User user = (User) session.getAttribute("user");
            userService.changePassword(user, currentPassword, newPassword, repeatNewPassword);
            model.addAttribute("message", "Password changed successfully!");
        } catch (NotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
        } catch (AuthenticationAccountException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        } catch (PasswordDoNotMatchException pe){
            model.addAttribute("errorMessage", pe.getMessage());
        } catch (Exception ex){
            model.addAttribute("errorMessage", "Không thể đổi mật khẩu!");
        }
        return "user/boot1/user-security";
    }
    @GetMapping("/billing/reset_login")
    public String resetLogin(@RequestParam Map<String , String> params, HttpSession session, HttpServletResponse response){
        System.out.println("/billing/reset_login");
        String accountName = params.get("username");
        System.out.println("accountName trong vnpay reset login: " + accountName);
        AuthenticateResponse authenticateResponse = authenticationService.authenticateWithOnlyAccountName(accountName);
        session.setAttribute("user", authenticateResponse.getUser());
        Cookie cookie = new Cookie("fourleavesshoestoken", authenticateResponse.getAccessToken());
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
        return "redirect:/user/billing";
    }

    public List<OrderStatus> returnListOrderStatus(){
        List<OrderStatus> danhSachOrderStatus = new ArrayList<>();
        danhSachOrderStatus.add(OrderStatus.PENDING);
        danhSachOrderStatus.add(OrderStatus.PAID_MOMO);
        danhSachOrderStatus.add(OrderStatus.PAID_VNPAY);
        danhSachOrderStatus.add(OrderStatus.CONFIRMED);
        danhSachOrderStatus.add(OrderStatus.SHIPPING);
        danhSachOrderStatus.add(OrderStatus.DELIVERED);
        danhSachOrderStatus.add(OrderStatus.CANCELED);
        danhSachOrderStatus.add(OrderStatus.FAILED);
        return danhSachOrderStatus;
    }

}
