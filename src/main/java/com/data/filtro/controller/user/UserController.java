package com.data.filtro.controller.user;

import com.data.filtro.exception.AuthenticationAccountException;
import com.data.filtro.model.Account;
import com.data.filtro.model.Order;
import com.data.filtro.model.User;
import com.data.filtro.model.payment.OrderStatus;
import com.data.filtro.service.AccountService;
import com.data.filtro.service.OrderService;
import com.data.filtro.service.UserService;
import jakarta.servlet.http.HttpSession;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;


    @Autowired
    OrderService orderService;


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
//        System.out.println("user id: " + user.getId() + " " + user.getName());
        List<Order> orderList;
        try {
            orderList = orderService.getOrderByUserId(user.getId());
        } catch (Exception e){
            orderList = new ArrayList<>();
        }
        List<OrderStatus> orderStatusList = returnListOrderStatus();
        //orderList.forEach(s -> System.out.println(s.getId()));
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
        }
        return "user/boot1/user-security";
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
