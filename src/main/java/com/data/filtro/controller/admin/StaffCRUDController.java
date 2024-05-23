package com.data.filtro.controller.admin;

import com.data.filtro.model.Account;
import com.data.filtro.model.Staff;
import com.data.filtro.model.User;
import com.data.filtro.service.AccountService;
import com.data.filtro.service.StaffService;
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
@RequestMapping("/admin/staff")
public class StaffCRUDController {

    @Autowired
    StaffService staffService;

    @Autowired
    UserService userService;


    public Pageable sortStaff(int currentPage, int pageSize, int sortType) {
        Pageable pageable;
        switch (sortType) {
            case 5, 10, 25, 50 -> pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("id"));
            default -> {
                pageSize = 5;
                pageable = PageRequest.of(currentPage - 1, pageSize);
            }
        }
        return pageable;
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_STAFF', 'VIEW_STAFF')")
    public String show(@RequestParam(defaultValue = "5") int sortType, @RequestParam("currentPage") Optional<Integer> page, Model model, HttpSession session) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        int currentPage = page.orElse(1);
        int pageSize = sortType;
        List<User> usableAccounts = userService.getEligibleAccountForStaff();
//        usableAccounts.forEach(st -> System.out.println(usableAccounts.isEmpty() ? "null" : st.getId()));
        Pageable pageable;
        Page<User> userPage;
        pageable = sortStaff(currentPage, pageSize, sortType);
        userPage = userService.getAllPagingStaff(pageable);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("sortType", sortType);
        model.addAttribute("totalElements", userPage.getTotalElements());
        model.addAttribute("usableAccounts", usableAccounts);
        return "admin/boot1/userStaff";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_STAFF')")
    public String create(@ModelAttribute Staff staff) {
        staffService.create(staff);
        return "redirect:/admin/staff";
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_STAFF')")
    public String update(@ModelAttribute Staff staff) {
        staffService.update(staff);
        return "redirect:/admin/staff";
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_STAFF')")
    public String delete(@RequestParam int id) {
        staffService.delete(id);
        return "redirect:/admin/staff";
    }

}
