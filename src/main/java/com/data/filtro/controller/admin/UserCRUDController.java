package com.data.filtro.controller.admin;

import com.data.filtro.model.Account;
import com.data.filtro.model.DTO.UserDTO;
import com.data.filtro.model.User;
import com.data.filtro.service.AccountService;
import com.data.filtro.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/user")
public class UserCRUDController {

    @Autowired
    UserService userService;


    public Pageable sortUser(int currentPage, int pageSize, int sortType) {
        Pageable pageable;
        switch (sortType) {
            case 5:
            case 10:
            case 25:
            case 50:
                pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("name"));
                break;
            default:
                pageSize = 5;
                pageable = PageRequest.of(currentPage - 1, pageSize);
                break;
        }
        return pageable;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_USER', 'VIEW_USER')")
    public String show(@RequestParam(defaultValue = "5") int sortType, @RequestParam("currentPage") Optional<Integer> page, Model model, HttpSession session) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        int currentPage = page.orElse(1);
        int pageSize = sortType;
        List<User> usableAccounts = userService.getAppropriateAccountForUser();
//        usableAccounts.forEach(st -> System.out.println(usableAccounts.isEmpty() ? "null" : st.getId()));
        System.out.println("HELLO!");
        Pageable pageable;
        Page<User> userPage;
        pageable = sortUser(currentPage, pageSize, sortType);
        userPage = userService.getAllPagingUser(pageable);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("sortType", sortType);
        model.addAttribute("totalElements", userPage.getTotalElements());
        model.addAttribute("usableAccounts", usableAccounts);
        return "admin/boot1/user";
    }


    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority( 'FULL_ACCESS_USER')")
    public String create(@ModelAttribute UserDTO user) {
        userService.create(user);
        return "redirect:/admin/user";
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority( 'FULL_ACCESS_USER')")
    public String update(@ModelAttribute UserDTO user) {
        userService.update(user);
        return "redirect:/admin/user";
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority( 'FULL_ACCESS_USER')")
    public String delete(@RequestParam("id") int id) {
        User user = userService.getByUserId(id);
        userService.deleteById(id);
        return "redirect:/admin/user";
    }
}
